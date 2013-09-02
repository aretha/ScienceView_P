/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.database;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.collections.map.MultiKeyMap;
import topicevolutionvis.data.Encoding;
import topicevolutionvis.preprocessing.Ngram;
import topicevolutionvis.preprocessing.Reference;
import topicevolutionvis.projection.ProjectionData;
import topicevolutionvis.util.Pair;

/**
 *
 * @author Aretha
 */
public class DatabaseCorpus {

    protected float[] cdata;
    protected int[] documents_ids;
    protected String name;
    protected int nrDocuments;
    protected static Encoding encoding = Encoding.ASCII;
    private int id_collection = 0;
    private TIntObjectHashMap<ArrayList<Pair>> citation_core = new TIntObjectHashMap<>();
    private MultiKeyMap coreCitationHistogram = new MultiKeyMap();
    private double[][] norm_bc;
    private int[] ascending_dates;
    private int n_unique_references = 0;

    public DatabaseCorpus(String name) {
        this.name = name;
        this.initDatabaseCorpus();
        Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.INFO, "Collection name: {0}", this.name);
        Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.INFO, "Collection id: {0}", this.id_collection);
        Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.INFO, "Number documents: {0}", this.nrDocuments);

    }

    private void initDatabaseCorpus() {
        this.retrieveCollectionId();
        this.retrieveNrDocuments();
        this.retrieveDocumentsIds();
        this.retrievetAscendingDates();
        this.matchCoreCitations();
        this.generateCoreCitationsHistogram();
        this.getNumberOfUniqueReferences_Query();
    }

    public String getCollectionName() {
        return this.name;
    }

    public float[] getClassData() {
        return this.cdata;
    }

    public int[] getDocumentsIds() {
        return this.documents_ids;
    }

    public int[] getDocumentsIds(boolean include_only_documents_with_abstract, boolean include_only_documents_with_keywords, boolean include_only_documents_with_citations) {
        return null;
    }

    public void updateClasses(ArrayList<Integer> ids_docs, int class_docs) {
        try {
            //preparing the update query
            StringBuilder query = new StringBuilder("UPDATE Documents SET class=? WHERE id_doc IN (");
            String docs = ids_docs.toString();
            query.append(docs.substring(1, docs.length() - 1)).append(") AND id_collection=?");
            PreparedStatement stmt = ConnectionManager.getInstance().getConnection().prepareStatement(query.toString());
            stmt.setInt(1, class_docs);
            stmt.setInt(2, id_collection);
            stmt.executeUpdate();


        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//    
//    public int[] getCitationsFromDocument(int id_doc) {
//        PreparedStatement stmt = null;
//        ResultSet rs = null;
//        int[] citations = null;
//        
//        try {
//            
//            stmt = SqlManager.getInstance().getSqlStatement("SELEC.CITATIONS.FROM.DOCUMENT", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
//            stmt.setInt(1, id_collection);
//            stmt.setInt(2, id_doc);
//            rs = stmt.executeQuery();
//            
//            rs.last();
//            int size = rs.getRow();
//            rs.beforeFirst();
//            
//            if (size > 0) {
//                citations = new int[size];
//                for (int i = 0; i < size; i++) {
//                    rs.next();
//                    citations[i] = rs.getInt(1);
//                }
//            }
//            return citations;
//        } catch (Exception ex) {
//            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            try {
//                if (rs != null) {
//                    rs.close();
//                }
//                if (stmt != null) {
//                    stmt.close();
//                }
//            } catch (SQLException ex) {
//                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        return null;
//    }

    public TIntObjectHashMap<ArrayList<Pair>> getCoreReferences() {
        return this.citation_core;
    }

    public int getLocalCitationCount(int id_doc) {
        return this.citation_core.get(id_doc).size();
    }

    public int getNumberOfCitationsAtYear(int id_doc, int year) {
        Integer value = (Integer) coreCitationHistogram.get(id_doc, year);
        if (value != null) {
            return value;
        }
        return 0;
    }

    private void generateCoreCitationsHistogram() {
        int n_citations, id_doc;
        int[] years = this.getAscendingDates();
        TIntIntHashMap citation_histogram = new TIntIntHashMap(years.length);
        TIntArrayList ids;

        for (int i = 0; i < this.documents_ids.length; i++) {
            id_doc = this.documents_ids[i];
            //ids of docucments citing id_doc
            ids = new TIntArrayList();
            for (Pair citation : citation_core.get(id_doc)) {
                ids.add(citation.index);
            }
            if (!ids.isEmpty()) {
                //discovering how many ids cite id_doc until year[i]
                PreparedStatement stmt = null;
                ResultSet rs = null;
                try {
                    StringBuilder sqlStatement = new StringBuilder("Select year, count(id_citation) FROM citations WHERE id_citation in(");
                    sqlStatement.append(ids.toString().substring(1, ids.toString().length() - 1));
                    sqlStatement.append(") and id_collection=").append(this.id_collection).append("group by year order by year");
                    stmt = ConnectionManager.getInstance().getConnection().prepareStatement(sqlStatement.toString());
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        citation_histogram.put(rs.getInt(1), rs.getInt(2));
                    }

                } catch (IOException | SQLException ex) {
                    Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                        if (stmt != null) {
                            stmt.close();
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            int count;
            for (int j = 0; j < years.length; j++) {
                count = 0;
                for (int n = 0; n <= j; n++) {
                    count += citation_histogram.get(years[n]);
                }
                this.coreCitationHistogram.put(id_doc, years[j], count);
            }
        }
    }

    public int getCollectionId() {
        return this.id_collection;
    }

    public String getCollectionFilename() {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            //getting the collection id
            stmt = SqlManager.getInstance().getSqlStatement("SELECT.COLLECTION.FILENAME", -1, -1);
            stmt.setInt(1, this.id_collection);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            } else {
                throw new IOException("There is not exist a collection called \""
                        + this.name + "\"");
            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    private void retrieveCollectionId() {
        if (this.name != null) {
            PreparedStatement stmt = null;
            ResultSet rs = null;
            try {
                //getting the collection id
                stmt = SqlManager.getInstance().getSqlStatement("SELECT.COLLECTION.BY.NAME", -1, -1);
                stmt.setString(1, this.name);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    this.id_collection = rs.getInt(1);
                } else {
                    throw new IOException("There is not exist a collection called \""
                            + this.name + "\"");
                }
            } catch (IOException | SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (stmt != null) {
                        stmt.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public int getNumberOfDocuments() {
        return this.nrDocuments;
    }

    private void retrieveDocumentsIds() {
        if (this.nrDocuments > 0) {
            PreparedStatement stmt = null;
            ResultSet rs = null;
            try {
                this.documents_ids = new int[this.nrDocuments];

                stmt = SqlManager.getInstance().getSqlStatement("SELECT.DOCUMENTS.IDS", -1, -1);
                stmt.setInt(1, this.id_collection);
                rs = stmt.executeQuery();

                for (int i = 0; i < this.documents_ids.length && rs.next(); i++) {
                    int id = rs.getInt(1);
                    this.documents_ids[i] = id;
                }

                //to ensure that different "import" of the same data set on
                //different moments returns the same order of ids
                Arrays.sort(this.documents_ids);
            } catch (IOException | SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (stmt != null) {
                        stmt.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void retrieveNrDocuments() {
        if (this.id_collection > 0) {
            PreparedStatement stmt = null;
            ResultSet rs = null;
            try {
                //getting the number of documents on the collection
                stmt = SqlManager.getInstance().getSqlStatement("SELECT.NUMBER.DOCUMENTS", -1, -1);
                stmt.setInt(1, this.id_collection);
                rs = stmt.executeQuery();

                if (rs.next()) {
                    this.nrDocuments = rs.getInt(1);
                } else {
                    throw new IOException("Problems retrieving the number of documents.");
                }
            } catch (IOException | SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (stmt != null) {
                        stmt.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public String getFullContent(int id) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        StringBuilder content = new StringBuilder();
        StringTokenizer tokenizer;
        try {
            stmt = SqlManager.getInstance().getSqlStatement("SELECT.CONTENT.DOCUMENT", -1, -1);
            stmt.setInt(1, id);
            stmt.setInt(2, this.id_collection);

            //getting the result
            rs = stmt.executeQuery();
            if (rs.next()) {
                //title
                if (rs.getString(1).compareTo("") != 0) {
                    content.append(rs.getString(1)).append("\r\n\n");
                }

                //abstract
                if (rs.getString(3).compareTo("") != 0) {
                    content.append(rs.getString(3)).append("\r\n\n");
                }

                //keywords
                if (rs.getString(4).compareTo("") != 0) {
                    content.append(rs.getString(4)).append("\r\n\n");
                }

                //authors keywords
                if (rs.getString(5).compareTo("") != 0) {
                    content.append(rs.getString(5)).append("\r\n\n");
                }

            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return content.toString();
    }

    public int getNumberOfUniqueReferences() {
        return this.n_unique_references;
    }

    public int getMinIdCitation() {
        PreparedStatement stmt;
        ResultSet rs;
        int min_id;
        try {
            stmt = SqlManager.getInstance().getSqlStatement("SELECT.MIN.ID.CITATION", -1, -1);
            stmt.setInt(1, this.id_collection);
            rs = stmt.executeQuery();
            rs.next();

            min_id = rs.getInt(1);
            rs.close();
            stmt.close();
            return min_id;

        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    private void getNumberOfUniqueReferences_Query() {
        PreparedStatement stmt;
        ResultSet rs;
        try {
            stmt = SqlManager.getInstance().getSqlStatement("COUNT.UNIQUE.REFERENCES", -1, -1);
            stmt.setInt(1, this.id_collection);
            rs = stmt.executeQuery();
            rs.next();

            this.n_unique_references = rs.getInt(1);
            rs.close();
            stmt.close();

        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getNumberOfSameAuthors(int id1, int id2) {
        PreparedStatement stmt;
        ResultSet rs;
        try {
            stmt = SqlManager.getInstance().getSqlStatement("COAUTHORSHIP", -1, -1);
            stmt.setInt(1, id1);
            stmt.setInt(2, this.id_collection);
            stmt.setInt(3, id2);
            stmt.setInt(4, this.id_collection);
            rs = stmt.executeQuery();
            rs.next();

            int number = rs.getInt(1);

            rs.close();
            stmt.close();
            return number;
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getBibliographicCoupling_Log(int id1, int id2) {
        if (id1 > id2) {
            return this.norm_bc[id1][id2];
        } else {
            return this.norm_bc[id2][id1];
        }
    }

    private int getBibliographicCoupling(int id1, int id2) {
        PreparedStatement stmt;
        ResultSet rs;
        try {
            stmt = SqlManager.getInstance().getSqlStatement("BIBLIOGRAPHIC.COUPLING", -1, -1);
            stmt.setInt(1, id1);
            stmt.setInt(2, this.id_collection);
            stmt.setInt(3, id2);
            stmt.setInt(4, this.id_collection);
            rs = stmt.executeQuery();
            rs.next();
            int bc = rs.getInt(1);
            rs.close();
            stmt.close();
            return bc;
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public String getViewContent(int id) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        StringBuilder content = new StringBuilder();
        StringTokenizer tokenizer;


        try {
            stmt = SqlManager.getInstance().getSqlStatement("SELECT.AUTHORS.FROM.DOCUMENT", -1, -1);
            stmt.setInt(1, id);
            stmt.setInt(2, this.id_collection);
            rs = stmt.executeQuery();

            while (rs.next()) {
                content.append(rs.getString(1)).append("; ");
            }
            content.append("\r\n\n");

            stmt = SqlManager.getInstance().getSqlStatement("SELECT.CONTENT.DOCUMENT", -1, -1);
            stmt.setInt(1, id);
            stmt.setInt(2, this.id_collection);

            //getting the result
            rs = stmt.executeQuery();

            if (rs.next()) {
                //research address
                if (rs.getString(2) != null) {
                    content.append("Research Addresses:\r\n");
                    tokenizer = new StringTokenizer(rs.getString(2), "|");
                    while (tokenizer.hasMoreTokens()) {
                        content.append(tokenizer.nextToken()).append("\r\n");
                    }
                    content.append("\n");
                }

                //journal
                if (rs.getString(12) != null) {
                    content.append(rs.getString(12)).append("\r\n");
                }

                //pages
                if (rs.getString(9) != null) {
                    content.append("Pages: ").append(rs.getString(9)).append("-");
                    if (rs.getString(10) != null) {
                        content.append(rs.getString(10));
                    }
                    content.append("\n");
                }

                //year of publication
                if (rs.getInt(6) != -1) {
                    content.append("Year of Publication: ").append(rs.getInt(6)).append("\n");
                }

                //global citation count
                if (rs.getInt(7) != 0) {
                    content.append("Global Citation Count: ").append(rs.getInt(7)).append("\n");
                }
                content.append("\n");

                //abstract
                if (rs.getString(3) != null) {
                    content.append(rs.getString(3)).append("\r\n\n");
                }

                //keywords
                if (rs.getString(4) != null) {
                    content.append("Keywords: ");
                    content.append(rs.getString(4)).append("\r\n\n");
                }
                if (rs.getString(5) != null) {
                    content.append("Authors Keywords: ");
                    content.append(rs.getString(5)).append("\r\n\n");
                }
                if (rs.getString(8) != null) {
                    content.append("DOI: ");
                    content.append(rs.getString(8)).append("\r\n\n");
                }

                //references
                ArrayList<String> references = this.getReferences(id);
                if (!references.isEmpty()) {
                    content.append("References\r\n");
                    for (String ref : references) {
                        content.append(ref).append("\r\n");
                    }
                }
            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return content.toString();
    }

    public void saveToPExFormat(String filename, boolean individual, int yearStep, ProjectionData pdata) throws Exception {
        byte[] buffer = new byte[1024];
        File file;
        BufferedWriter bufferedWriter, bufferedWriterScalar = null;
        ZipOutputStream out;
//        String fname;
//        if (individual) {
//            int count = 0;
//            int[] yearsInt = this.getAscendingDates();
//            I
//            for (int i = 0; i < yearsInt.length; i++) {
//
//            }
//            for (Integer year : years) {
//                if (this.trueLbLDistribution.get(year) == 0) {
//                    continue;
//                }
//                if (count == 0) {
//                    if ((years.indexOf(year) + (yearStep - 1)) > years.size()) {
//                        if (yearStep != 1) {
//                            fname = filename.replace(".", "-" + year + "-" + years.get(years.size() - 1) + ".");
//                        } else {
//                            fname = filename.replace(".", "-" + year + ".");
//                        }
//
//                    } else {
//                        if (yearStep != 1) {
//                            fname = filename.replace(".", "-" + year + "-" + years.get(years.indexOf(year) + yearStep - 1) + ".");
//                        } else {
//                            fname = filename.replace(".", "-" + year + ".");
//                        }
//                    }
//                    out = new ZipOutputStream(new FileOutputStream(fname));
//                    //bufferedWriterScalar = new BufferedWriter(new FileWriter(fname.replace(".zip", "")));
//                }
//                for (EndnoteEntry entry : this.documentsTreeMap.get(year)) {
//                    if (entry.getTrueLblWork() == 1 && entry.getAbstract() != null) {
//                        StringBuilder entryFilename = new StringBuilder(String.valueOf(entry.getId()));
//                        entryFilename.append(".txt");
//                        file = new File(entryFilename.toString());
//                        bufferedWriter = new BufferedWriter(new FileWriter(file), 1024);
//
//                        //title
//                        if (pdata.getSaveTitleToPExFormat()) {
//                            bufferedWriter.write(entry.getTitle());
//                            bufferedWriter.newLine();
//                            bufferedWriter.newLine();
//                        }
//
//
//                        //authors
//                        if (pdata.getSaveAuthorsToPExFormat() && entry.getAuthors() != null) {
//                            bufferedWriter.write(entry.getAuthors().replaceAll("|", "; "));
//                            bufferedWriter.newLine();
//                            bufferedWriter.newLine();
//                            bufferedWriter.flush();
//                        }
//
//
//                        //abstract
//                        if (pdata.getSaveAbstractToPExFormat() && entry.getAbstract() != null) {
//                            bufferedWriter.write(entry.getAbstract());
//                            bufferedWriter.newLine();
//                            bufferedWriter.newLine();
//                            bufferedWriter.flush();
//                        }
//
//                        //keywords
//                        if (pdata.getSaveKeywordsToPExFormat() && entry.getKeywords() != null) {
//                            bufferedWriter.write(entry.getKeywords());
//                            bufferedWriter.newLine();
//                            bufferedWriter.newLine();
//                            bufferedWriter.flush();
//                        }
//
//                        //references
//                        if (pdata.getSaveReferencesToPExFormat() && entry.getReferences() != null) {
//                            bufferedWriter.write(entry.getReferences());
//                            bufferedWriter.newLine();
//                            bufferedWriter.flush();
//                            bufferedWriter.close();
//                        }
//
//                        FileInputStream in = new FileInputStream(entryFilename.toString());
//
//                        // Add ZIP entry to output stream.
//                        out.putNextEntry(new ZipEntry(entryFilename.toString()));
//                        int n;
//                        while ((n = in.read(buffer)) != -1) {
//                            out.write(buffer, 0, n);
//                        }
//                        in.close();
//                        file.delete();
//                    }
//                }
//                count++;
//                if (count == yearStep || years.indexOf(year) == years.size() - 1) {
//                    out.close();
//                    count = 0;
//
//                }
//            }
//        } else {
        out = new ZipOutputStream(new FileOutputStream(filename));
        int semAbstract = 0;

        if (pdata.getScalarFilename().compareToIgnoreCase("") != 0) {
            //writing scalar value for TrueLbL
            bufferedWriterScalar = new BufferedWriter(new FileWriter(pdata.getScalarFilename()), 1024);
            bufferedWriterScalar.write("trueLbl");
            bufferedWriterScalar.newLine();
        }
        String authors, abstractText, keywords;
        ArrayList<String> references;

        float trueLbLWork;
        for (int id : this.documents_ids) {
            trueLbLWork = this.getClass(id);
            authors = this.getAuthors(id);
            abstractText = this.getAbstract(id);
            keywords = this.getKeywords(id);
            references = this.getReferences(id);

            if (this.getAbstract(id) != null && trueLbLWork != 0.5f) {
                if (pdata.getScalarFilename().compareToIgnoreCase("") != 0) {
                    //writing scalar value for TrueLbL
                    bufferedWriterScalar.write(id + ".txt;" + trueLbLWork);
                    bufferedWriterScalar.newLine();
                    bufferedWriterScalar.flush();
                }

                //criando o arquivo
                StringBuilder entryFilename = new StringBuilder(String.valueOf(id));
                entryFilename.append(".txt");
                file = new File(entryFilename.toString());
                bufferedWriter = new BufferedWriter(new FileWriter(file), 1024);

                //title
                if (pdata.getSaveTitleToPExFormat()) {
                    bufferedWriter.write(this.getTitle(id));
                    bufferedWriter.newLine();
                    bufferedWriter.newLine();
                }

                //authors
                if (pdata.getSaveAuthorsToPExFormat() && this.getAuthors(id) != null) {
                    bufferedWriter.write(authors.replaceAll("|", "; "));
                    bufferedWriter.newLine();
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }


                //abstract
                if (pdata.getSaveAbstractToPExFormat() && abstractText != null) {
                    bufferedWriter.write(abstractText);
                    bufferedWriter.newLine();
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                } else {
                    semAbstract++;
                }

                //keywords
                if (pdata.getSaveKeywordsToPExFormat() && keywords != null) {
                    bufferedWriter.write(keywords);
                    bufferedWriter.newLine();
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }

                //references
                if (pdata.getSaveReferencesToPExFormat() && references != null) {
                    for (String ref : references) {
                        bufferedWriter.write(ref);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                }


                bufferedWriter.close();
                FileInputStream in = new FileInputStream(entryFilename.toString());
                out.putNextEntry(new ZipEntry(entryFilename.toString()));
                int count;
                while ((count = in.read(buffer)) != -1) {
                    out.write(buffer, 0, count);
                }

                file.delete();
            } //            }
            if (bufferedWriterScalar != null) {
                bufferedWriterScalar.close();
            }
            System.out.println("Documentos sem abstract: " + semAbstract);
            out.close();
        }
    }

    public ArrayList<Ngram> getNgrams(int id) throws IOException {
        ArrayList<Ngram> ngrams = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = SqlManager.getInstance().getSqlStatement("SELECT.NGRAMS.DOCUMENT", -1, -1);
            stmt.setInt(1, id);
            stmt.setInt(2, this.id_collection);
            rs = stmt.executeQuery();

            if (rs.next()) {
                InputStream is = rs.getBlob(1).getBinaryStream();
                ObjectInputStream ois = new ObjectInputStream(is);
                ngrams = (ArrayList<Ngram>) ois.readObject();
            }

        } catch (IOException | SQLException | ClassNotFoundException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }

            } catch (SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ngrams;
    }

    public boolean doesThisDocumentCitesThisReference(int id_doc, int index_citation) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = SqlManager.getInstance().getSqlStatement("DOES.THIS.DOCUMENT.CITES.THIS.REFERENCE", -1, -1);
            stmt.setInt(1, id_doc);
            stmt.setInt(2, index_citation);
            stmt.setInt(3, this.id_collection);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    public ArrayList<Reference> getCorpusReferences(int lower, int upper) {
        ArrayList<Reference> references = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int id_citation;
        boolean lower_ok = false, upper_ok = false;
        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM (SELECT id_CITATION , count(*) as freq FROM DOCUMENTS_TO_CITATIONS where id_COLLECTION =? group by ID_CITATION order by freq desc)");
            if (upper != -1) {
                sql = sql.append("WHERE freq <= ?");
                upper_ok = true;
                if (lower != - 1) {
                    sql = sql.append("AND freq >= ?");
                    lower_ok = true;
                }
            } else {
                if (lower != - 1) {
                    sql = sql.append("WHERE freq >= ?");
                    lower_ok = true;
                }
            }
            stmt = ConnectionManager.getInstance().getConnection().prepareStatement(sql.toString());
            stmt.setInt(1, this.id_collection);
            if (upper_ok) {
                stmt.setInt(2, upper);
                if (lower_ok) {
                    stmt.setInt(3, lower);
                }
            } else {
                if (lower_ok) {
                    stmt.setInt(2, lower);
                }
            }
            rs = stmt.executeQuery();

            while (rs.next()) {
                id_citation = rs.getInt(1);
                references.add(new Reference(this.getReferenceText(id_citation), rs.getInt(2), id_citation));
            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return references;
    }

    public ArrayList<Ngram> getCorpusNgrams() throws IOException {
        ArrayList<Ngram> ngrams = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = SqlManager.getInstance().getSqlStatement("SELECT.NGRAMS.COLLECTION", -1, -1);
            stmt.setInt(1, this.id_collection);
            rs = stmt.executeQuery();

            if (rs.next()) {
                InputStream is = rs.getBlob(1).getBinaryStream();
                ObjectInputStream ois = new ObjectInputStream(is);
                ngrams = (ArrayList<Ngram>) ois.readObject();

            }
        } catch (IOException | SQLException | ClassNotFoundException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
                throw new IOException(ex.getMessage());
            }
        }
        return ngrams;
    }

    public int getNumberGrams() {
        int nrGrams = 0;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = SqlManager.getInstance().getSqlStatement("SELECT.NUMBER.GRAMS", -1, -1);
            stmt.setInt(1, this.id_collection);
            rs = stmt.executeQuery();

            if (rs.next()) {
                nrGrams = rs.getInt(1);
            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return nrGrams;
    }

    public int[] getAscendingDates() {
        return this.ascending_dates;
    }

    private void retrievetAscendingDates() {
        PreparedStatement stmt = null;
        ResultSet rs = null;


        try {
            stmt = SqlManager.getInstance().getSqlStatement("SELECT.DISTINCT.YEARS", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setInt(1, this.id_collection);
            rs = stmt.executeQuery();

            rs.last();
            int size = rs.getRow();
            ascending_dates = new int[size];
            rs.beforeFirst();

            for (int i = 0; i < size; i++) {
                rs.next();
                ascending_dates[i] = rs.getInt(1);
            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }

            } catch (SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public int[] getDocumentsWithLCC(int value, String comparison) {
        PreparedStatement stmt;
        ResultSet rs;
        Connection conn;
        int[] ids = null;
        StringBuilder sql_statement = new StringBuilder("SELECT ID_DOC FROM DOCUMENTS WHERE LCC ");
        sql_statement = sql_statement.append(comparison).append(" ").append(value);
        try {
            conn = ConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(sql_statement.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            rs = stmt.executeQuery();
            rs.last();
            int size = rs.getRow();
            ids = new int[size];
            rs.beforeFirst();
            for (int i = 0; i < size; i++) {
                rs.next();
                ids[i] = rs.getInt(1);
            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ids;

    }

    public int[] getDocumentsWithGCC(int value, String comparison) {
        PreparedStatement stmt;
        ResultSet rs;
        Connection conn;
        int[] ids = null;
        StringBuilder sql_statement = new StringBuilder("SELECT ID_DOC FROM DOCUMENTS WHERE GCC ");
        sql_statement = sql_statement.append(comparison).append(" ").append(value);

        try {
            conn = ConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(sql_statement.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            rs = stmt.executeQuery();
            rs.last();
            int size = rs.getRow();
            ids = new int[size];
            rs.beforeFirst();
            for (int i = 0; i < size; i++) {
                rs.next();
                ids[i] = rs.getInt(1);
            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ids;
    }

    public TIntArrayList getDocumentsIdsFromYearToYear(int begin_year, int end_year) {

        PreparedStatement stmt;
        ResultSet rs;
        TIntArrayList ids = new TIntArrayList();
        try {
            stmt = SqlManager.getInstance().getSqlStatement("SELECT.ID.DOCUMENTS.FROM.YEAR.TO.YEAR", -1, -1);
            stmt.setInt(1, this.id_collection);
            stmt.setInt(2, begin_year);
            stmt.setInt(3, end_year);
            rs = stmt.executeQuery();

            while (rs.next()) {
                ids.add(rs.getInt(1));
            }

        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ids;
    }

    public int[] getDocumentsIdsSortedByTitle(int year) {
        PreparedStatement stmt;
        ResultSet rs;
        int[] ids = null;

        try {
            stmt = SqlManager.getInstance().getSqlStatement("SELECT.ID.DOCUMENTS.FROM.YEAR.ORDER.BY.TITLE.ORDER.BY.TITLE", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setInt(1, this.id_collection);
            stmt.setInt(2, year);
            rs = stmt.executeQuery();

            rs.last();
            int size = rs.getRow();
            ids = new int[size];
            rs.beforeFirst();

            for (int i = 0; i < size; i++) {
                rs.next();
                ids[i] = rs.getInt(1);
            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ids;
    }

    public int[] getDocumentsFromAuthor(String author_name, int year) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int[] ids = null;
        try {

            stmt = SqlManager.getInstance().getSqlStatement("SELECT.ID.DOCUMENTS.FROM.AUTHOR.TO.YEAR", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, author_name);
            stmt.setInt(2, id_collection);
            stmt.setInt(3, year);
            rs = stmt.executeQuery();


            rs.last();
            int size = rs.getRow();
            ids = new int[size];
            rs.beforeFirst();

            for (int i = 0; i < size; i++) {
                rs.next();
                ids[i] = rs.getInt(1);
            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ids;
    }

    public Object[][] getMainAuthors(TIntArrayList docs_ids) {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            StringBuilder docs = new StringBuilder(docs_ids.size() * 2);
            for (int i = 0; i < docs_ids.size() - 1; i++) {
                docs = docs.append(docs_ids.get(i)).append(", ");
            }
            docs = docs.append(docs_ids.get(docs_ids.size() - 1));

            StringBuilder sql = new StringBuilder("SELECT name, aUTHORS.ID_AUTHOR, COUNT(authors.id_author) as c FROM DOCUMENTS_TO_AUTHORS INNER JOIN authors where id_DOC in (" + docs + ") AND aUTHORS.ID_AUTHOR =dOCUMENTS_TO_AUTHORs.ID_AUTHOR AND AUTHORS.ID_COLLECTION =? GROUP BY Authors.ID_AUTHOR order by c desc");
            stmt = ConnectionManager.getInstance().getConnection().prepareStatement(sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setInt(1, this.id_collection);

            rs = stmt.executeQuery();

            rs.last();
            int size = rs.getRow();
            Object[][] data = new Object[size][2];
            rs.beforeFirst();

            for (int i = 0; i < size; i++) {
                rs.next();
                data[i][0] = rs.getString(1);
                data[i][1] = rs.getInt(3);
            }
            return data;
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public TIntIntHashMap searchTerm(String term) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {

            TIntIntHashMap aux = new TIntIntHashMap();

            stmt = SqlManager.getInstance().getSqlStatement("SEARCH.TERM", -1, -1);
            stmt.setString(1, term);
            stmt.setString(2, term);
            stmt.setString(3, term);
            stmt.setInt(4, this.id_collection);
            
            rs = stmt.executeQuery();
            while(rs.next()){
                aux.put(rs.getInt(1), rs.getInt(2));
            }
            return aux;
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public int[] getDocumentsIds(int year) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int[] ids = null;

        try {
            stmt = SqlManager.getInstance().getSqlStatement("SELECT.ID.DOCUMENTS.FROM.YEAR", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setInt(1, this.id_collection);
            stmt.setInt(2, year);
            rs = stmt.executeQuery();

            rs.last();
            int size = rs.getRow();
            ids = new int[size];
            rs.beforeFirst();

            for (int i = 0; i < size; i++) {
                rs.next();
                ids[i] = rs.getInt(1);
            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ids;
    }

    public String getAbstract(int id) {
        String abstractText = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = SqlManager.getInstance().getSqlStatement("SELECT.DOCUMENT.ABSTRACT", -1, -1);
            stmt.setInt(1, this.id_collection);
            stmt.setInt(2, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                abstractText = rs.getString(1);
            }

        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return abstractText;
    }

    public String getTitle(int id) {
        String title = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = SqlManager.getInstance().getSqlStatement("SELECT.DOCUMENT.TITLE", -1, -1);
            stmt.setInt(1, this.id_collection);
            stmt.setInt(2, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                title = rs.getString(1);
            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return title;
    }

    public int getYear(int id) {
        int year = -1;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {

            stmt = SqlManager.getInstance().getSqlStatement("SELECT.DOCUMENT.YEAR", -1, -1);
            stmt.setInt(1, this.id_collection);
            stmt.setInt(2, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                year = rs.getInt(1);
            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }

                if (stmt != null) {
                    stmt.close();
                }

            } catch (SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return year;
    }

    public String getPDFFile(int id) {
        String pdfFile = "";
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {

            stmt = SqlManager.getInstance().getSqlStatement("SELECT.PDF.DOCUMENT", -1, -1);
            stmt.setInt(1, this.id_collection);
            stmt.setInt(2, id);

            rs = stmt.executeQuery();

            if (rs.next()) {
                pdfFile = rs.getString(1);
            }

        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return pdfFile;
    }

    public String getKeywords(int id) {
        String keywords = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = SqlManager.getInstance().getSqlStatement("SELECT.DOCUMENT.KEYWORDS", -1, -1);
            stmt.setInt(1, this.id_collection);
            stmt.setInt(2, id);

            rs = stmt.executeQuery();
            if (rs.next()) {
                keywords = rs.getString(1);
            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return keywords;
    }

    private String getReferenceText(int id_citation) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String text = null;
        try {
            stmt = SqlManager.getInstance().getSqlStatement("SELECT.REFERENCE", -1, -1);
            stmt.setInt(1, id_citation);
            stmt.setInt(2, id_collection);
            rs = stmt.executeQuery();
            if (rs.next()) {
                text = rs.getString(1);
            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return text;
    }

    public ArrayList<String> getReferences(int id) {
        ArrayList<String> references = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = SqlManager.getInstance().getSqlStatement("SELECT.REFERENCES.DOCUMENT", -1, -1);
            stmt.setInt(1, id);
            stmt.setInt(2, this.id_collection);

            rs = stmt.executeQuery();
            while (rs.next()) {
                references.add(rs.getString(1));
            }

        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return references;
    }

    public void matchCoreCitations() {
        for (int i = 0; i < this.documents_ids.length; i++) {
            this.citation_core.put(documents_ids[i], new ArrayList<Pair>());
        }

        PreparedStatement stmt = null, stmt2, stmt3;
        ResultSet rs = null, rs2;
        int count;
        try {
            stmt = SqlManager.getInstance().getSqlStatement("CORE.REFERENCES", -1, -1);
            stmt.setInt(1, id_collection);
            rs = stmt.executeQuery();

            int id_citation, id_doc_cited;
            while (rs.next()) {
                id_citation = rs.getInt(1);
                id_doc_cited = rs.getInt(2);

                stmt2 = SqlManager.getInstance().getSqlStatement("DOCUMENTS.CITING.REFERENCE", -1, -1);
                stmt2.setInt(1, id_citation);
                stmt2.setInt(2, id_collection);
                rs2 = stmt2.executeQuery();

                count = 0;
                boolean contains = this.citation_core.containsKey(id_doc_cited);
                if (contains) {
                    while (rs2.next()) {
                        this.citation_core.get(id_doc_cited).add(new Pair(rs2.getInt(1), -1));
                        count++;
                    }
                }
                stmt2.close();
                rs2.close();
                if (contains) {
                    stmt3 = SqlManager.getInstance().getSqlStatement("UPDATE.LCC.DOCUMENT", -1, -1);
                    stmt3.setInt(1, count);
                    stmt3.setInt(2, id_doc_cited);
                    stmt3.setInt(3, id_collection);
                    stmt3.executeUpdate();
                    stmt3.close();
                }

            }
            stmt.close();
            rs.close();
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public TIntObjectHashMap<ArrayList<Pair>> getCoAuthorship() {
        TIntObjectHashMap<ArrayList<Pair>> coauthorship = new TIntObjectHashMap<>();
        for (int i = 0; i < this.documents_ids.length; i++) {
            coauthorship.put(documents_ids[i], new ArrayList<Pair>());
        }

        for (int i = 0; i < documents_ids.length; i++) {
            for (int j = 0; j < i; j++) {
                coauthorship.get(documents_ids[i]).add(new Pair(documents_ids[j], this.getNumberOfSameAuthors(documents_ids[i], documents_ids[i])));
            }
        }
        return coauthorship;
    }

    public static boolean uniqueName(String collection) throws IOException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = SqlManager.getInstance().getSqlStatement("SELECT.COLLECTION.BY.NAME", -1, -1);
            stmt.setString(1, collection);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex.getMessage());
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
                throw new IOException(ex.getMessage());
            }
        }
    }

    public TIntObjectHashMap<TIntIntHashMap> getBibliographicCoupling() {
        TIntObjectHashMap<TIntIntHashMap> bibliographic_coupling = new TIntObjectHashMap<>();
        bibliographic_coupling.put(documents_ids[0], new TIntIntHashMap());
        int bc;
        for (int i = 1; i < documents_ids.length; i++) {
            bibliographic_coupling.put(documents_ids[i], new TIntIntHashMap());
            for (int j = 0; j < i; j++) {
                bc = getBibliographicCoupling(documents_ids[i], documents_ids[j]);
                if (bc > 0) {
                    bibliographic_coupling.get(documents_ids[i]).put(documents_ids[j], bc);
                }
            }
        }
        return bibliographic_coupling;
    }

//    public void generateBibliographicCoupling() {
//        for (int i = 0; i < this.documents_ids.length; i++) {
//            bibliographic_coupling.add(new ArrayList<Pair>());
//        }
//
//        int bc;
//        double min = Double.MAX_VALUE, max = 0;
//        this.norm_bc = new double[documents_ids.length][];
//
//
//        for (int i = 1; i < documents_ids.length; i++) {
//            this.norm_bc[i] = new double[i];
//            for (int j = 0; j < i; j++) {
//                bc = getBibliographicCoupling(documents_ids[i], documents_ids[j]);
//                this.norm_bc[i][j] = bc;
//                if (bc < min) {
//                    min = bc;
//                }
//                if (bc > max) {
//                    max = bc;
//                }
//                if (bc > 0) {
//                    bibliographic_coupling.get(i).add(new Pair(j, bc));
//                }
//            }
//        }
//
//        double min_log = 0;
//        if (min != 0) {
//            min_log = Math.log(min);
//        }
//
//        double max_log = 0;
//        if (max != 0) {
//            max_log = Math.log(max);
//        }
//
//        double bc_log;
//
//        for (int i = 1; i < documents_ids.length; i++) {
//            for (int j = 0; j < i; j++) {
//                bc_log = 0;
//                bc = (int) this.norm_bc[i][j];
//                if (bc != 0) {
//                    bc_log = Math.log(bc);
//                }
//                this.norm_bc[i][j] = (bc_log - min_log) / (max_log - min_log);
//            }
//        }
//    }
    public int getGlobalCitationCount(int id) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = SqlManager.getInstance().getSqlStatement("SELECT.TIMESCITED.DOCUMENT", -1, -1);
            stmt.setInt(1, id);
            stmt.setInt(2, this.id_collection);

            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }

            } catch (SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    public String getAuthors(int id) {
        String authors = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;


        try {
            stmt = SqlManager.getInstance().getSqlStatement("SELECT.AUTHORS.DOCUMENT", -1, -1);
            stmt.setInt(1, this.id_collection);
            stmt.setInt(2, id);

            rs = stmt.executeQuery();
            if (rs.next()) {
                authors = rs.getString(1);
            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return authors;
    }

    public String getDOI(int id) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = SqlManager.getInstance().getSqlStatement("SELECT.DOCUMENT.DOI", -1, -1);
            stmt.setInt(1, this.id_collection);
            stmt.setInt(2, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString(1);
            } else {
                return null;
            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public int getClass(int id) {
        int classId = -1;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = SqlManager.getInstance().getSqlStatement("SELECT.DOCUMENT.CLASS", -1, -1);
            stmt.setInt(1, this.id_collection);
            stmt.setInt(2, id);

            rs = stmt.executeQuery();
            if (rs.next()) {
                classId = rs.getInt(1);
                return classId;
            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return classId;
    }
}
