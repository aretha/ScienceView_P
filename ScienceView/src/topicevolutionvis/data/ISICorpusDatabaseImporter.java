package topicevolutionvis.data;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import topicevolutionvis.database.ConnectionManager;
import topicevolutionvis.database.SqlManager;
import topicevolutionvis.database.SqlUtil;
import topicevolutionvis.preprocessing.Ngram;
import topicevolutionvis.util.PExConstants;
import topicevolutionvis.wizard.DataSourceChoiceWizard;

/**
 *
 * @author Aretha
 */
public class ISICorpusDatabaseImporter extends DatabaseImporter
{
    private Pattern authorP = Pattern.compile("[a-zA-Z\\-\\s]{2,}, [A-Z][a-z]+");
    
    private Pattern authorPattern2 = Pattern.compile("[a-zA-Z\\-\\s]{2,}, [A-Z]+");
    
    private Pattern authorPattern3 = Pattern.compile("[a-zA-Z\\-\\s]{2,}, [[A-Z\\.][\\s]?]+");

	
	// TODO: Move this to JabRef
	public static final String FILE_EXTENSION = ".isi";

    public ISICorpusDatabaseImporter(String filename, String collection, int nrGrams, DataSourceChoiceWizard view, boolean removeStopwordsByTagging) {
        super(filename, collection, nrGrams, view, removeStopwordsByTagging);
    }

    @Override
    protected Void doInBackground()
    {
        // Check if the collection name already exist
        if (! collectionManager.isUnique(collection)) {
            this.cancel(true);
            this.msg = "A collection intitled \"" + collection + "\" already exists. Please choose another name.";
        }

        // Create the collection
        ConnectionManager connManager = ConnectionManager.getInstance();
        Connection conn = null;
        try {
        	conn = connManager.getConnection();

            dropIndexForBibliographicCoupling(conn);
            createISICollection(conn);
            readISIFile(conn);
            matchReferencesToPapers(conn);
            createIndexForBibliographicCoupling(conn);
        } catch (Exception e) {
        	throw new RuntimeException("Could not save collection to database", e);
        } finally {
        	SqlUtil.close(conn);
        }
        return null;
    }

