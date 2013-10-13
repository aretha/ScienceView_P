/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import topicevolutionvis.database.ConnectionManager;
import topicevolutionvis.database.SqlManager;
import topicevolutionvis.preprocessing.Ngram;
import topicevolutionvis.wizard.DataSourceChoice;

/**
 *
 * @authorReference Aretha
 */
public abstract class DatabaseImporter extends SwingWorker<Void, Void> {

    protected String collection, filename, msg = "";
    protected int nrGrams, id_collection;
    protected boolean removeStopwordsByTagging;
    protected DataSourceChoice view = null;
    Pattern referencePattern = Pattern.compile("\\*?([\\w\\s-\\.'()]{2,50})(,\\s(\\d{4}))?,(\\s(UNPUB|INPRESS))?\\s([\\w\\d\\s-\\.\\+\\&():\\d]+){1}(,\\s?[Vv]([\\w\\d-]+))?(,\\sCH([\\d\\w]+))?(,\\s(\\w([\\w\\d]+)))?(,\\sUNSP\\s[\\d\\w\\-/\\.()]+|,\\sARTN\\s([\\d\\w\\.]+))?(,\\sDOI\\s(.{5,100}))?");
    Pattern isiPattern = Pattern.compile("FN\\s.*|VR\\s.*\\s*|PT\\s.*\\s*|AU\\s.*\\s*|AF\\s.*\\s*|ED\\s.*\\s*|C1\\s.*\\s*|TI\\s.*\\s*|RID\\s.*\\s*|SO\\s.*\\s*|LA\\s.*\\s*|DI\\s.*\\s*|DT\\s.*\\s*|NR\\s.*\\s*|SN\\s.*\\s*|PU\\s.*\\s*|C1\\s.*\\s*|DE\\s.*\\s*|ID\\s.*\\s*|AB\\s.*\\s*|CR\\s.*\\s*|TC\\s.*\\s*|BP\\s.*\\s*|EP\\s.*\\s*|PG\\s.*\\s*|JI\\s.*\\s*|SE\\s.*\\s*|BS\\s.*\\s*|PY\\s.*\\s*|CY\\s.*\\s*|PD\\s.*\\s*|VL\\s.*\\s*|IS\\s.*\\s*|PN\\s.*\\s*|SU\\s.*\\s*|SI\\s.*\\s*|GA\\s.*\\s*|PI\\s.*\\s*|WP\\s.*\\s*|RP\\s.*\\s*|CP\\s.*\\s*|J9\\s.*\\s*|PA\\s.*\\s*|UT\\s.*\\s*|MH\\s.*\\s*|SS\\s.*\\s*|JC\\s.*\\s*|PS\\s.*\\s?\\s*|RC\\s.*\\s?\\s*|SC\\s.*\\s?\\s*|PE\\s.*\\s?\\s*|ER\\s?\\s*|EF\\s?");

    public DatabaseImporter(String filename, String collection, int nrGrams, DataSourceChoice view, boolean removeStopwordsByTagging) {
        this.filename = filename;
        this.collection = collection;
        this.nrGrams = nrGrams;
        this.removeStopwordsByTagging = removeStopwordsByTagging;
        this.view = view;
    }

