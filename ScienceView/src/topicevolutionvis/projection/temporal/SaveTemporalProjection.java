/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.projection.temporal;

import gnu.trove.iterator.TIntObjectIterator;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import topicevolutionvis.database.ConnectionManager;
import topicevolutionvis.database.DatabaseCorpus;
import topicevolutionvis.database.SqlManager;
import topicevolutionvis.graph.*;
import topicevolutionvis.projection.ProjectionData;
import topicevolutionvis.topic.TopicData;
import topicevolutionvis.util.PExConstants;
import topicevolutionvis.view.SaveProjectionDialog;

/**
 *
 * @author aretha
 */
public class SaveTemporalProjection extends SwingWorker<Void, Void> {

    private String filename;
    private File db_file, xml_file;
    private SaveProjectionDialog view;
    private TemporalProjection tproj;
    private ConnectionManager connManager;
    private SqlManager sqlManager;

    public SaveTemporalProjection(String filename, SaveProjectionDialog view, TemporalProjection tproj) {
        this.filename = filename;
        this.view = view;
        this.tproj = tproj;
        connManager = ConnectionManager.getInstance();
        sqlManager = SqlManager.getInstance();

    }

    @Override
    protected Void doInBackground() throws Exception {
        this.save_database();
        this.save_projection();
        this.create_zip();
        this.db_file.deleteOnExit();
        this.xml_file.deleteOnExit();
        return null;
    }

