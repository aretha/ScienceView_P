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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import topicevolutionvis.database.ConnectionManager;
import topicevolutionvis.database.H2ConnectionManager;
import topicevolutionvis.database.SqlManager;
import topicevolutionvis.database.SqlUtil;
import topicevolutionvis.preprocessing.Ngram;
import topicevolutionvis.util.PExConstants;
import topicevolutionvis.wizard.DataSourceChoiceWizard;

/**
 *
 * @author Aretha
 */
public class EndnoteDatabaseImporter extends DatabaseImporter {

    Pattern endnotePattern = Pattern.compile("%[A-Z0-9]\\s.*|.*:.*|Review|%».*|%∂.*|%©.*|%@.*|%+.*");

    public EndnoteDatabaseImporter(String filename, String collection, int nrGrams, DataSourceChoiceWizard view, boolean removeStopwordsByTagging) {
        super(filename, collection, nrGrams, view, removeStopwordsByTagging);
    }

    @Override
    protected Void doInBackground() throws Exception
    {
    	ConnectionManager connManager = H2ConnectionManager.getInstance();
    	Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            //checking if the collection name already exist
            if (!collectionManager.isUnique(collection)) {
                this.cancel(true);
                this.msg = "A collection intitled \"" + collection + "\" already exists. Please choose another name.";
            }
            dropIndexForBibliographicCoupling(conn);
            //creating the collection

            conn = connManager.getConnection();
            stmt = SqlManager.getInstance().getSqlStatement(conn, "INSERT.COLLECTION", -1, -1);
            stmt.setString(1, collection);
            stmt.setString(2, filename);
            stmt.setInt(3, nrGrams);
            stmt.setString(4, "enw");
            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();
            rs.next();
            this.id_collection = rs.getInt(1);
            rs.close();
            stmt.close();

            readEndnoteFile();
            matchReferencesToPapers(conn);
            createIndexForBibliographicCoupling(conn);
        } catch (SQLException ex) {
            Logger.getLogger(ISICorpusDatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
        	SqlUtil.close(rs);
        	SqlUtil.close(stmt);
        	SqlUtil.close(conn);
        }
        return null;
    }

