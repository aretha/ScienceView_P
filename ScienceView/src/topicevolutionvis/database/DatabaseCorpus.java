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
    
    private ConnectionManager connManager;
    
    private SqlManager sqlManager;

    public DatabaseCorpus(String name) {
        this.name = name;
        connManager = H2ConnectionManager.getInstance();
        sqlManager = SqlManager.getInstance();
        initDatabaseCorpus();
    }

    private void initDatabaseCorpus() {
    	Connection conn = connManager.getConnection();
    	try {
	        retrieveCollectionId(conn);
	        retrieveNrDocuments(conn);
	        retrieveDocumentsIds(conn);
	        retrievetAscendingDates(conn);
	        matchCoreCitations(conn);
	        generateCoreCitationsHistogram(conn);
	        getNumberOfUniqueReferences_Query(conn);
    	} finally {
    		SqlUtil.close(conn);
    	}
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
        StringBuilder query = new StringBuilder("UPDATE Documents SET class=? WHERE id_doc IN (");
        String docs = ids_docs.toString();
        query.append(docs.substring(1, docs.length() - 1)).append(") AND id_collection=?");
        PreparedStatement stmt = null; 
        Connection conn = null;
        
        try {
        	conn = connManager.getConnection();
            stmt = sqlManager.createSqlStatement(conn, query.toString());
            stmt.setInt(1, class_docs);
            stmt.setInt(2, id_collection);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not update class for selected documents", e);
        } finally {
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
    }

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

    private void generateCoreCitationsHistogram(Connection conn) {
        int id_doc;
        int count;
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
                    StringBuilder sqlStatement = new StringBuilder("Select year, count(id_citation) FROM citations WHERE id_citation in (");
                    sqlStatement.append(ids.toString().substring(1, ids.toString().length() - 1));
                    sqlStatement.append(") and id_collection=").append(this.id_collection).append(" group by year order by year");
                	stmt = sqlManager.createSqlStatement(conn, sqlStatement.toString());
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        citation_histogram.put(rs.getInt(1), rs.getInt(2));
                    }
                } catch (SQLException e) {
                    throw new RuntimeException("Could not load citation data to build the histogram", e);
                } finally {
                	SqlUtil.close(rs);
                	SqlUtil.close(stmt);
                }
            }

            for (int j = 0; j < years.length; j++) {
                count = 0;
                for (int n = 0; n <= j; n++) {
                    count += citation_histogram.get(years[n]);
                }
                coreCitationHistogram.put(id_doc, years[j], count);
            }
        }
    }

    public int getCollectionId() {
        return this.id_collection;
    }

    public String getCollectionFilename() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            // Get the collection id
        	conn = connManager.getConnection();
            stmt = sqlManager.getSqlStatement(conn, "SELECT.COLLECTION.FILENAME");
            stmt.setInt(1, this.id_collection);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            } else {
                throw new IllegalArgumentException("There is not exist a collection called \"" + this.name + "\"");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not get collection filename", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
    }

    private void retrieveCollectionId(Connection conn) {
        if (this.name != null) {
            PreparedStatement stmt = null;
            ResultSet rs = null;
            try {
                // Getting the collection id
                stmt = sqlManager.getSqlStatement(conn, "SELECT.COLLECTION.BY.NAME");
                stmt.setString(1, this.name);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    this.id_collection = rs.getInt(1);
                } else {
                    throw new IllegalArgumentException("There is not exist a collection called \"" + this.name + "\"");
                }
            } catch (SQLException e) {
                throw new RuntimeException("Could not get collection filename", e);
            } finally {
            	SqlUtil.close(rs);
            	SqlUtil.close(stmt);
            }
        }
    }

    public int getNumberOfDocuments() {
        return this.nrDocuments;
    }

    private void retrieveDocumentsIds(Connection conn)
    {
        if (nrDocuments > 0) {
            PreparedStatement stmt = null;
            ResultSet rs = null;
            documents_ids = new int[this.nrDocuments];

            try {
                stmt = sqlManager.getSqlStatement(conn, "SELECT.DOCUMENTS.IDS");
                stmt.setInt(1, this.id_collection);
                rs = stmt.executeQuery();

                for (int i = 0; i < this.documents_ids.length && rs.next(); i++) {
                    int id = rs.getInt(1);
                    this.documents_ids[i] = id;
                }

                //to ensure that different "import" of the same data set on
                //different moments returns the same order of ids
                Arrays.sort(this.documents_ids);
            } catch (SQLException e) {
            	throw new RuntimeException("Could not retrieve document data from database", e);
            } finally {
            	SqlUtil.close(rs);
            	SqlUtil.close(stmt);
            }
        }
    }

    private void retrieveNrDocuments(Connection conn) {
        if (this.id_collection > 0) {
            PreparedStatement stmt = null;
            ResultSet rs = null;
            try {
                //getting the number of documents on the collection
                stmt = sqlManager.getSqlStatement(conn, "SELECT.NUMBER.DOCUMENTS");
                stmt.setInt(1, this.id_collection);
                rs = stmt.executeQuery();

                if (rs.next()) {
                    this.nrDocuments = rs.getInt(1);
                } else {
                    throw new IllegalArgumentException("Problems retrieving the number of documents.");
                }
            } catch (SQLException e) {
            	throw new RuntimeException("Could not retrieve document data from database", e);
            } finally {
            	SqlUtil.close(rs);
            	SqlUtil.close(stmt);
            }
        }
    }

    public String getFullContent(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        StringBuilder content = new StringBuilder();
        try {
        	conn = connManager.getConnection();
            stmt = sqlManager.getSqlStatement(conn, "SELECT.CONTENT.DOCUMENT");
            stmt.setInt(1, id);
            stmt.setInt(2, this.id_collection);
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
        } catch (SQLException e) {
        	throw new RuntimeException("Could not retrieve document data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
        return content.toString();
    }

    public int getNumberOfUniqueReferences() {
        return this.n_unique_references;
    }

    public int getMinIdCitation() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int min_id;
        try {
        	conn = connManager.getConnection();
            stmt = sqlManager.getSqlStatement(conn, "SELECT.MIN.ID.CITATION");
            stmt.setInt(1, this.id_collection);
            rs = stmt.executeQuery();
            rs.next();
            min_id = rs.getInt(1);
            return min_id;
        } catch (SQLException e) {
        	throw new RuntimeException("Could not retrieve citation data from database", e);
    	} finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
    	}
    }

    private void getNumberOfUniqueReferences_Query(Connection conn) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = sqlManager.getSqlStatement(conn, "COUNT.UNIQUE.REFERENCES");
            stmt.setInt(1, id_collection);
            rs = stmt.executeQuery();
            rs.next();

            n_unique_references = rs.getInt(1);
        } catch (SQLException e) {
        	throw new RuntimeException("Could not retrieve citation data from database", e);
        } finally {
    		SqlUtil.close(rs);
    		SqlUtil.close(stmt);
    	}
    }

    private int getNumberOfSameAuthors(int id1, int id2) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
        	conn = connManager.getConnection();
            stmt = sqlManager.getSqlStatement(conn, "COAUTHORSHIP");
            stmt.setInt(1, id1);
            stmt.setInt(2, this.id_collection);
            stmt.setInt(3, id2);
            stmt.setInt(4, this.id_collection);
            rs = stmt.executeQuery();
            rs.next();

            int number = rs.getInt(1);
            return number;
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
    }

    public double getBibliographicCoupling_Log(int id1, int id2) {
        if (id1 > id2) {
            return this.norm_bc[id1][id2];
        } else {
            return this.norm_bc[id2][id1];
        }
    }

    private int getBibliographicCoupling(int id1, int id2) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
        	conn = connManager.getConnection();
            stmt = sqlManager.getSqlStatement(conn, "BIBLIOGRAPHIC.COUPLING");
            stmt.setInt(1, id1);
            stmt.setInt(2, this.id_collection);
            stmt.setInt(3, id2);
            stmt.setInt(4, this.id_collection);
            rs = stmt.executeQuery();
            rs.next();
            int bc = rs.getInt(1);
            return bc;
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
    }

    public String getViewContent(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        StringBuilder content = new StringBuilder();
        StringTokenizer tokenizer;


        try {
        	conn = connManager.getConnection();
            stmt = sqlManager.getSqlStatement(conn, "SELECT.AUTHORS.FROM.DOCUMENT");
            stmt.setInt(1, id);
            stmt.setInt(2, this.id_collection);
            rs = stmt.executeQuery();

            while (rs.next()) {
                content.append(rs.getString(1)).append("; ");
            }
            content.append("\r\n\n");
            stmt.close();
            rs.close();

            stmt = sqlManager.getSqlStatement(conn, "SELECT.CONTENT.DOCUMENT");
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
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
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
                in.close();

                file.delete();
            }
            if (bufferedWriterScalar != null) {
                bufferedWriterScalar.close();
            }
            out.close();
        }
    }

    public ArrayList<Ngram> getNgrams(int id) throws IOException {
        ArrayList<Ngram> ngrams = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
        	conn = connManager.getConnection();
            stmt = sqlManager.getSqlStatement(conn, "SELECT.NGRAMS.DOCUMENT");
            stmt.setInt(1, id);
            stmt.setInt(2, this.id_collection);
            rs = stmt.executeQuery();

            if (rs.next()) {
                InputStream is = rs.getBlob(1).getBinaryStream();
                ObjectInputStream ois = new ObjectInputStream(is);
                ngrams = (ArrayList<Ngram>) ois.readObject();
            }
            return ngrams;

        } catch (IOException | SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
        	SqlUtil.close(conn);
        }
    }

    public boolean doesThisDocumentCitesThisReference(int id_doc, int index_citation) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
        	conn = connManager.getConnection();
            stmt = sqlManager.getSqlStatement(conn, "DOES.THIS.DOCUMENT.CITES.THIS.REFERENCE");
            stmt.setInt(1, id_doc);
            stmt.setInt(2, index_citation);
            stmt.setInt(3, this.id_collection);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
    }
  
    public ArrayList<Reference> getCorpusReferences(int lower, int upper) {
        ArrayList<Reference> references = new ArrayList<>();
        Connection conn = null;
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

            conn = connManager.getConnection();
            stmt = sqlManager.createSqlStatement(conn, sql.toString());
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
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
        return references;
    }

    public ArrayList<Ngram> getCorpusNgrams() throws IOException {
        ArrayList<Ngram> ngrams = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
        	conn = connManager.getConnection();
            stmt = sqlManager.getSqlStatement(conn, "SELECT.NGRAMS.COLLECTION");
            stmt.setInt(1, this.id_collection);
            rs = stmt.executeQuery();

            if (rs.next()) {
                InputStream is = rs.getBlob(1).getBinaryStream();
                ObjectInputStream ois = new ObjectInputStream(is);
                ngrams = (ArrayList<Ngram>) ois.readObject();
            }
        } catch (IOException | SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
        return ngrams;
    }

    public int getNumberGrams() {
        int nrGrams = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
        	conn = connManager.getConnection();
            stmt = sqlManager.getSqlStatement(conn, "SELECT.NUMBER.GRAMS");
            stmt.setInt(1, this.id_collection);
            rs = stmt.executeQuery();

            if (rs.next()) {
                nrGrams = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
        return nrGrams;
    }

    public int[] getAscendingDates() {
        return this.ascending_dates;
    }

    private void retrievetAscendingDates(Connection conn) {
        PreparedStatement stmt = null;
        ResultSet rs = null;


        try {
            stmt = sqlManager.getSqlStatement(conn, "SELECT.DISTINCT.YEARS", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
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
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
        }
    }

    public int[] getDocumentsWithLCC(int value, String comparison) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int[] ids = null;
        StringBuilder sql_statement = new StringBuilder("SELECT ID_DOC FROM DOCUMENTS WHERE LCC ");
        sql_statement = sql_statement.append(comparison).append(" ").append(value);
        try {
        	conn = connManager.getConnection();
            stmt = sqlManager.createSqlStatement(conn, sql_statement.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery();
            rs.last();
            int size = rs.getRow();
            ids = new int[size];
            rs.beforeFirst();
            for (int i = 0; i < size; i++) {
                rs.next();
                ids[i] = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
        return ids;

    }

    public int[] getDocumentsWithGCC(int value, String comparison) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        int[] ids = null;

        try {
            StringBuilder sql_statement = new StringBuilder("SELECT ID_DOC FROM DOCUMENTS WHERE GCC ");
            sql_statement = sql_statement.append(comparison).append(" ").append(value);

            conn = connManager.getConnection();
            stmt = sqlManager.createSqlStatement(conn, sql_statement.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery();
            rs.last();
            int size = rs.getRow();
            ids = new int[size];
            rs.beforeFirst();
            for (int i = 0; i < size; i++) {
                rs.next();
                ids[i] = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
        return ids;
    }

    public TIntArrayList getDocumentsIdsFromYearToYear(int begin_year, int end_year) {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        TIntArrayList ids = new TIntArrayList();
        try {
        	conn = connManager.getConnection();
            stmt = sqlManager.getSqlStatement(conn, "SELECT.ID.DOCUMENTS.FROM.YEAR.TO.YEAR");
            stmt.setInt(1, this.id_collection);
            stmt.setInt(2, begin_year);
            stmt.setInt(3, end_year);
            rs = stmt.executeQuery();

            while (rs.next()) {
                ids.add(rs.getInt(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
        return ids;
    }

    public int[] getDocumentsIdsSortedByTitle(int year) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int[] ids = null;

        try {
        	conn = connManager.getConnection();
            stmt = sqlManager.getSqlStatement(conn, "SELECT.ID.DOCUMENTS.FROM.YEAR.ORDER.BY.TITLE.ORDER.BY.TITLE", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
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
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
        return ids;
    }

    public int[] getDocumentsFromAuthor(String author_name, int year) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int[] ids = null;
        try {
        	conn = connManager.getConnection();
            stmt = sqlManager.getSqlStatement(conn, "SELECT.ID.DOCUMENTS.FROM.AUTHOR.TO.YEAR", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
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
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
        return ids;
    }

    public Object[][] getMainAuthors(TIntArrayList docs_ids) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            StringBuilder docs = new StringBuilder(docs_ids.size() * 2);
            for (int i = 0; i < docs_ids.size() - 1; i++) {
                docs = docs.append(docs_ids.get(i)).append(", ");
            }
            docs = docs.append(docs_ids.get(docs_ids.size() - 1));

            StringBuilder sql = new StringBuilder("SELECT name, aUTHORS.ID_AUTHOR, COUNT(authors.id_author) as c FROM DOCUMENTS_TO_AUTHORS INNER JOIN authors where id_DOC in (" + docs + ") AND aUTHORS.ID_AUTHOR =dOCUMENTS_TO_AUTHORs.ID_AUTHOR AND AUTHORS.ID_COLLECTION =? GROUP BY Authors.ID_AUTHOR order by c desc");
        	conn = connManager.getConnection();
            stmt = sqlManager.createSqlStatement(conn, sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
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
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
    }

    public TIntIntHashMap searchTerm(String term) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            TIntIntHashMap aux = new TIntIntHashMap();

        	conn = connManager.getConnection();
            stmt = sqlManager.getSqlStatement(conn, "SEARCH.TERM");
            stmt.setString(1, term);
            stmt.setString(2, term);
            stmt.setString(3, term);
            stmt.setInt(4, this.id_collection);
            
            rs = stmt.executeQuery();
            while(rs.next()){
                aux.put(rs.getInt(1), rs.getInt(2));
            }
            return aux;
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
    }

    public int[] getDocumentsIds(int year) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int[] ids = null;

        try {
        	conn = connManager.getConnection();
            stmt = sqlManager.getSqlStatement(conn, "SELECT.ID.DOCUMENTS.FROM.YEAR", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
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
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
        return ids;
    }

    public String getAbstract(int id) {
        String abstractText = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
        	conn = connManager.getConnection();
            stmt = sqlManager.getSqlStatement(conn, "SELECT.DOCUMENT.ABSTRACT");
            stmt.setInt(1, this.id_collection);
            stmt.setInt(2, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                abstractText = rs.getString(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
        return abstractText;
    }

    public String getTitle(int id) {
        String title = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
        	conn = connManager.getConnection();
            stmt = sqlManager.getSqlStatement(conn, "SELECT.DOCUMENT.TITLE");
            stmt.setInt(1, this.id_collection);
            stmt.setInt(2, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                title = rs.getString(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
        return title;
    }

    public int getYear(int id) {
        int year = -1;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
        	conn = connManager.getConnection();
            stmt = sqlManager.getSqlStatement(conn, "SELECT.DOCUMENT.YEAR");
            stmt.setInt(1, this.id_collection);
            stmt.setInt(2, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                year = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
        return year;
    }

    public String getPDFFile(int id) {
        String pdfFile = "";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
        	conn = connManager.getConnection();
            stmt = sqlManager.getSqlStatement(conn, "SELECT.PDF.DOCUMENT");
            stmt.setInt(1, this.id_collection);
            stmt.setInt(2, id);

            rs = stmt.executeQuery();

            if (rs.next()) {
                pdfFile = rs.getString(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
        return pdfFile;
    }

    public String getKeywords(int id) {
        String keywords = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
        	conn = connManager.getConnection();
            stmt = sqlManager.getSqlStatement(conn, "SELECT.DOCUMENT.KEYWORDS");
            stmt.setInt(1, this.id_collection);
            stmt.setInt(2, id);

            rs = stmt.executeQuery();
            if (rs.next()) {
                keywords = rs.getString(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
        return keywords;
    }

    private String getReferenceText(int id_citation) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String text = null;
        try {
        	conn = connManager.getConnection();
            stmt = sqlManager.getSqlStatement(conn, "SELECT.REFERENCE");
            stmt.setInt(1, id_citation);
            stmt.setInt(2, id_collection);
            rs = stmt.executeQuery();
            if (rs.next()) {
                text = rs.getString(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
        return text;
    }

    public ArrayList<String> getReferences(int id) {
        ArrayList<String> references = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
        	conn = connManager.getConnection();
            stmt = sqlManager.getSqlStatement(conn, "SELECT.REFERENCES.DOCUMENT");
            stmt.setInt(1, id);
            stmt.setInt(2, this.id_collection);

            rs = stmt.executeQuery();
            while (rs.next()) {
                references.add(rs.getString(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
        return references;
    }

    private void matchCoreCitations(Connection conn) {
        for (int i = 0; i < this.documents_ids.length; i++) {
            this.citation_core.put(documents_ids[i], new ArrayList<Pair>());
        }

        PreparedStatement stmt = null;
        PreparedStatement stmt2 = null;
        PreparedStatement stmt3 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        int count;
        
        try {
            stmt = sqlManager.getSqlStatement(conn, "CORE.REFERENCES");
            stmt.setInt(1, id_collection);
            rs = stmt.executeQuery();

            int id_citation, id_doc_cited;
            while (rs.next()) {
                id_citation = rs.getInt(1);
                id_doc_cited = rs.getInt(2);

                stmt2 = sqlManager.getSqlStatement(conn, "DOCUMENTS.CITING.REFERENCE");
                stmt2.setInt(1, id_citation);
                stmt2.setInt(2, id_collection);
                rs2 = stmt2.executeQuery();

                count = 0;
                boolean contains = citation_core.containsKey(id_doc_cited);
                if (contains) {
                    while (rs2.next()) {
                        this.citation_core.get(id_doc_cited).add(new Pair(rs2.getInt(1), -1));
                        count++;
                    }
                }
 
                if (contains) {
                    stmt3 = sqlManager.getSqlStatement(conn, "UPDATE.LCC.DOCUMENT");
                    stmt3.setInt(1, count);
                    stmt3.setInt(2, id_doc_cited);
                    stmt3.setInt(3, id_collection);
                    stmt3.executeUpdate();
                    stmt3.close();
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(rs2);
            SqlUtil.close(stmt);
            SqlUtil.close(stmt2);
            SqlUtil.close(stmt3);
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

    public int getGlobalCitationCount(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
        	conn = connManager.getConnection();
            stmt = sqlManager.getSqlStatement(conn, "SELECT.TIMESCITED.DOCUMENT");
            stmt.setInt(1, id);
            stmt.setInt(2, this.id_collection);

            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
            	return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
    }

    public String getAuthors(int id) {
        String authors = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
        	conn = connManager.getConnection();
            stmt = sqlManager.getSqlStatement(conn, "SELECT.AUTHORS.DOCUMENT");
            stmt.setInt(1, this.id_collection);
            stmt.setInt(2, id);

            rs = stmt.executeQuery();
            if (rs.next()) {
                authors = rs.getString(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
        return authors;
    }

    public String getDOI(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = sqlManager.getSqlStatement(conn, "SELECT.DOCUMENT.DOI");
            stmt.setInt(1, this.id_collection);
            stmt.setInt(2, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString(1);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
    }

    public int getClass(int id) {
        int classId = -1;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
        	conn = connManager.getConnection();
            stmt = sqlManager.getSqlStatement(conn, "SELECT.DOCUMENT.CLASS");
            stmt.setInt(1, this.id_collection);
            stmt.setInt(2, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                classId = rs.getInt(1);
            }
            return classId;

        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
    }
}