    private void create_zip() {
        try {
            try (FileOutputStream dest = new FileOutputStream(this.filename); ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest))) {
                byte[] buffer = new byte[1024];

                int bytesRead;
                try (FileInputStream fi = new FileInputStream(db_file)) {
                    try (BufferedInputStream origin = new BufferedInputStream(fi, 1024)) {
                        ZipEntry entry = new ZipEntry(this.db_file.getName());
                        out.putNextEntry(entry);
                        while ((bytesRead = origin.read(buffer, 0, 1024)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                        out.closeEntry();
                    }
                }

                try (FileInputStream fi2 = new FileInputStream(xml_file); BufferedInputStream origin2 = new BufferedInputStream(fi2, 1024)) {
                    ZipEntry entry = new ZipEntry(this.xml_file.getName());
                    out.putNextEntry(entry);
                    while ((bytesRead = origin2.read(buffer, 0, 1024)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                    out.closeEntry();
                }
                out.finish();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SaveTemporalProjection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SaveTemporalProjection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void save_projection() {
        try {
            DatabaseCorpus database = tproj.getDatabaseCorpus();
            ProjectionData pdata = tproj.getProjectionData();
            TopicData tdata = tproj.getTopicData();
            Attr attr;
            Element topicdata_elem, topicdata_param, projectiondata, projection, vertexList, vertex, edges, edge, element, scalars, scalar;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            //root element
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("temporalprojection");
            doc.appendChild(rootElement);

            projectiondata = doc.createElement("projection-data");
            rootElement.appendChild(projectiondata);

            //collection name
            element = doc.createElement("collection-name");
            attr = doc.createAttribute("value");
            attr.setValue(database.getCollectionName());
            element.setAttributeNode(attr);
            projectiondata.appendChild(element);

            //number of documents
            element = doc.createElement("number-documents");
            attr = doc.createAttribute("value");
            attr.setValue(Integer.toString(database.getNumberOfDocuments()));
            element.setAttributeNode(attr);
            projectiondata.appendChild(element);

            //representation type
            element = doc.createElement("representation-type");
            attr = doc.createAttribute("value");
            attr.setValue(pdata.getRepresentationType().toString());
            element.setAttributeNode(attr);
            projectiondata.appendChild(element);

            //distance type
            element = doc.createElement("dissimilarity-type");
            attr = doc.createAttribute("value");
            attr.setValue(pdata.getDissimilarityType().toString());
            element.setAttributeNode(attr);
            projectiondata.appendChild(element);

            //matrix transformation type
            element = doc.createElement("matrix-transformation-type");
            attr = doc.createAttribute("value");
            attr.setValue(pdata.getMatrixTransformationType().toString());
            element.setAttributeNode(attr);
            projectiondata.appendChild(element);

            //dimensionality reduction
            element = doc.createElement("dimensionality-reduction-type");
            attr = doc.createAttribute("value");
            attr.setValue(pdata.getDimensionReductionType().toString());
            element.setAttributeNode(attr);
            projectiondata.appendChild(element);

            //normalizatioin type
            element = doc.createElement("normalization-type");
            attr = doc.createAttribute("value");
            attr.setValue(pdata.getNormalization().toString());
            element.setAttributeNode(attr);
            projectiondata.appendChild(element);

            //stemmer type
            element = doc.createElement("stemmer-type");
            attr = doc.createAttribute("value");
            attr.setValue(pdata.getStemmer().toString());
            element.setAttributeNode(attr);
            projectiondata.appendChild(element);

            //number of grams
            element = doc.createElement("number-grams");
            attr = doc.createAttribute("value");
            attr.setValue(Integer.toString(pdata.getNumberGrams()));
            element.setAttributeNode(attr);
            projectiondata.appendChild(element);

            //source file
            element = doc.createElement("source-file");
            attr = doc.createAttribute("value");
            attr.setValue(pdata.getSourceFile());
            element.setAttributeNode(attr);
            projectiondata.appendChild(element);

            //luhn lower and upper cut to terms
            element = doc.createElement("luhn-lower-cut");
            attr = doc.createAttribute("value");
            attr.setValue(Integer.toString(pdata.getLunhLowerCut()));
            element.setAttributeNode(attr);
            projectiondata.appendChild(element);

            element = doc.createElement("luhn-upper-cut");
            attr = doc.createAttribute("value");
            attr.setValue(Integer.toString(pdata.getLunhUpperCut()));
            element.setAttributeNode(attr);
            projectiondata.appendChild(element);

            //lower and upper cut to references
            element = doc.createElement("references-lower-cut");
            attr = doc.createAttribute("value");
            attr.setValue(Integer.toString(pdata.getReferencesLowerCut()));
            element.setAttributeNode(attr);
            projectiondata.appendChild(element);

            element = doc.createElement("references-upper-cut");
            attr = doc.createAttribute("value");
            attr.setValue(Integer.toString(pdata.getReferencesUpperCut()));
            element.setAttributeNode(attr);
            projectiondata.appendChild(element);

            element = doc.createElement("lsp-fraction-delta");
            attr = doc.createAttribute("value");
            attr.setValue(Float.toString(pdata.getFractionDelta()));
            element.setAttributeNode(attr);
            projectiondata.appendChild(element);

            element = doc.createElement("lsp-number-iterations");
            attr = doc.createAttribute("value");
            attr.setValue(Integer.toString(pdata.getNumberIterations()));
            element.setAttributeNode(attr);
            projectiondata.appendChild(element);

            element = doc.createElement("lsp-number-control-points");
            attr = doc.createAttribute("value");
            attr.setValue(Integer.toString(pdata.getNumberControlPoints()));
            element.setAttributeNode(attr);
            projectiondata.appendChild(element);

            element = doc.createElement("lsp-number-neighbors-connections");
            attr = doc.createAttribute("value");
            attr.setValue(Integer.toString(pdata.getNumberNeighborsConnection()));
            element.setAttributeNode(attr);
            projectiondata.appendChild(element);

            element = doc.createElement("lsp-control-points-choice");
            attr = doc.createAttribute("value");
            attr.setValue(pdata.getControlPointsChoice().toString());
            element.setAttributeNode(attr);
            projectiondata.appendChild(element);

            if (pdata.isTopicEvolutionGenerated()) {
                element = doc.createElement("dbscan-epsilon");
                attr = doc.createAttribute("value");
                attr.setValue(Double.toString(pdata.getEpsilon()));
                element.setAttributeNode(attr);
                projectiondata.appendChild(element);

                element = doc.createElement("dbscan-minpoints");
                attr = doc.createAttribute("value");
                attr.setValue(Integer.toString(pdata.getMinPoints()));
                element.setAttributeNode(attr);
                projectiondata.appendChild(element);

                element = doc.createElement("monic-theta");
                attr = doc.createAttribute("value");
                attr.setValue(Double.toString(pdata.getTheta()));
                element.setAttributeNode(attr);
                projectiondata.appendChild(element);

                element = doc.createElement("monic-theta-split");
                attr = doc.createAttribute("value");
                attr.setValue(Double.toString(pdata.getThetaSplit()));
                element.setAttributeNode(attr);
                projectiondata.appendChild(element);
            }   //projection technique

            element = doc.createElement("projection-technique");
            attr = doc.createAttribute("value");
            attr.setValue(pdata.getProjectionType().toString());
            element.setAttributeNode(attr);
            projectiondata.appendChild(element);

            topicdata_elem = doc.createElement("topic-data");
            rootElement.appendChild(topicdata_elem);

            element = doc.createElement("topic-type");
            attr = doc.createAttribute("value");
            attr.setValue(tdata.getTopicType().toString());
            element.setAttributeNode(attr);
            topicdata_elem.appendChild(element);

            element = doc.createElement("topic-visualization-type");
            attr = doc.createAttribute("value");
            attr.setValue(tdata.getTypeOfTopicVisualization().toString());
            element.setAttributeNode(attr);
            topicdata_elem.appendChild(element);

            if (tdata.getTopicType() == TopicData.TopicType.COVARIANCE) {
                topicdata_param = doc.createElement("topic-data-parameters");
                topicdata_elem.appendChild(topicdata_param);

                element = doc.createElement("covariance-min-terms");
                attr = doc.createAttribute("value");
                attr.setValue(Float.toString(tdata.getCovariancePercentageTerms()));
                element.setAttributeNode(attr);
                topicdata_param.appendChild(element);

                element = doc.createElement("covariance-min-topics");
                attr = doc.createAttribute("value");
                attr.setValue(Float.toString(tdata.getCovariancePercentageTopics()));
                element.setAttributeNode(attr);
                topicdata_param.appendChild(element);

            } else if (tdata.getTopicType() == TopicData.TopicType.PCA) {
                topicdata_param = doc.createElement("topic-data-parameters");
                topicdata_elem.appendChild(topicdata_param);

                element = doc.createElement("pca-min-terms");
                attr = doc.createAttribute("value");
                attr.setValue(Float.toString(tdata.getPcaMinInformationTerms()));
                element.setAttributeNode(attr);
                topicdata_param.appendChild(element);

                element = doc.createElement("pca-min-topics");
                attr = doc.createAttribute("value");
                attr.setValue(Float.toString(tdata.getPcaInformationTopics()));
                element.setAttributeNode(attr);
                topicdata_param.appendChild(element);

            } else if (tdata.getTopicType() == TopicData.TopicType.LDA) {
                topicdata_param = doc.createElement("topic-data-parameters");
                topicdata_param.appendChild(topicdata_param);

                element = doc.createElement("lda-number-topics");
                attr = doc.createAttribute("value");
                attr.setValue(Integer.toString(tdata.getLdaNumberOfTopics()));
                element.setAttributeNode(attr);
                topicdata_param.appendChild(element);

                element = doc.createElement("lda-number-iterations");
                attr = doc.createAttribute("value");
                attr.setValue(Integer.toString(tdata.getLdaNumberOfIterations()));
                element.setAttributeNode(attr);
                topicdata_param.appendChild(element);

                element = doc.createElement("lda-alpha");
                attr = doc.createAttribute("value");
                attr.setValue(Double.toString(tdata.getLdaAlpha()));
                element.setAttributeNode(attr);
                topicdata_param.appendChild(element);

                element = doc.createElement("lda-beta");
                attr = doc.createAttribute("value");
                attr.setValue(Double.toString(tdata.getLdaBeta()));
                element.setAttributeNode(attr);
                topicdata_param.appendChild(element);

            }
//
//        //initial number of control points
//        element = doc.createElement("initial-number-control-points");
//        attr = doc.createAttribute("value");
//        attr.setValue(Integer.toString(tproj.getProjectionData().getNumberControlPoints()));
//        element.setAttributeNode(attr);
//        rootElement.appendChild(element);
//
//        //number of neighbors
//        element = doc.createElement("number-neighbors");
//        attr = doc.createAttribute("value");
//        attr.setValue(Integer.toString(tproj.getProjectionData().getNumberNeighborsConnection()));
//        element.setAttributeNode(attr);
//        rootElement.appendChild(element);
            TemporalGraph graph;
            for (ArrayList<TemporalGraph> intermediates : tproj.getGraphs().values()) {
                graph = intermediates.get(TemporalProjection.getN() - 1);
                projection = doc.createElement("projection");
                rootElement.appendChild(projection);

                attr = doc.createAttribute("year");
                attr.setValue(Integer.toString(graph.getYear()));
                projection.setAttributeNode(attr);

                vertexList = doc.createElement("vertex-list");
                projection.appendChild(vertexList);

                TIntObjectIterator<Vertex> iterator = graph.getVertex().iterator();
                while (iterator.hasNext()) {
                    iterator.advance();
                    Vertex v = iterator.value();
                    vertex = doc.createElement("vertex");
                    vertexList.appendChild(vertex);

                    //set id attr
                    attr = doc.createAttribute("id");
                    attr.setValue(Integer.toString(v.getId()));
                    vertex.setAttributeNode(attr);

                    //set year attr
                    attr = doc.createAttribute("year");
                    attr.setValue(Integer.toString(v.getPublishedYear()));
                    vertex.setAttributeNode(attr);

                    //x coordinate
                    element = doc.createElement("x-coordinate");
                    attr = doc.createAttribute("value");
                    attr.setValue(Double.toString(v.getX()));
                    element.setAttributeNode(attr);
                    vertex.appendChild(element);

                    //xcoordinate
                    element = doc.createElement("y-coordinate");
                    attr = doc.createAttribute("value");
                    attr.setValue(Double.toString(v.getY()));
                    element.setAttributeNode(attr);
                    vertex.appendChild(element);

                    //title
                    element = doc.createElement("label");
                    attr = doc.createAttribute("title");
                    attr.setValue(tproj.getTitleDocument(v.getId()));
                    element.setAttributeNode(attr);
                    vertex.appendChild(element);

                    scalars = doc.createElement("scalars");
                    for (Scalar s : tproj.getVertexScalars()) {
                        if (s.toString().compareTo(PExConstants.DOTS) != 0) {
                            scalar = doc.createElement("scalar");
                            attr = doc.createAttribute("name");
                            attr.setValue(s.toString());
                            scalar.setAttributeNode(attr);
                            attr = doc.createAttribute("value");
                            attr.setValue(Double.toString(v.getScalar(s, false)));
                            scalar.setAttributeNode(attr);
                            scalars.appendChild(scalar);
                        }
                    }
                }

//            for (Connectivity con : tproj.getConnectivities(graph.getYear())) {
//                if (con.getName().compareTo(PExConstants.DOTS) != 0) {
//                    edges = doc.createElement("edges-list");
//                    attr = doc.createAttribute("name");
//                    attr.setValue(con.getName());
//                    edges.setAttributeNode(attr);
//
//                    attr = doc.createAttribute("weighted");
//                    if (con.isWeighted()) {
//                        attr.setValue("true");
//                    } else {
//                        attr.setValue("false");
//                    }
//                    edges.setAttributeNode(attr);
//
//                    attr = doc.createAttribute("directed");
//                    if (con.isDirected()) {
//                        attr.setValue("true");
//                    } else {
//                        attr.setValue("false");
//                    }
//                    edges.setAttributeNode(attr);
//
//                    projection.appendChild(edges);
//
//                    for (Edge e : con.getEdges()) {
//                        edge = doc.createElement("edge");
//                        attr = doc.createAttribute("source");
//                        attr.setValue(Integer.toString(e.getSource()));
//                        edge.setAttributeNode(attr);
//                        attr = doc.createAttribute("target");
//                        attr.setValue(Integer.toString(e.getTarget()));
//                        edge.setAttributeNode(attr);
//                        attr = doc.createAttribute("weight");
//                        attr.setValue(Float.toString(e.getWeight()));
//                        edge.setAttributeNode(attr);
//                        edges.appendChild(edge);
//                    }
//                }
//            }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            xml_file = File.createTempFile("projection", ".xml");
            StreamResult result = new StreamResult(this.xml_file);
            transformer.transform(source, result);
        } catch (ParserConfigurationException | TransformerException | IOException ex) {
            Logger.getLogger(SaveTemporalProjection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void saveCitations(BufferedWriter writer, int id_collection) {

        //scrip para tabela CITATIONS
        try (
                Connection conn = connManager.getConnection();
                PreparedStatement stmt = sqlManager.getSqlStatement(conn, "SAVE.SCRIPT.REFERENCES")) {
            stmt.setInt(1, id_collection);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    writer.newLine();
                    writer.write("INSERT INTO CITATIONS VALUES (");
                    processInt(writer, rs.getInt(1), false); //id_citation
                    processString(writer, "???", false); //ID_COLLECTION
                    processString(writer, rs.getString(3), false); //TYPE
                    processInt(writer, rs.getInt(4), false); //id_author
                    processInt(writer, rs.getInt(5), false); //year
                    processString(writer, rs.getString(6), false); //JOURNAL
                    processString(writer, rs.getString(7), false); //VOLUME
                    processString(writer, rs.getString(8), false); //CHAPTER
                    processString(writer, rs.getString(9), false); //DOI
                    processString(writer, rs.getString(10), false); //PAGES
                    processString(writer, rs.getString(11), false); //ARTN
                    processString(writer, rs.getString(12), false); //FULL_REFERENCE
                    processInt(writer, rs.getInt(13), true); //ID_DOC_CORE     
                    writer.flush();
                }
            }
        } catch (SQLException | IOException ex) {
            Logger.getLogger(SaveTemporalProjection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void saveCollections(BufferedWriter writer, int id_collection) {
        //scrip para tabela COLLECTIONS
        try (Connection conn = connManager.getConnection();
                PreparedStatement stmt = sqlManager.getSqlStatement(conn, "SAVE.SCRIPT.COLLECTIONS")) {
            stmt.setInt(1, id_collection);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    writer.write("INSERT INTO COLLECTIONS VALUES (");
                    processString(writer, "???", false); //ID_COLLECTION
                    processString(writer, rs.getString(2), false); //NAME
                    processString(writer, rs.getString(3), false); //FILENAME
                    processInt(writer, rs.getInt(4), false); //NRGRAMS
                    processString(writer, rs.getString(5), false); //FORMAT
                    writer.write("X");
                    processString(writer, rs.getString(6), true); //GRAMS                  
                    writer.flush();
                }
            }
        } catch (SQLException | IOException ex) {
            Logger.getLogger(SaveTemporalProjection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void saveDocuments(BufferedWriter writer, int id_collection) {
        //scrip para tabela DOCUMENTS
        try (Connection conn = connManager.getConnection();
                PreparedStatement stmt = sqlManager.getSqlStatement(conn, "SAVE.SCRIPT.DOCUMENTS")) {
            stmt.setInt(1, id_collection);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    writer.newLine();
                    writer.write("INSERT INTO DOCUMENTS VALUES (");
                    processInt(writer, rs.getInt(1), false); //ID_DOC
                    processString(writer, "???", false); //ID_COLLECTION
                    processInt(writer, rs.getInt(3), false); //TYPE
                    processString(writer, rs.getString(4), false); //TITLE
                    processString(writer, rs.getString(5), false); //RESEARCH_ADDRESS
                    processString(writer, rs.getString(6), false); //ABSTRACT
                    processString(writer, rs.getString(7), false); //KEYWORDS
                    processString(writer, rs.getString(8), false); //AUTHORS_KEYWORDS
                    processInt(writer, rs.getInt(9), false); //YEAR
                    processInt(writer, rs.getInt(10), false); //GCC
                    processInt(writer, rs.getInt(11), false); //LCC
                    processString(writer, rs.getString(12), false); //DOI
                    processString(writer, rs.getString(13), false); //FIRST_PAGE
                    processString(writer, rs.getString(14), false); //END_PAGE
                    processString(writer, rs.getString(15), false); //PDF_FILE
                    processString(writer, rs.getString(16), false); //JOURNAL
                    processString(writer, rs.getString(17), false); //JOURNAL_ABBREV
                    processString(writer, rs.getString(18), false); //VOLUME
                    processInt(writer, rs.getInt(19), false); //CLASS
                    writer.write("X");
                    processString(writer, rs.getString(20), true); //GRAMS
                    writer.flush();
                }
            }
        } catch (SQLException | IOException ex) {
            Logger.getLogger(SaveTemporalProjection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void saveDocumentsToCitations(BufferedWriter writer, int id_collection) {

        //scrip para tabela DOCUMENTS_TO_CITATIONS
        try (Connection conn = connManager.getConnection();
                PreparedStatement stmt = sqlManager.getSqlStatement(conn, "SAVE.SCRIPT.DOCUMENTS.TO.REFERENCES")) {
            stmt.setInt(1, id_collection);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    writer.newLine();
                    writer.write("INSERT INTO DOCUMENTS_TO_CITATIONS VALUES (");
                    processInt(writer, rs.getInt(1), false); //ID_DOC
                    processString(writer, "???", false); //ID_COLLECTION
                    processInt(writer, rs.getInt(3), true); //ID_CITATION
                    writer.flush();
                }
            }
        } catch (SQLException | IOException ex) {
            Logger.getLogger(SaveTemporalProjection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void saveDocumentsToAuthors(BufferedWriter writer, int id_collection) {
        try (Connection conn = connManager.getConnection();
                PreparedStatement stmt = sqlManager.getSqlStatement(conn, "SAVE.SCRIPT.DOCUMENTS.TO.AUTHORS")) {
            stmt.setInt(1, id_collection);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    writer.newLine();
                    writer.write("INSERT INTO PUBLIC.DOCUMENTS_TO_AUTHORS VALUES (");
                    processInt(writer, rs.getInt(1), false); //ID_DOC
                    processString(writer, "???", false); //ID_COLLECTION
                    processInt(writer, rs.getInt(3), false); //ID_AUTHOR
                    processInt(writer, rs.getInt(4), true); //AUTHOR_ORDER
                    writer.flush();
                }
            }
        } catch (SQLException | IOException ex) {
            Logger.getLogger(SaveTemporalProjection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void saveAuthors(BufferedWriter writer, int id_collection) {
        try {
            try (Connection conn = connManager.getConnection();
                    PreparedStatement stmt = sqlManager.getSqlStatement(conn, "SAVE.SCRIPT.AUTHORS")) {
                stmt.setInt(1, id_collection);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        writer.newLine();
                        writer.write("INSERT INTO AUTHORS (id_author, name, id_collection) VALUES (");
                        processInt(writer, rs.getInt(1), false); //ID_AUTHOR
                        processString(writer, rs.getString(2), false); //NAME
                        processString(writer, "???", true); //ID_COLLECTION
                        writer.flush();
                    }
                }
            }
        } catch (SQLException | IOException ex) {
            Logger.getLogger(SaveTemporalProjection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void save_database() {
        try {
            db_file = File.createTempFile("database", ".sql");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(db_file), 1024)) {
                int id_collection = tproj.getDatabaseCorpus().getCollectionId();

                this.saveCollections(writer, id_collection);
                this.saveAuthors(writer, id_collection);
                this.saveCitations(writer, id_collection);
                this.saveDocuments(writer, id_collection);
                this.saveDocumentsToCitations(writer, id_collection);
                this.saveDocumentsToAuthors(writer, id_collection);
            }
        } catch (IOException ex) {
            Logger.getLogger(SaveTemporalProjection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processInt(BufferedWriter writer, int p, boolean ultimo_campo) {
        try {
            if (ultimo_campo == false) {
                writer.write(p + ", ");
            } else {
                writer.write(p + ");");
            }
        } catch (IOException ex) {
            Logger.getLogger(SaveTemporalProjection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processString(BufferedWriter writer, String p, boolean ultimo_campo) {
        try {
            if (p != null) {
                if (ultimo_campo == false) {
                    writer.write("'" + p.replace("'", "''") + "', ");
                } else {
                    writer.write("'" + p.replace("'", "''") + "');");
                }

            } else {
                if (ultimo_campo == false) {
                    writer.write("NULL, ");
                } else {
                    writer.write("NULL);");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(SaveTemporalProjection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void done() {
        this.view.setStatus(false);
        JOptionPane.showMessageDialog(view, "Temporal Projection and Database successfully saved", "Message", JOptionPane.INFORMATION_MESSAGE);
        this.view.dispose();
    }
}