    private void readEndnoteFile() {
    	ConnectionManager connManager = H2ConnectionManager.getInstance();
    	Connection conn = null;

        BufferedReader in = null;
        PreparedStatement stmt = null;
        try {
            int index = -1;
            String[] pages;
            String line, tag, title, abs, keywords, references, classId, journal, journal_abbrev, type, volume, doi, research_address, aux;
            Integer year, times_cited;
            StringBuilder content = null;
            EndnoteEntry entry = null;
            ArrayList<Ngram> fngrams;
            ArrayList<EndnoteEntry> entries = new ArrayList<>();
            HashMap<String, Integer> corpusNgrams = new HashMap<>();

            conn = connManager.getConnection();
            in = new BufferedReader(new FileReader(this.filename));
            while (((line = in.readLine()) != null)) {
                if (line.length() > 1 && Character.isIdentifierIgnorable(line.charAt(0))) {
                    line = line.substring(1);
                }
                if (this.endnotePattern.matcher(line).matches()) {
                    tag = line.substring(0, 2);
                    if (tag.compareTo("%0") == 0) {
                        index++;
                        entry = new EndnoteEntry();
                        entries.add(entry);
                        type = line.substring(3).trim();
                        if (type.compareToIgnoreCase("Journal Article") == 0) {
                            entry.setType(PExConstants.JOURNAL_ARTICLE);
                        } else if (type.compareToIgnoreCase("Conference Paper") == 0) {
                            entry.setType(PExConstants.CONFERENCE_PAPER);
                        } else if (type.compareToIgnoreCase("Thesis") == 0) {
                            entry.setType(PExConstants.THESIS);
                        } else if (type.compareToIgnoreCase("Report") == 0) {
                            entry.setType(PExConstants.REPORT);
                        } else if (type.compareToIgnoreCase("Conference Proceedings") == 0) {
                            entry.setType(PExConstants.CONFERENCE_PROCEEDINGS);
                        } else if (type.compareToIgnoreCase("Book Section") == 0) {
                            entry.setType(PExConstants.BOOK_CHAPTER);
                        } else if (type.compareToIgnoreCase("Book") == 0) {
                            entry.setType(PExConstants.BOOK);
                        } else if (type.compareToIgnoreCase("Generic") == 0) {
                            entry.setType(PExConstants.GENERIC);
                        } else if (type.compareToIgnoreCase("Unpublished Work") == 0) {
                            entry.setType(PExConstants.UNPUBLISHED);
                        }
                        entry.setId(index);
                        content = new StringBuilder("");
                        entry.setContent(content);
                    } else if (tag.compareTo("%A") == 0) { //author
                        entry.addAuthor(line.substring(3).trim());
                    } else if (tag.compareTo("%T") == 0) { //title
                        title = multipleLines(in, line);
                        entry.setTitle(title);
                        content.append(" ").append(title);
                    } else if (tag.compareTo("%J") == 0) { //journal
                        journal = multipleLines(in, line);
                        entry.setJournal(journal);
                    } else if (tag.compareTo("%O") == 0) {
                        journal_abbrev = multipleLines(in, line).replaceAll("\\.", "").toUpperCase();
                        entry.setJournalAbbrev(journal_abbrev);
                    } else if (tag.compareTo("%V") == 0) { //volume
                        volume = line.substring(3).trim();
                        entry.setVolume(volume);
                    } else if (tag.compareTo("%D") == 0) {
                        year = Integer.valueOf(line.substring(3).trim());
                        entry.setYear(year);
                    } else if (tag.compareTo("%X") == 0) { //abstract
                        abs = multipleLines(in, line);
                        entry.setAbstract(abs);
                        content.append(" ").append(abs);
                    } else if (tag.compareTo("%R") == 0) { //DOI
                        aux = line.substring(3).trim();
                        if (aux.startsWith("10.")) {
                            doi = aux;
                            entry.setDoi(doi);
                        }
                    } else if (tag.compareTo("%K") == 0) { //keywords
                        keywords = this.multipleLinesArray(in, line, true);
                        entry.setKeywords(keywords);
                        content.append(" ").append(keywords);
                    } else if (tag.compareTo("%P") == 0) { //pages
                        line = line.substring(3).trim();
                        pages = line.split("-");
                        if (pages.length == 2) {
                            entry.setBeginPage(pages[0]);
                            entry.setEndPage(pages[1]);
                        } else if (pages.length == 1) {
                            entry.setBeginPage(pages[0]);
                        }
                    } else if (tag.compareTo("%+") == 0) { //research address
                        research_address = multipleLines(in, line);
                        entry.setResearchAddress(research_address);
                    } else if (line.indexOf(":") != -1 && line.substring(0, line.indexOf(":")).compareTo("Times Cited") == 0) {
                        times_cited = Integer.valueOf(line.substring(line.indexOf(":") + 1).trim());
                        entry.setTimesCited(times_cited);
                    } else if (line.indexOf(":") != -1 && line.substring(0, line.indexOf(":")).compareTo("Cited References") == 0) {
                        references = this.processReferences(in, line);
                        entry.setReferences(references);
                    } else if (tag.compareTo("%»") == 0) {
                        classId = line.substring(2).trim();
                        if (classId.compareToIgnoreCase("Yes") == 0) {
                            entry.setTrueLblWork(1);
                        } else if (classId.compareToIgnoreCase("No") == 0 || classId.compareToIgnoreCase("No?") == 0) {
                            entry.setTrueLblWork(2);
                        } else {
                            entry.setTrueLblWork(3);
                        }
                    }
                }
            }
            index = -1;
            for (EndnoteEntry e : entries) {
                if (e.getTrueLblWork() == 1) {
                    index++;
                    System.out.println(index);
                    e.setId(index);
                    saveToDataBase(conn, e.getId(), e.getType(), e.getTitle(), e.getResearchAddress(), e.getAuthors(), e.getAbstract(), e.getKeywords(), null, e.getReferences(), e.getYear(), e.getTimesCited(), e.getDoi(), e.getBeginPage(), e.getEndPage(), "", e.getJournal(), e.getJournalAbbrev(), e.getVolume(), e.getTrueLblWork());

                    //creating the ngrams stream
                    fngrams = this.getNgramsFromFileRemovingStopwordsByTagging(e.getContent().toString());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(fngrams);
                    oos.flush();

                    //inserting the ngrams
                    stmt = SqlManager.getInstance().getSqlStatement(conn, "UPDATE.NGRAMS.DOCUMENT", -1, -1);
                    stmt.setBytes(1, baos.toByteArray());
                    stmt.setInt(2, e.getId());
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
                }
            }

            //add the ngrams to the collection
            ArrayList<Ngram> ngrams = new ArrayList<>();
            for (Entry<String, Integer> e : corpusNgrams.entrySet()) {
                ngrams.add(new Ngram(e.getKey(), e.getValue()));
            }
            Collections.sort(ngrams);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(ngrams);
            oos.flush();


            stmt = SqlManager.getInstance().getSqlStatement(conn, "UPDATE.NGRAMS.COLLECTION", -1, -1);
            stmt.setBytes(1, baos.toByteArray());
            stmt.setInt(2, id_collection);
            stmt.executeUpdate();
            stmt.close();


        } catch (IOException | NumberFormatException | SQLException ex) {
            Logger.getLogger(EndnoteDatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ex) {
                }
            }

        	if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                }
            }

        }
    }

    @Override
    public String multipleLines(BufferedReader in, String line) {
        try {
            StringBuilder content = new StringBuilder(line.substring(3).trim());
            in.mark(1000);

            while (((line = in.readLine()) != null)) {
                if (this.endnotePattern.matcher(line).matches()) {
                    in.reset();
                    break;
                } else {
                    content = content.append(" ").append(line.trim());
                    in.mark(1000);
                }
            }
            return content.toString();
        } catch (IOException ex) {
            Logger.getLogger(DatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private String processReferences(BufferedReader in, String line) {
        try {
            StringBuilder value;
            String aux;
            ArrayList<String> lines = new ArrayList<>();

            aux = line.substring(line.indexOf(":") + 1).trim();
            if (aux.compareTo("") != 0) {
                lines.add(aux);
            }

            in.mark(8000);
            String previous;
            while ((line = in.readLine().trim()) != null) {
                if (this.endnotePattern.matcher(line).matches()) {
                    in.reset();
                    break;
                } else {
                    if (line.startsWith("10.")) {
                        previous = lines.get(lines.size() - 1);
                        if (previous.endsWith("DOI")) {
                            lines.set(lines.size() - 1, previous.concat(" " + line.trim()));
                        }
                    } else {
//                        referenceMatcher = referencePattern.matcher(line);
//                        if (referenceMatcher.find(0)) {
                        lines.add(line);
                        in.mark(8000);
//                        } else{
//                         //   System.out.println("** "+line);
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
        } catch (IOException ex) {
            Logger.getLogger(EndnoteDatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private String multipleLinesArray(BufferedReader in, String line, boolean normal_field) {
        try {
            StringBuilder value;
            String aux;
            ArrayList<String> lines = new ArrayList<>();
            if (normal_field) {
                aux = line.substring(3).trim();
                if (aux.compareTo("") != 0) {
                    lines.add(line.substring(3).trim());
                }
            } else {
                aux = line.substring(line.indexOf(":") + 1).trim();
                if (aux.compareTo("") != 0) {
                    lines.add(line.substring(line.indexOf(":") + 1).trim());
                }
            }
            in.mark(8000);
            while ((line = in.readLine()) != null) {
                if (this.endnotePattern.matcher(line).matches()) {
                    in.reset();
                    break;
                } else {
                    lines.add(line.trim());
                    in.mark(8000);
                }
            }
            value = new StringBuilder();
            for (int i = 0; i < lines.size() - 1; i++) {
                value.append(lines.get(i)).append(" | ");
            }
            value.append(lines.get(lines.size() - 1));

            return value.toString();
        } catch (IOException ex) {
            Logger.getLogger(EndnoteDatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