    @Override
    public void done() {
        try {
            if (!this.isCancelled()) {
                view.setStatus("Finished", false);
            } else {
                view.setStatus("", false);
                JOptionPane.showMessageDialog(view, this.msg, "Warning", JOptionPane.WARNING_MESSAGE);
            }
            view.finishedLoadingCollection(collection, this.isCancelled());
            //      printDatabase();
        } catch (Exception ex) {
            Logger.getLogger(DatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void matchReferencesToPapers() {
        PreparedStatement stmt = null, stmt2;
        ResultSet result;
        try {
            stmt = SqlManager.getInstance().getSqlStatement("MATCH.CORE.REFERENCES", -1, -1);
            stmt.setInt(1, id_collection);
            stmt.setInt(2, id_collection);
            stmt.setInt(3, id_collection);
            stmt.setInt(4, id_collection);
            result = stmt.executeQuery();
            int id_doc, id_ref;
            while (result.next()) {
                id_doc = result.getInt(1);
                id_ref = result.getInt(2);

                stmt2 = SqlManager.getInstance().getSqlStatement("UPDATE.REFERENCE", -1, -1);
                stmt2.setInt(1, id_doc);
                stmt2.setInt(2, id_ref);
                stmt2.setInt(3, id_collection);
                stmt2.executeUpdate();
                stmt2.close();
            }
            stmt.close();
            result.close();

        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

//    private int getNewReferenceId() {
//        PreparedStatement stmt = null;
//        ResultSet rs = null;
//
//        try {
//            stmt = SqlManager.getInstance().getSqlStatement("SELECT.REFERENCES.ID");
//            stmt.setInt(1, this.id_collection);
//            rs = stmt.executeQuery();
//
//            if (rs.next()) {
//                return rs.getInt(1) + 1;
//            } else {
//                return 0;
//            }
//        } catch (Exception ex) {
//            Logger.getLogger(DatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            try {
//                if (stmt != null) {
//                    stmt.close();
//                }
//                if (rs != null) {
//                    rs.close();
//                }
//                //closing the data base connection
//            } catch (SQLException ex) {
//                Logger.getLogger(DatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        return -1;
//    }
    public int getNumberOfReferences() {
        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            stmt = SqlManager.getInstance().getSqlStatement("SELECT.NUMBER.OF.REFERENCES", -1, -1);
            stmt.setInt(1, id_collection);
            result = stmt.executeQuery();
            result.next();
            int number = result.getInt(1);
            result.close();
            stmt.close();
            return number;
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return 0;
    }

    public synchronized void saveToDataBase(int id, int type, String title, String research_address, String authors, String abs, String keywords, String authors_keywords, String references, int year, int times_cited, String doi, String begin_page, String end_page, String pdf_file, String journal, String journal_abbrev, String volume, int classId) {
        PreparedStatement stmt = null;
        try {
            //inserting the content
            stmt = SqlManager.getInstance().getSqlStatement("INSERT.CONTENT", -1, -1);
            stmt.setInt(1, id);
            stmt.setInt(2, id_collection);
            stmt.setInt(3, type);
            stmt.setString(4, title.substring(0, 1) + title.substring(1).toLowerCase(Locale.ENGLISH));
            stmt.setString(5, research_address);
            stmt.setString(6, abs);
            stmt.setString(7, keywords);
            stmt.setString(8, authors_keywords);
            stmt.setInt(9, year);
            stmt.setInt(10, times_cited);
            stmt.setString(11, doi);
            stmt.setString(12, begin_page);
            stmt.setString(13, end_page);
            stmt.setString(14, pdf_file);
            stmt.setString(15, journal);
            stmt.setString(16, journal_abbrev);
            stmt.setString(17, volume);
            stmt.setInt(18, classId);
            stmt.executeUpdate();
            stmt.close();
            if (authors != null) {
                this.saveAuthors(id, authors);
            }

            this.saveReferences(id, references);

        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void saveAuthors(int id, String authors) {
        PreparedStatement stmt = null;
        ResultSet result;
        StringTokenizer authorsTokenizer = new StringTokenizer(authors, "|");
        String author;
        int author_order = 0, index_author;

        try {
            while (authorsTokenizer.hasMoreTokens()) {
                author = authorsTokenizer.nextToken().trim().toUpperCase();
                stmt = SqlManager.getInstance().getSqlStatement("SELECT.SAME.AUTHOR", -1, -1);
                stmt.setString(1, author);
                result = stmt.executeQuery();
                author_order++;
                if (result.next()) {
                    index_author = result.getInt(1);
                    stmt = SqlManager.getInstance().getSqlStatement("INSERT.DOCUMENT.TO.AUTHOR", -1, -1);
                    stmt.setInt(1, id);
                    stmt.setInt(2, id_collection);
                    stmt.setInt(3, index_author);
                    stmt.setInt(4, author_order);
                    stmt.executeUpdate();
                    stmt.close();
                } else {
                    stmt = SqlManager.getInstance().getSqlStatement("INSERT.AUTHOR", -1, -1);
                    stmt.setString(1, author);
                    stmt.setInt(2, id_collection);
                    stmt.executeUpdate();
                    result = stmt.getGeneratedKeys();
                    result.next();
                    index_author = result.getInt(1);
                    stmt.close();

                    stmt = SqlManager.getInstance().getSqlStatement("INSERT.DOCUMENT.TO.AUTHOR", -1, -1);
                    stmt.setInt(1, id);
                    stmt.setInt(2, id_collection);
                    stmt.setInt(3, index_author);
                    stmt.setInt(4, author_order);
                    stmt.executeUpdate();
                    stmt.close();
                }
                result.close();
                stmt.close();

            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void saveReferences(int id, String references) {
        PreparedStatement stmt = null;
        ResultSet result;
        int id_author, id_ref = 0;
        String reference = null, aux;
        Matcher referenceMatcher;
        try {
            if ((references != null) && (references.contains("|"))) {
                StringTokenizer referencesTokenizer = new StringTokenizer(references, "|");
                String authorReference, doiReference, journalReference, volumeReference, pagesReference, typeReference, chapterReference, artnReference;

                int yearReference;
                while (referencesTokenizer.hasMoreTokens()) {
                    reference = referencesTokenizer.nextToken().trim();
                    referenceMatcher = referencePattern.matcher(reference);
                    if (referenceMatcher.matches()) {
                        id_ref = -1;
                        yearReference = -1;
                        journalReference = volumeReference = doiReference = pagesReference = chapterReference = artnReference = null;
                        typeReference = "CONF";

                        //splitting the reference
                        aux = referenceMatcher.group(1);
                        if (aux != null && !aux.isEmpty()) {
                            authorReference = aux.trim().replace(". ", "").replace(".", "").toUpperCase();
                            //trying to find out if the author of this reference is already on the authors table
                            stmt = SqlManager.getInstance().getSqlStatement("SELECT.SAME.AUTHOR", -1, -1);
                            stmt.setString(1, authorReference);
                            result = stmt.executeQuery();
                            if (result.next()) {
                                id_author = result.getInt(1);
                            } else {
                                stmt = SqlManager.getInstance().getSqlStatement("INSERT.AUTHOR", -1, -1);
                                stmt.setString(1, authorReference);
                                stmt.setInt(2, id_collection);
                                stmt.executeUpdate();
                                result = stmt.getGeneratedKeys();
                                result.next();
                                id_author = result.getInt(1);
                                result.close();
                                stmt.close();
                            }


                        } else {
                            id_author = -1;
                        }

                        aux = referenceMatcher.group(10);
                        if (aux != null && !aux.isEmpty()) {
                            chapterReference = aux;
                        }

                        aux = referenceMatcher.group(6).trim();
                        if (aux != null && !aux.isEmpty()) {
                            journalReference = aux;
                        }

                        aux = referenceMatcher.group(3);
                        if (aux != null && !aux.isEmpty()) {
                            yearReference = Integer.valueOf(aux);
                        }

                        aux = referenceMatcher.group(8);
                        if (aux != null && !aux.isEmpty()) {
                            volumeReference = aux;
                        }

                        aux = referenceMatcher.group(12);
                        if (aux != null && !aux.isEmpty()) {
                            if (aux.startsWith("p") || aux.startsWith("P")) {
                                pagesReference = aux.substring(1);
                            } else {
                                pagesReference = aux;
                            }
                        }

                        aux = referenceMatcher.group(17);
                        if (aux != null && !aux.isEmpty()) {
                            doiReference = aux;
                        }

                        aux = referenceMatcher.group(15);
                        if (aux != null && !aux.isEmpty()) {
                            artnReference = aux;
                        }

                        //descobrindo se esta referencia já apareceu antes na colecao
                        if (doiReference != null) {
                            stmt = SqlManager.getInstance().getSqlStatement("SELECT.CITATION.WITH.DOI", -1, -1);
                            stmt.setString(1, doiReference);
                            stmt.setInt(2, id_collection);
                            result = stmt.executeQuery();
                            if (result.next()) {
                                id_ref = result.getInt(1);
                            }
                            result.close();
                            stmt.close();
                        }

                        if (id_ref == -1) {
                            StringBuilder sqlStatement = new StringBuilder("SELECT id_citation FROM Citations Where");
                            sqlStatement.append(" type LIKE '").append(typeReference).append("'");
                            sqlStatement.append(" and id_author=").append(id_author);
                            sqlStatement.append(" and year=").append(yearReference);
                            if (volumeReference == null) {
                                sqlStatement.append(" and volume is null");
                            } else {
                                sqlStatement.append(" and volume='").append(volumeReference).append("'");
                            }
                            if (pagesReference == null) {
                                sqlStatement.append(" and pages is null");
                            } else {
                                sqlStatement.append(" and pages='").append(pagesReference).append("'");
                            }
                            if (journalReference == null) {
                                sqlStatement.append(" and journal is null");
                            } else {
                                sqlStatement.append(" and journal='").append(journalReference).append("'");
                            }

                            if (chapterReference == null) {
                                sqlStatement.append(" and chapter is null");
                            } else {
                                sqlStatement.append(" and chapter='").append(chapterReference).append("'");
                            }

                            if (artnReference == null) {
                                sqlStatement.append(" and artn is null");
                            } else {
                                sqlStatement.append(" and artn='").append(artnReference).append("'");
                            }

                            sqlStatement.append(" and id_collection=").append(this.id_collection);

                            stmt = ConnectionManager.getInstance().getConnection().prepareStatement(sqlStatement.toString());
                            result = stmt.executeQuery();

                            if (result.next()) {
                                id_ref = result.getInt(1);
                                stmt = SqlManager.getInstance().getSqlStatement("INSERT.DOCUMENT.TO.REFERENCE", -1, -1);
                                stmt.setInt(1, id);
                                stmt.setInt(2, id_collection);
                                stmt.setInt(3, id_ref);
                                stmt.executeUpdate();
                                result.close();
                                stmt.close();
                            } else {
                                //inserindo referencia na tabela de citacoes
                                stmt = SqlManager.getInstance().getSqlStatement("INSERT.REFERENCE", -1, -1);
                                stmt.setInt(1, id_collection);
                                stmt.setInt(2, id_author);
                                stmt.setString(3, typeReference);
                                stmt.setInt(4, yearReference);
                                stmt.setString(5, journalReference);
                                stmt.setString(6, volumeReference);
                                stmt.setString(7, chapterReference);
                                stmt.setString(8, doiReference);
                                stmt.setString(9, pagesReference);
                                stmt.setString(10, artnReference);
                                stmt.setString(11, reference);
                                stmt.setInt(12, -1);
                                stmt.executeUpdate();

                                result = stmt.getGeneratedKeys();
                                result.next();
                                id_ref = result.getInt(1);
                                result.close();
                                stmt.close();

                                stmt = SqlManager.getInstance().getSqlStatement("INSERT.DOCUMENT.TO.REFERENCE", -1, -1);
                                stmt.setInt(1, id);
                                stmt.setInt(2, id_collection);
                                stmt.setInt(3, id_ref);
                                stmt.executeUpdate();
                                stmt.close();
                            }
                        }
                    }
//                    else {
//                        System.out.println("[WRONG FORMAT] " + reference);
//                    }
                }
            }

        } catch (IOException | SQLException | NumberFormatException ex) {
            System.out.println("Documento: " + id);
            System.out.println("Referencia: " + id_ref);
            System.out.println("[REF DUPLICADA] " + reference);
//            Logger.getLogger(DatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void createIndexForBibliographicCoupling() {
        PreparedStatement stmt = null;
        try {
            stmt = SqlManager.getInstance().getSqlStatement("CREATE.INDEX.BC", -1, -1);
            stmt.executeUpdate();
            stmt.close();
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void dropIndexForBibliographicCoupling() {
        PreparedStatement stmt = null;
        try {
            stmt = SqlManager.getInstance().getSqlStatement("DROP.INDEX.BC", -1, -1);
            stmt.executeUpdate();
            stmt.close();
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public ArrayList<Ngram> getNgramsFromFile(String content) {
        if (this.removeStopwordsByTagging) {
            return this.getNgramsFromFileRemovingStopwordsByTagging(content);
        } else {
            return this.getNgramsFromFileWithStopwordsListOnly(content);
        }
    }

    public ArrayList<Ngram> getNgramsFromFileRemovingStopwordsByTagging(String content) {
        HashMap<String, Integer> ngramsTable = new HashMap<>();
        InputStream modelIn = null, rules_POS;
        if (content != null) {
            try {

                modelIn = new FileInputStream("resources/en-token.bin");
                TokenizerModel model = new TokenizerModel(modelIn);
                Tokenizer tokenizer = new TokenizerME(model);
                String paras[] = tokenizer.tokenize(content);

                rules_POS = new FileInputStream("resources/en-pos-maxent.bin");
                POSModel modelPOS = new POSModel(rules_POS);
                POSTaggerME tagger = new POSTaggerME(modelPOS);
                String tags[] = tagger.tag(paras);
//                System.out.println(content);
//                System.out.println();
                ArrayList<String> words = new ArrayList<>();
                for (int i = 0; i < tags.length; i++) {
//                    System.out.print(paras[i] + "_" + tags[i] + " ");
                    if (tags[i].compareTo("CC") != 0 && tags[i].compareTo("CD") != 0
                            && tags[i].compareTo("DT") != 0 && tags[i].compareTo("EX") != 0
                            && tags[i].compareTo("IN") != 0 && tags[i].compareTo("JJR") != 0
                            && tags[i].compareTo("JJS") != 0 && tags[i].compareTo("LS") != 0
                            && tags[i].compareTo("MD") != 0 && tags[i].compareTo("PDT") != 0
                            && tags[i].compareTo("POS") != 0 && tags[i].compareTo("PRP") != 0
                            && tags[i].compareTo("PRP$") != 0 && tags[i].compareTo("RB") != 0
                            && tags[i].compareTo("RBR") != 0 && tags[i].compareTo("RBS") != 0
                            && tags[i].compareTo("RP") != 0 && tags[i].compareTo("SYM") != 0
                            && tags[i].compareTo("TO") != 0 && tags[i].compareTo("UH") != 0
                            && tags[i].compareTo("VB") != 0 && tags[i].compareTo("VBD") != 0
                            && tags[i].compareTo("VBG") != 0 && tags[i].compareTo("VBN") != 0
                            && tags[i].compareTo("VBP") != 0 && tags[i].compareTo("VBZ") != 0
                            && tags[i].compareTo("WDT") != 0 && tags[i].compareTo("WP") != 0
                            && tags[i].compareTo("WP$") != 0 && tags[i].compareTo("WRB") != 0) {
                        words.add(paras[i]);
                    }
                }
//                System.out.println();
//                System.out.println("----------------");
                //create the first ngram
                String[] ngram = new String[nrGrams];
                int i = 0, count = 0;
                while (count < nrGrams && i < words.size()) {
                    if (words.get(i).trim().length() > 0 && !words.get(i).matches("[\\p{Punct}\\p{Digit}]+|'s")) {
                        String word = words.get(i).toLowerCase();
                        if ((!DatabaseImporter.FixedStopword.isStopWord(word)) && word.trim().length() > 0) {
                            ngram[count] = word;
                            count++;
                        }
                    }
                    i++;
                }

                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < ngram.length - 1; j++) {
                    sb.append(ngram[j]).append("<>");
                }
                sb.append(ngram[ngram.length - 1]);

                //adding to the frequencies table
                ngramsTable.put(sb.toString(), 1);

                //creating the remaining ngrams
                String word;
                while (i < words.size()) {
                    if (words.get(i).trim().length() > 0 && !words.get(i).matches("[\\p{Punct}\\p{Digit}']+|'s")) {
                        word = words.get(i).toLowerCase();

                        if ((!DatabaseImporter.FixedStopword.isStopWord(word)) && word.trim().length() > 0) {
                            String ng = this.addNextWord(ngram, word);

                            //verify if the ngram already exist on the document
                            if (ngramsTable.containsKey(ng)) {
                                ngramsTable.put(ng, ngramsTable.get(ng) + 1);
                            } else {
                                ngramsTable.put(ng, 1);
                            }
                        }
                    }
                    i++;
                }
            } catch (IOException e) {
                Logger.getLogger(DatabaseImporter.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                if (modelIn != null) {
                    try {
                        modelIn.close();
                    } catch (IOException e) {
                    }
                }
            }
        }




        ArrayList<Ngram> ngrams = new ArrayList<>();
        for (Entry<String, Integer> entry : ngramsTable.entrySet()) {
            ngrams.add(new Ngram(entry.getKey(), entry.getValue()));
        }
        Collections.sort(ngrams);
        return ngrams;
    }

    public ArrayList<Ngram> getNgramsFromFileWithStopwordsListOnly(String content) {
        HashMap<String, Integer> ngramsTable = new HashMap<>();
        InputStream modelIn = null; //rules_POS = null;
        if (content != null) {
            try {

                modelIn = new FileInputStream("resources/en-token.bin");
                TokenizerModel model = new TokenizerModel(modelIn);
                Tokenizer tokenizer = new TokenizerME(model);
                String paras[] = tokenizer.tokenize(content);

                //create the first ngram
                String[] ngram = new String[nrGrams];
                int i = 0, count = 0;
                while (count < nrGrams && i < paras.length) {
                    if (paras[i].trim().length() > 0 && !paras[i].matches("[\\p{Punct}\\p{Digit}]+|'s")) {
                        String word = paras[i].toLowerCase();
                        if ((!DatabaseImporter.FixedStopword.isStopWord(word)) && word.trim().length() > 0) {
                            ngram[count] = word;
                            count++;
                        }
                    }
                    i++;
                }

                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < ngram.length - 1; j++) {
                    sb.append(ngram[j]).append("<>");
                }
                sb.append(ngram[ngram.length - 1]);

                //adding to the frequencies table
                ngramsTable.put(sb.toString(), 1);

                //creating the remaining ngrams
                String word;
                while (i < paras.length) {
                    if (paras[i].trim().length() > 0 && !paras[i].matches("[\\p{Punct}\\p{Digit}']+|'s")) {
                        word = paras[i].toLowerCase();

                        if ((!DatabaseImporter.FixedStopword.isStopWord(word)) && word.trim().length() > 0) {
                            String ng = this.addNextWord(ngram, word);

                            //verify if the ngram already exist on the document
                            if (ngramsTable.containsKey(ng)) {
                                ngramsTable.put(ng, ngramsTable.get(ng) + 1);
                            } else {
                                ngramsTable.put(ng, 1);
                            }
                        }
                    }
                    i++;
                }
            } catch (IOException e) {
                Logger.getLogger(DatabaseImporter.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                if (modelIn != null) {
                    try {
                        modelIn.close();
                    } catch (IOException e) {
                    }
                }
            }
        }




        ArrayList<Ngram> ngrams = new ArrayList<>();
        for (Entry<String, Integer> entry : ngramsTable.entrySet()) {
            ngrams.add(new Ngram(entry.getKey(), entry.getValue()));
        }
        Collections.sort(ngrams);
        return ngrams;
    }

    public String multipleLines(BufferedReader in, String line) {
        String aux = line;
        try {
            StringBuilder content = new StringBuilder(line.substring(3).trim());
            in.mark(10000);
            while ((line = in.readLine()) != null) {
                if (line.matches("[A-Z]{2}\\s.*|[A-Z]{1}[1-9]{1}\\s.*|ER")) {
                    in.reset();
                    break;
                } else {
                    content = content.append(" ").append(line.trim());
                    in.mark(10000);
                }
            }
            return content.toString();
        } catch (IOException ex) {
            System.out.println("RESET PROBLEM: " + aux);
            Logger.getLogger(DatabaseImporter.class.getName()).log(Level.SEVERE, null, ex);
        }


        return null;


    }

    private String addNextWord(String[] ngram, String word) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < ngram.length - 1; i++) {
            ngram[i] = ngram[i + 1];
            sb.append(ngram[i]).append("<>");
        }

        ngram[ngram.length - 1] = word;
        sb.append(word);

        return sb.toString();
    }

    public static class FixedStopword {

        private FixedStopword() {
            this.stopwords = new ArrayList<>();
            this.stopwords.add("a");
            this.stopwords.add("able");
            this.stopwords.add("about");
            this.stopwords.add("above");
            this.stopwords.add("according");
            this.stopwords.add("accordingly");
            this.stopwords.add("across");
            this.stopwords.add("actually");
            this.stopwords.add("after");
            this.stopwords.add("afterwards");
            this.stopwords.add("again");
            this.stopwords.add("against");
            this.stopwords.add("all");
            this.stopwords.add("allow");
            this.stopwords.add("allows");
            this.stopwords.add("almost");
            this.stopwords.add("alone");
            this.stopwords.add("along");
            this.stopwords.add("already");
            this.stopwords.add("also");
            this.stopwords.add("although");
            this.stopwords.add("always");
            this.stopwords.add("am");
            this.stopwords.add("among");
            this.stopwords.add("amongst");
            this.stopwords.add("an");
            this.stopwords.add("and");
            this.stopwords.add("another");
            this.stopwords.add("any");
            this.stopwords.add("anybody");
            this.stopwords.add("anyhow");
            this.stopwords.add("anyone");
            this.stopwords.add("anything");
            this.stopwords.add("anyway");
            this.stopwords.add("anyways");
            this.stopwords.add("anywhere");
            this.stopwords.add("apart");
            this.stopwords.add("appear");
            this.stopwords.add("appreciate");
            this.stopwords.add("appropriate");
            this.stopwords.add("are");
            this.stopwords.add("around");
            this.stopwords.add("as");
            this.stopwords.add("aside");
            this.stopwords.add("ask");
            this.stopwords.add("asking");
            this.stopwords.add("associated");
            this.stopwords.add("at");
            this.stopwords.add("available");
            this.stopwords.add("away");
            this.stopwords.add("awfully");
            this.stopwords.add("b");
            this.stopwords.add("be");
            this.stopwords.add("became");
            this.stopwords.add("because");
            this.stopwords.add("become");
            this.stopwords.add("becomes");
            this.stopwords.add("becoming");
            this.stopwords.add("been");
            this.stopwords.add("before");
            this.stopwords.add("beforehand");
            this.stopwords.add("behind");
            this.stopwords.add("being");
            this.stopwords.add("believe");
            this.stopwords.add("below");
            this.stopwords.add("beside");
            this.stopwords.add("besides");
            this.stopwords.add("best");
            this.stopwords.add("better");
            this.stopwords.add("between");
            this.stopwords.add("beyond");
            this.stopwords.add("both");
            this.stopwords.add("brief");
            this.stopwords.add("but");
            this.stopwords.add("by");
            this.stopwords.add("c");
            this.stopwords.add("came");
            this.stopwords.add("can");
            this.stopwords.add("cannot");
            this.stopwords.add("cant");
            this.stopwords.add("cause");
            this.stopwords.add("causes");
            this.stopwords.add("certain");
            this.stopwords.add("certainly");
            this.stopwords.add("changes");
            this.stopwords.add("clearly");
            this.stopwords.add("co");
            this.stopwords.add("com");
            this.stopwords.add("come");
            this.stopwords.add("comes");
            this.stopwords.add("concerning");
            this.stopwords.add("consequently");
            this.stopwords.add("consider");
            this.stopwords.add("considering");
            this.stopwords.add("contain");
            this.stopwords.add("containing");
            this.stopwords.add("contains");
            this.stopwords.add("corresponding");
            this.stopwords.add("could");
            this.stopwords.add("course");
            this.stopwords.add("currently");
            this.stopwords.add("d");
            this.stopwords.add("definitely");
            this.stopwords.add("described");
            this.stopwords.add("despite");
            this.stopwords.add("did");
            this.stopwords.add("different");
            this.stopwords.add("do");
            this.stopwords.add("does");
            this.stopwords.add("doing");
            this.stopwords.add("done");
            this.stopwords.add("down");
            this.stopwords.add("downwards");
            this.stopwords.add("during");
            this.stopwords.add("e");
            this.stopwords.add("each");
            this.stopwords.add("eds");
            this.stopwords.add("edu");
            this.stopwords.add("eg");
            this.stopwords.add("eight");
            this.stopwords.add("either");
            this.stopwords.add("else");
            this.stopwords.add("elsewhere");
            this.stopwords.add("enough");
            this.stopwords.add("entirely");
            this.stopwords.add("especially");
            this.stopwords.add("et");
            this.stopwords.add("etc");
            this.stopwords.add("even");
            this.stopwords.add("ever");
            this.stopwords.add("every");
            this.stopwords.add("everybody");
            this.stopwords.add("everyone");
            this.stopwords.add("everything");
            this.stopwords.add("everywhere");
            this.stopwords.add("ex");
            this.stopwords.add("exactly");
            this.stopwords.add("example");
            this.stopwords.add("except");
            this.stopwords.add("f");
            this.stopwords.add("far");
            this.stopwords.add("few");
            this.stopwords.add("fifth");
            this.stopwords.add("first");
            this.stopwords.add("five");
            this.stopwords.add("followed");
            this.stopwords.add("following");
            this.stopwords.add("follows");
            this.stopwords.add("for");
            this.stopwords.add("former");
            this.stopwords.add("formerly");
            this.stopwords.add("forth");
            this.stopwords.add("four");
            this.stopwords.add("from");
            this.stopwords.add("further");
            this.stopwords.add("furthermore");
            this.stopwords.add("g");
            this.stopwords.add("get");
            this.stopwords.add("gets");
            this.stopwords.add("getting");
            this.stopwords.add("given");
            this.stopwords.add("gives");
            this.stopwords.add("go");
            this.stopwords.add("goes");
            this.stopwords.add("going");
            this.stopwords.add("gone");
            this.stopwords.add("got");
            this.stopwords.add("gotten");
            this.stopwords.add("greetings");
            this.stopwords.add("h");
            this.stopwords.add("had");
            this.stopwords.add("happens");
            this.stopwords.add("hardly");
            this.stopwords.add("has");
            this.stopwords.add("have");
            this.stopwords.add("having");
            this.stopwords.add("he");
            this.stopwords.add("hello");
            this.stopwords.add("help");
            this.stopwords.add("hence");
            this.stopwords.add("her");
            this.stopwords.add("here");
            this.stopwords.add("hereafter");
            this.stopwords.add("hereby");
            this.stopwords.add("herein");
            this.stopwords.add("hereupon");
            this.stopwords.add("hers");
            this.stopwords.add("herself");
            this.stopwords.add("hi");
            this.stopwords.add("him");
            this.stopwords.add("himself");
            this.stopwords.add("his");
            this.stopwords.add("hither");
            this.stopwords.add("hopefully");
            this.stopwords.add("how");
            this.stopwords.add("howbeit");
            this.stopwords.add("however");
            this.stopwords.add("i");
            this.stopwords.add("ie");
            this.stopwords.add("if");
            this.stopwords.add("ignored");
            this.stopwords.add("immediate");
            this.stopwords.add("in");
            this.stopwords.add("inasmuch");
            this.stopwords.add("inc");
            this.stopwords.add("indeed");
            this.stopwords.add("indicate");
            this.stopwords.add("indicated");
            this.stopwords.add("indicates");
            this.stopwords.add("inner");
            this.stopwords.add("insofar");
            this.stopwords.add("instead");
            this.stopwords.add("into");
            this.stopwords.add("inward");
            this.stopwords.add("is");
            this.stopwords.add("it");
            this.stopwords.add("its");
            this.stopwords.add("itself");
            this.stopwords.add("j");
            this.stopwords.add("just");
            this.stopwords.add("k");
            this.stopwords.add("keep");
            this.stopwords.add("keeps");
            this.stopwords.add("kept");
            this.stopwords.add("know");
            this.stopwords.add("known");
            this.stopwords.add("knows");
            this.stopwords.add("l");
            this.stopwords.add("last");
            this.stopwords.add("lately");
            this.stopwords.add("later");
            this.stopwords.add("latter");
            this.stopwords.add("latterly");
            this.stopwords.add("least");
            this.stopwords.add("less");
            this.stopwords.add("lest");
            this.stopwords.add("let");
            this.stopwords.add("like");
            this.stopwords.add("liked");
            this.stopwords.add("likely");
            this.stopwords.add("little");
            this.stopwords.add("look");
            this.stopwords.add("looking");
            this.stopwords.add("looks");
            this.stopwords.add("ltd");
            this.stopwords.add("m");
            this.stopwords.add("mainly");
            this.stopwords.add("many");
            this.stopwords.add("may");
            this.stopwords.add("maybe");
            this.stopwords.add("me");
            this.stopwords.add("mean");
            this.stopwords.add("meanwhile");
            this.stopwords.add("merely");
            this.stopwords.add("might");
            this.stopwords.add("more");
            this.stopwords.add("moreover");
            this.stopwords.add("most");
            this.stopwords.add("mostly");
            this.stopwords.add("much");
            this.stopwords.add("must");
            this.stopwords.add("my");
            this.stopwords.add("myself");
            this.stopwords.add("n");
            this.stopwords.add("name");
            this.stopwords.add("namely");
            this.stopwords.add("nd");
            this.stopwords.add("near");
            this.stopwords.add("nearly");
            this.stopwords.add("necessary");
            this.stopwords.add("need");
            this.stopwords.add("needs");
            this.stopwords.add("neither");
            this.stopwords.add("never");
            this.stopwords.add("nevertheless");
            this.stopwords.add("new");
            this.stopwords.add("next");
            this.stopwords.add("nine");
            this.stopwords.add("no");
            this.stopwords.add("nobody");
            this.stopwords.add("non");
            this.stopwords.add("none");
            this.stopwords.add("noone");
            this.stopwords.add("nor");
            this.stopwords.add("normally");
            this.stopwords.add("not");
            this.stopwords.add("nothing");
            this.stopwords.add("novel");
            this.stopwords.add("now");
            this.stopwords.add("nowhere");
            this.stopwords.add("o");
            this.stopwords.add("obviously");
            this.stopwords.add("of");
            this.stopwords.add("off");
            this.stopwords.add("often");
            this.stopwords.add("oh");
            this.stopwords.add("ok");
            this.stopwords.add("okay");
            this.stopwords.add("old");
            this.stopwords.add("on");
            this.stopwords.add("once");
            this.stopwords.add("one");
            this.stopwords.add("ones");
            this.stopwords.add("only");
            this.stopwords.add("onto");
            this.stopwords.add("or");
            this.stopwords.add("other");
            this.stopwords.add("others");
            this.stopwords.add("otherwise");
            this.stopwords.add("ought");
            this.stopwords.add("our");
            this.stopwords.add("ours");
            this.stopwords.add("ourselves");
            this.stopwords.add("out");
            this.stopwords.add("outside");
            this.stopwords.add("over");
            this.stopwords.add("overall");
            this.stopwords.add("own");
            this.stopwords.add("p");
            this.stopwords.add("particular");
            this.stopwords.add("particularly");
            this.stopwords.add("per");
            this.stopwords.add("perhaps");
            this.stopwords.add("placed");
            this.stopwords.add("please");
            this.stopwords.add("plus");
            this.stopwords.add("possible");
            this.stopwords.add("presumably");
            this.stopwords.add("probably");
            this.stopwords.add("provides");
            this.stopwords.add("q");
            this.stopwords.add("que");
            this.stopwords.add("quite");
            this.stopwords.add("qv");
            this.stopwords.add("r");
            this.stopwords.add("rather");
            this.stopwords.add("rd");
            this.stopwords.add("re");
            this.stopwords.add("really");
            this.stopwords.add("reasonably");
            this.stopwords.add("regarding");
            this.stopwords.add("regardless");
            this.stopwords.add("regards");
            this.stopwords.add("relatively");
            this.stopwords.add("respectively");
            this.stopwords.add("right");
            this.stopwords.add("s");
            this.stopwords.add("said");
            this.stopwords.add("same");
            this.stopwords.add("saw");
            this.stopwords.add("say");
            this.stopwords.add("saying");
            this.stopwords.add("says");
            this.stopwords.add("second");
            this.stopwords.add("secondly");
            this.stopwords.add("see");
            this.stopwords.add("seeing");
            this.stopwords.add("seem");
            this.stopwords.add("seemed");
            this.stopwords.add("seeming");
            this.stopwords.add("seems");
            this.stopwords.add("seen");
            this.stopwords.add("self");
            this.stopwords.add("selves");
            this.stopwords.add("sensible");
            this.stopwords.add("sent");
            this.stopwords.add("serious");
            this.stopwords.add("seriously");
            this.stopwords.add("seven");
            this.stopwords.add("several");
            this.stopwords.add("shall");
            this.stopwords.add("she");
            this.stopwords.add("should");
            this.stopwords.add("since");
            this.stopwords.add("six");
            this.stopwords.add("so");
            this.stopwords.add("some");
            this.stopwords.add("somebody");
            this.stopwords.add("somehow");
            this.stopwords.add("someone");
            this.stopwords.add("something");
            this.stopwords.add("sometime");
            this.stopwords.add("sometimes");
            this.stopwords.add("somewhat");
            this.stopwords.add("somewhere");
            this.stopwords.add("soon");
            this.stopwords.add("sorry");
            this.stopwords.add("specified");
            this.stopwords.add("specify");
            this.stopwords.add("specifying");
            this.stopwords.add("still");
            this.stopwords.add("sub");
            this.stopwords.add("such");
            this.stopwords.add("sup");
            this.stopwords.add("sure");
            this.stopwords.add("t");
            this.stopwords.add("take");
            this.stopwords.add("taken");
            this.stopwords.add("tell");
            this.stopwords.add("tends");
            this.stopwords.add("th");
            this.stopwords.add("than");
            this.stopwords.add("thank");
            this.stopwords.add("thanks");
            this.stopwords.add("thanx");
            this.stopwords.add("that");
            this.stopwords.add("thats");
            this.stopwords.add("the");
            this.stopwords.add("their");
            this.stopwords.add("theirs");
            this.stopwords.add("them");
            this.stopwords.add("themselves");
            this.stopwords.add("then");
            this.stopwords.add("thence");
            this.stopwords.add("there");
            this.stopwords.add("thereafter");
            this.stopwords.add("thereby");
            this.stopwords.add("therefore");
            this.stopwords.add("therein");
            this.stopwords.add("theres");
            this.stopwords.add("thereupon");
            this.stopwords.add("these");
            this.stopwords.add("they");
            this.stopwords.add("think");
            this.stopwords.add("third");
            this.stopwords.add("this");
            this.stopwords.add("thorough");
            this.stopwords.add("thoroughly");
            this.stopwords.add("those");
            this.stopwords.add("though");
            this.stopwords.add("three");
            this.stopwords.add("through");
            this.stopwords.add("throughout");
            this.stopwords.add("thru");
            this.stopwords.add("thus");
            this.stopwords.add("to");
            this.stopwords.add("together");
            this.stopwords.add("too");
            this.stopwords.add("took");
            this.stopwords.add("toward");
            this.stopwords.add("towards");
            this.stopwords.add("tried");
            this.stopwords.add("tries");
            this.stopwords.add("truly");
            this.stopwords.add("try");
            this.stopwords.add("trying");
            this.stopwords.add("twice");
            this.stopwords.add("two");
            this.stopwords.add("u");
            this.stopwords.add("un");
            this.stopwords.add("under");
            this.stopwords.add("unfortunately");
            this.stopwords.add("unless");
            this.stopwords.add("unlikely");
            this.stopwords.add("until");
            this.stopwords.add("unto");
            this.stopwords.add("up");
            this.stopwords.add("upon");
            this.stopwords.add("us");
            this.stopwords.add("use");
            this.stopwords.add("used");
            this.stopwords.add("useful");
            this.stopwords.add("uses");
            this.stopwords.add("using");
            this.stopwords.add("usually");
            this.stopwords.add("uucp");
            this.stopwords.add("v");
            this.stopwords.add("value");
            this.stopwords.add("various");
            this.stopwords.add("very");
            this.stopwords.add("via");
            this.stopwords.add("viz");
            this.stopwords.add("vs");
            this.stopwords.add("w");
            this.stopwords.add("want");
            this.stopwords.add("wants");
            this.stopwords.add("was");
            this.stopwords.add("way");
            this.stopwords.add("we");
            this.stopwords.add("welcome");
            this.stopwords.add("well");
            this.stopwords.add("went");
            this.stopwords.add("were");
            this.stopwords.add("what");
            this.stopwords.add("whatever");
            this.stopwords.add("when");
            this.stopwords.add("whence");
            this.stopwords.add("whenever");
            this.stopwords.add("where");
            this.stopwords.add("whereafter");
            this.stopwords.add("whereas");
            this.stopwords.add("whereby");
            this.stopwords.add("wherein");
            this.stopwords.add("whereupon");
            this.stopwords.add("wherever");
            this.stopwords.add("whether");
            this.stopwords.add("which");
            this.stopwords.add("while");
            this.stopwords.add("whither");
            this.stopwords.add("who");
            this.stopwords.add("whoever");
            this.stopwords.add("whole");
            this.stopwords.add("whom");
            this.stopwords.add("whose");
            this.stopwords.add("why");
            this.stopwords.add("will");
            this.stopwords.add("willing");
            this.stopwords.add("wish");
            this.stopwords.add("with");
            this.stopwords.add("within");
            this.stopwords.add("without");
            this.stopwords.add("wonder");
            this.stopwords.add("would");
            this.stopwords.add("x");
            this.stopwords.add("y");
            this.stopwords.add("yes");
            this.stopwords.add("yet");
            this.stopwords.add("you");
            this.stopwords.add("your");
            this.stopwords.add("yours");
            this.stopwords.add("yourself");
            this.stopwords.add("yourselves");
            this.stopwords.add("z");
            this.stopwords.add("zero");
            Collections.sort(this.stopwords);
        }

        public static boolean isStopWord(String word) {
            if (_instance == null) {
                _instance = new FixedStopword();
            }
            return (Collections.binarySearch(_instance.stopwords, word) >= 0);
        }
        private static FixedStopword _instance;
        private ArrayList<String> stopwords;
    }
}