    private void createISICollection(Connection conn) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
        	stmt = SqlManager.getInstance().getSqlStatement(conn, "INSERT.COLLECTION");
            stmt.setString(1, collection);
            stmt.setString(2, filename);
            stmt.setInt(3, nrGrams);
            stmt.setString(4, "isi");
            stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            rs.next();
            id_collection = rs.getInt(1);
        } catch (SQLException e) {
        	throw new RuntimeException("Could not create and initialize collection into database", e);
        } finally {
        	SqlUtil.close(rs);
        	SqlUtil.close(stmt);
        }
    	
    }
    
    private void readISIFile(Connection conn) {
        PreparedStatement stmt = null;

        String line = "";
        try {
            HashMap<String, Integer> corpusNgrams = new HashMap<String, Integer>();
            Integer index = -1, type = -1;
            String tag, title, authors, abs, author_keywords, keywords, references, doi, aux, journal, journal_abbrev, volume, begin_page, end_page, research_address;
            title = authors = research_address = doi = abs = keywords = author_keywords = references = journal = journal_abbrev = volume = begin_page = end_page = null;
            Integer year = 0, times_cited = 0;
            StringBuilder content = new StringBuilder();
            ArrayList<Ngram> fngrams;
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF8"));
            title = authors = research_address = doi = abs = author_keywords = keywords = references = journal = journal_abbrev = volume = begin_page = end_page = null;
            year = 0;
            times_cited = 0;
            index++;
            
            while ((line = in.readLine()) != null) {
          
            	try {
            		tag = line.substring(0, 2);
            	} catch (IndexOutOfBoundsException e) {
            		continue;
            	}
            	
                if (tag.compareTo("FN") == 0) {
                	continue;
                } else if (tag.compareTo("VR") == 0) {
                	continue;
                } else if (tag.compareTo("PT") == 0) {
                	type = -1;
                	aux = line.substring(3).trim();
                	if (aux.compareToIgnoreCase("J") == 0 || aux.compareToIgnoreCase("JOUR") == 0) {
                		type = PExConstants.JOURNAL_ARTICLE;
                	} else if (aux.compareToIgnoreCase("B") == 0) {
                        type = PExConstants.BOOK;
                    } else if (aux.compareToIgnoreCase("BOOK CHAPTER") == 0) {
                        type = PExConstants.BOOK_CHAPTER;
                    } else if (aux.compareToIgnoreCase("C") == 0 || aux.compareToIgnoreCase("CPAPER") == 0) {
                        type = PExConstants.CONFERENCE_PAPER;
                    } else if (aux.compareToIgnoreCase("GOVERNMENT REPORT") == 0) {
                        type = PExConstants.REPORT;
                    } else if (aux.compareToIgnoreCase("ENCYCLOPEDIA ENTRY") == 0) {
                    	type = PExConstants.ENCYCLOPEDIA_ENTRY;
                    } else if (aux.compareToIgnoreCase("NEWSLETTER") == 0) {
                        type = PExConstants.NEWSLETTER;
                    } else if (aux.compareToIgnoreCase("DICTIONARY ENTRY") == 0) {
                        type = PExConstants.DICTIONARY_ENTRY;
                    } else if (aux.compareToIgnoreCase("LECTURE") == 0) {
                        type = PExConstants.LECTURE;
                    }
                } else if (tag.compareTo("TI") == 0) {
                    title = multipleLines(in, line);
                    content = new StringBuilder(title);
                } else if (tag.compareTo("AU") == 0) {
                    authors = processAuthors(in, line);
                } else if (tag.compareTo("AB") == 0 | tag.compareTo("ABS") == 0) {
                    abs = multipleLines(in, line);
                    content = content.append(" ").append(abs);
                } else if (tag.compareTo("PY") == 0) {
                    year = Integer.valueOf(line.substring(3).trim());
                } else if (tag.compareTo("CR") == 0) {
                    references = processReferences(in, line);
                } else if (tag.compareTo("ID") == 0 || tag.compareTo("KW") == 0) {
                    keywords = multipleLines(in, line);
                    content = content.append(" ").append(keywords);
                } else if (tag.compareTo("TC") == 0) {
                    times_cited = Integer.valueOf(line.substring(3));
                } else if (tag.compareTo("VL") == 0) {
                    volume = line.substring(3);
                } else if (tag.compareTo("DI") == 0) {
                    doi = line.substring(3).trim();
                } else if (tag.compareTo("C1") == 0) {
                    research_address = this.multipleLinesWithDelimiter(in, line);
                } else if (tag.compareTo("SO") == 0 || tag.compareTo("JF") == 0) {
                    journal = line.substring(3).trim();
                } else if (tag.compareTo("J9") == 0) {
                    journal_abbrev = line.substring(3).trim();
                } else if (tag.compareTo("DE") == 0) {
                    author_keywords = multipleLines(in, line);
                    content = content.append(" ").append(author_keywords);
                } else if (tag.compareTo("BP") == 0) {
                    begin_page = line.substring(3);
                } else if (tag.compareTo("EP") == 0) {
                    end_page = line.substring(3);
                } else if (tag.compareTo("ER") == 0) {
                    saveToDataBase(conn, index, type, title, research_address, authors, abs, keywords, author_keywords, references, year, times_cited, doi, begin_page, end_page, "", journal, journal_abbrev, volume, 0);
                    fngrams = getNgramsFromFile(content.toString());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(fngrams);
                    oos.flush();

                    //inserting the ngrams
                    stmt = SqlManager.getInstance().getSqlStatement(conn, "UPDATE.NGRAMS.DOCUMENT");
                    stmt.setBytes(1, baos.toByteArray());
                    stmt.setInt(2, index);
                    stmt.setInt(3, id_collection);
                    stmt.executeUpdate();
                    stmt.close();
                    
                    for (Ngram n : fngrams) {
                        if (corpusNgrams.containsKey(n.ngram)) {
                            corpusNgrams.put(n.ngram, corpusNgrams.get(n.ngram) + n.frequency);
                        } else {
                            corpusNgrams.put(n.ngram, n.frequency);
                        }
                    }
                    title = authors = research_address = doi = abs = author_keywords = keywords = references = journal = journal_abbrev = volume = begin_page = end_page = null;
                    year = 0;
                    times_cited = 0;
                    index++;
                }
            }

            //add the ngrams to the collection
            ArrayList<Ngram> ngrams = new ArrayList<Ngram>();
            for (Entry<String, Integer> e : corpusNgrams.entrySet()) {
            	ngrams.add(new Ngram(e.getKey(), e.getValue()));
            }
            Collections.sort(ngrams);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(ngrams);
            oos.flush();

            stmt = SqlManager.getInstance().getSqlStatement(conn, "UPDATE.NGRAMS.COLLECTION");
            stmt.setBytes(1, baos.toByteArray());
            stmt.setInt(2, id_collection);
            stmt.executeUpdate();
            stmt.close();
        } catch (Exception e) {
        	throw new RuntimeException("Could not save collection to database", e);
        } finally {
        	SqlUtil.close(stmt);
    	}
    }

    private String processReferences(BufferedReader in, String line) {
        try {
            StringBuilder value;
            String aux;
            ArrayList<String> lines = new ArrayList<>();
            Matcher referenceMatcher;

            aux = line.substring(2).trim();
            if (aux.compareTo("") != 0) {
                lines.add(aux);
            }

            in.mark(300);
            String previous;
            while ((line = in.readLine().trim()) != null) {
                if (this.isiPattern.matcher(line).matches()) {
                    in.reset();
                    break;
                } else {
                    if (line.startsWith("10.")) {
                        previous = lines.get(lines.size() - 1);
                        if (previous.endsWith("DOI")) {
                            lines.set(lines.size() - 1, previous.concat(" " + line.trim()));
                        }
                        in.mark(300);
                    } else {
                        referenceMatcher = referencePattern.matcher(line);
                        if (referenceMatcher.find(0)) {
                            lines.add(line);
                        }
                        in.mark(300);
                    }
                }
            }
            value = new StringBuilder();
            for (int i = 0; i < lines.size() - 1; i++) {
                value.append(lines.get(i)).append("|");
            }
            if (!lines.isEmpty()) {
                value.append(lines.get(lines.size() - 1));
            }

            return value.toString();
        } catch (IOException e) {
        	throw new RuntimeException("Could not save references n to database", e);
        }
    }

    private String processAuthors(BufferedReader in, String line) {
        int index;
        line = line.substring(3).trim().replace(",", "");
        StringBuilder value = new StringBuilder(line);
        String author;
    	try {
            in.mark(1000);
            while (((line = in.readLine()) != null)) {
                if (! line.startsWith("   ")) {
                    in.reset();
                    break;
                } else {
                    author = line.trim();
                    if (authorP.matcher(author).matches()) {
                        index = author.indexOf(" ");
                        author = author.substring(0, index + 2).replace(",", "");
                    }
                    if (authorPattern2.matcher(author).matches()) {
                        author = author.replace(",", "");
                    }
                    if (authorPattern3.matcher(author).matches()) {
                        author = author.replace(". ", "").replace(".", "").replace(",", "");
                    }
                    value.append(" | ").append(author);
                    in.mark(1000);
                }
            }
        } catch (IOException e) {
        	throw new RuntimeException("Could not save split authors", e);
        }
        return value.toString();
    }

    private String multipleLinesWithDelimiter(BufferedReader in, String line) {
        try {
            if (line.contains("C1")) {
                line = line.substring(3).trim();
            }
            StringBuilder value = new StringBuilder(line);
            in.mark(1000);
            while (((line = in.readLine()) != null)) {
                if (this.isiPattern.matcher(line).matches()) {
                    in.reset();
                    break;
                } else {
                    value.append("|").append(line.trim());
                    in.mark(1000);
                }
            }
            return value.toString();
        } catch (IOException e) {
        	throw new RuntimeException("Could not split lines", e);
        }
    }
}
