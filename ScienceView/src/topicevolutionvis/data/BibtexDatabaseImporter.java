/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.data;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.BibtexEntryType;
import net.sf.jabref.imports.BibtexParser;
import topicevolutionvis.database.DatabaseCorpus;
import topicevolutionvis.database.SqlManager;
import topicevolutionvis.preprocessing.Ngram;
import topicevolutionvis.util.PExConstants;
import topicevolutionvis.wizard.DataSourceChoice;

/**
 *
 * @author Aretha
 */
public class BibtexDatabaseImporter extends DatabaseImporter {

    public BibtexDatabaseImporter(String filename, String collection, int nrGrams, DataSourceChoice view, boolean removeStopwordsByTagging) {
        super(filename, collection, nrGrams, view, removeStopwordsByTagging);
    }

    @Override
    protected Void doInBackground() throws Exception {
        HashMap<String, Integer> corpusNgrams = new HashMap<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            //checking if the collection nam already exist
            if (!DatabaseCorpus.uniqueName(collection)) {
                this.msg = "A collection intitled \"" + collection + "\" already exists. Please choose another name.";
                this.cancel(true);

            } else {
                this.dropIndexForBibliographicCoupling();
                stmt = SqlManager.getInstance().getSqlStatement("INSERT.COLLECTION", -1, -1);
                stmt.setString(1, collection);
                stmt.setString(2, filename);
                stmt.setInt(3, nrGrams);
                stmt.setString(4, "bib");
                stmt.executeUpdate();


                rs = stmt.getGeneratedKeys();
                rs.next();
                this.id_collection = rs.getInt(1);


                BibtexDatabase database = BibtexParser.parse(new FileReader(new File(this.filename))).getDatabase();
                BibtexEntry entry;
                int id_doc = 0;
                StringBuilder content;
                String[] pages;
                String begin_page, end_page, author, pagesField;
                for (Entry<String, BibtexEntry> e : database.getEntryMap().entrySet()) {
                    entry = e.getValue();

                    begin_page = null;
                    end_page = null;

                    content = new StringBuilder(entry.getField("title"));
                    content.append(" ").append(entry.getField("author"));
                    content.append(" ").append(entry.getField("abstract"));
                    content.append(" ").append(entry.getField("keywords"));
                    content.append(" ").append(entry.getField("references"));

                    int type = -1;
                    if (entry.getType() == BibtexEntryType.ARTICLE) {
                        type = PExConstants.JOURNAL_ARTICLE;
                    } else if (entry.getType() == BibtexEntryType.INPROCEEDINGS) {
                        type = PExConstants.CONFERENCE_PAPER;
                    } else if (entry.getType() == BibtexEntryType.BOOK || entry.getType() == BibtexEntryType.BOOKLET) {
                        type = PExConstants.BOOK;
                    } else if (entry.getType() == BibtexEntryType.INBOOK || entry.getType() == BibtexEntryType.INCOLLECTION) {
                        type = PExConstants.BOOK_CHAPTER;
                    } else if (entry.getType() == BibtexEntryType.TECHREPORT) {
                        type = PExConstants.REPORT;
                    } else if (entry.getType() == BibtexEntryType.PHDTHESIS || entry.getType() == BibtexEntryType.MASTERSTHESIS) {
                        type = PExConstants.THESIS;
                    } else if (entry.getType() == BibtexEntryType.MISC || entry.getType() == BibtexEntryType.MANUAL) {
                        type = PExConstants.GENERIC;
                    } else if (entry.getType() == BibtexEntryType.PHDTHESIS || entry.getType() == BibtexEntryType.MASTERSTHESIS) {
                        type = PExConstants.THESIS;
                    } else if (entry.getType() == BibtexEntryType.UNPUBLISHED) {
                        type = PExConstants.UNPUBLISHED;
                    }

                    ArrayList<Ngram> fngrams = this.getNgramsFromFile(content.toString());
                    pagesField = entry.getField("pages");
                    pages = null;
                    if (pagesField != null) {
                        pages = pagesField.replaceAll("--", "-").split("-");
                    }

                    if (pages != null) {
                        if (pages.length == 2) {
                            begin_page = pages[0];
                            end_page = pages[1];
                        } else if (pages.length == 1) {
                            begin_page = pages[0];
                        }
                    }
                    author = entry.getField("author");
                    if (author != null) {
                        author = author.replaceAll("and", "|");
                    }
                    this.saveToDataBase(id_doc, type, entry.getField("title"), null, author, entry.getField("abstract"), entry.getField("keywords"), null, entry.getField("references"), Integer.valueOf(entry.getField("year")), 0, entry.getField("doi"), begin_page, end_page, "", entry.getField("journal"), "", entry.getField("volume"), 0);
                    //creating the ngrams stream
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(fngrams);
                    oos.flush();


                    //inserting the ngrams
                    stmt = SqlManager.getInstance().getSqlStatement("UPDATE.NGRAMS.DOCUMENT", -1, -1);
                    stmt.setBytes(1, baos.toByteArray());
                    stmt.setInt(2, id_doc);
                    stmt.setInt(3, id_collection);

                    stmt.executeUpdate();
                    stmt.close();
                    id_doc++;

                    for (Ngram n : fngrams) {
                        if (corpusNgrams.containsKey(n.ngram)) {
                            corpusNgrams.put(n.ngram, corpusNgrams.get(n.ngram) + n.frequency);
                        } else {
                            corpusNgrams.put(n.ngram, n.frequency);
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

                stmt = SqlManager.getInstance().getSqlStatement("UPDATE.NGRAMS.COLLECTION", -1, -1);
                stmt.setBytes(1, baos.toByteArray());
                stmt.setInt(2, id_collection);
                stmt.executeUpdate();
                stmt.close();

                this.matchReferencesToPapers();
                this.createIndexForBibliographicCoupling();
            }
        } catch (IOException | SQLException | NumberFormatException ex) {
            Logger.getLogger(BibtexDatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(BibtexDatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(ISICorpusDatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }
}
