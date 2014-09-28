/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.projection.temporal;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import topicevolutionvis.database.CollectionManager;
import topicevolutionvis.database.ConnectionManager;
import topicevolutionvis.database.DatabaseCorpus;
import topicevolutionvis.dimensionreduction.DimensionalityReductionType;
import topicevolutionvis.graph.*;
import topicevolutionvis.matrix.normalization.NormalizationType;
import topicevolutionvis.preprocessing.RepresentationType;
import topicevolutionvis.preprocessing.steemer.StemmerType;
import topicevolutionvis.preprocessing.transformation.MatrixTransformationType;
import topicevolutionvis.projection.ProjectionData;
import topicevolutionvis.projection.ProjectionType;
import topicevolutionvis.projection.distance.DissimilarityType;
import topicevolutionvis.projection.lsp.ControlPointsType;
import topicevolutionvis.topic.TopicData;
import topicevolutionvis.topic.TopicData.TopicType;
import topicevolutionvis.topic.TopicData.TopicVisualization;
import topicevolutionvis.util.PExConstants;
import topicevolutionvis.view.tools.OpenProjectionDialog;

/**
 *
 * @author aretha
 */
public class OpenTemporalProjection extends SwingWorker<Void, Void> {

    private String filename;
    private File xml_file, db_file;
    private OpenProjectionDialog view;
    private TemporalProjection tproj;
    private ProjectionData pdata;
    private TopicData tdata;
    private TreeMap<Integer, TemporalGraph> graphs = new TreeMap<>();
    private Scalar sdots;

    public OpenTemporalProjection(String filename, OpenProjectionDialog view) {
        this.filename = filename;
        this.view = view;
    }

    @Override
    protected Void doInBackground() {
        try {
            view.setStatus(true);
            tproj = new TemporalProjection();
            pdata = new ProjectionData();
            tdata = new TopicData();
            tproj.setTopicData(tdata);
            tproj.setProjectionData(pdata);
            sdots = tproj.addVertexScalar(PExConstants.DOTS);

            this.unzip();
            this.loadDatabase();
            this.loadProjection();

            //this.createIntermediateGraphs();
            //ScienceViewMainFrame.getInstance().addTemporalProjectionViewer(tproj);
        } catch (Exception ex) {
            Logger.getLogger(OpenTemporalProjection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void unzip() throws Exception {
        final int BUFFER = 2048;
        int count;
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(this.filename)));

        db_file = File.createTempFile("dabase", ".sql");
        if (zis.getNextEntry() != null) {
            byte data[] = new byte[BUFFER];
            // write the files to the disk
            FileOutputStream fos = new FileOutputStream(db_file);
            try (BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER)) {
                while ((count = zis.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, count);
                }
                dest.flush();
            }
        }

        xml_file = File.createTempFile("projection", ".xml");
        if (zis.getNextEntry() != null) {
            byte data[] = new byte[BUFFER];
            // write the files to the disk
            FileOutputStream fos = new FileOutputStream(xml_file);
            try (BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER)) {
                while ((count = zis.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, count);
                }
                dest.flush();
            }
        }
    }

    private void loadDatabase() {
        String line;
        CollectionManager cm = new CollectionManager();
        int id_collection = cm.getNextCollectionId();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(db_file), "UTF8")); Connection conn = ConnectionManager.getInstance().getConnection()) {
            line = in.readLine();
            if (line != null) {
                int index = line.indexOf("???") + 7;
                int index2 = line.indexOf('\'', index);
                String collection_name = line.substring(index, index2);
                int aux = cm.getCollectionId(collection_name);

                if (aux != -1) { //uma coleção com este nome já existe na base de dados
                    String message = "The collection \"" + collection_name + "\" already exists. \n"
                            + "Do you want to replace it?";
                    int answer = JOptionPane.showOptionDialog(this.view, message, "Warning",
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
                    if (answer == JOptionPane.YES_OPTION) {
                        cm.removeCollection(aux);
                        this.pdata.setCollectionName(collection_name);
                        line = line.replace("???", Integer.toString(id_collection));
                        try (PreparedStatement stmt = conn.prepareStatement(line)) {
                            stmt.executeUpdate();
                        } catch (SQLException ex) {
                            Logger.getLogger(OpenTemporalProjection.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        while ((line = in.readLine()) != null) {
                            if (line.trim().compareToIgnoreCase("") != 0) {
                                line = line.replace("???", Integer.toString(id_collection));
                                try (PreparedStatement stmt = conn.prepareStatement(line)) {
                                    stmt.executeUpdate();
                                } catch (SQLException ex) {
                                    Logger.getLogger(OpenTemporalProjection.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                        DatabaseCorpus databaseCorpus = new DatabaseCorpus(collection_name);
                        this.pdata.setDatabaseCorpus(databaseCorpus);
                    }
                } else {
                    this.pdata.setCollectionName(collection_name);
                    line = line.replace("???", Integer.toString(id_collection));
                    try (PreparedStatement stmt = conn.prepareStatement(line)) {
                        stmt.executeUpdate();
                    } catch (SQLException ex) {
                        Logger.getLogger(OpenTemporalProjection.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    while ((line = in.readLine()) != null) {
                        line = line.replace("'???'", Integer.toString(id_collection));
                        System.out.println(line);
                        try (PreparedStatement stmt = conn.prepareStatement(line)) {
                            stmt.executeUpdate();
                        } catch (SQLException ex) {
                            Logger.getLogger(OpenTemporalProjection.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    DatabaseCorpus databaseCorpus = new DatabaseCorpus(collection_name);
                    this.pdata.setDatabaseCorpus(databaseCorpus);
                }
            }
        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            Logger.getLogger(OpenTemporalProjection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | SQLException ex) {
            Logger.getLogger(OpenTemporalProjection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadProjection() {
        try {
            Document dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xml_file.getAbsolutePath());

            //get the root element
            Element docEle = dom.getDocumentElement();

            /* Projection Data parameters */
            this.pdata.setNumberOfDocuments(Integer.parseInt(getAttrOfElement(docEle, "number-documents", "value")));
            this.pdata.setRepresentationType(RepresentationType.retrieve(getAttrOfElement(docEle, "representation-type", "value")));
            this.pdata.setDissimilarityType(DissimilarityType.retrieve(getAttrOfElement(docEle, "dissimilarity-type", "value")));
            this.pdata.setMatrixTransformationType(MatrixTransformationType.retrieve(getAttrOfElement(docEle, "matrix-transformation-type", "value")));
            this.pdata.setDimensionReductionType(DimensionalityReductionType.retrieve(getAttrOfElement(docEle, "dimensionality-reduction-type", "value")));
            this.pdata.setNormalization(NormalizationType.retrieve(getAttrOfElement(docEle, "normalization-type", "value")));
            this.pdata.setStemmer(StemmerType.retrieve(getAttrOfElement(docEle, "stemmer-type", "value")));
            this.pdata.setNumberGrams(Integer.parseInt(getAttrOfElement(docEle, "number-grams", "value")));
            this.pdata.setSourceFile(getAttrOfElement(docEle, "source-file", "value"));
            this.pdata.setLunhLowerCut(Integer.parseInt(getAttrOfElement(docEle, "luhn-lower-cut", "value")));
            this.pdata.setLunhUpperCut(Integer.parseInt(getAttrOfElement(docEle, "luhn-upper-cut", "value")));
            this.pdata.setReferencesLowerCut(Integer.parseInt(getAttrOfElement(docEle, "references-lower-cut", "value")));
            this.pdata.setReferencesUpperCut(Integer.parseInt(getAttrOfElement(docEle, "references-upper-cut", "value")));
            this.pdata.setFractionDelta(Float.parseFloat(getAttrOfElement(docEle, "lsp-fraction-delta", "value")));
            this.pdata.setNumberIterations(Integer.parseInt(getAttrOfElement(docEle, "lsp-number-iterations", "value")));
            this.pdata.setNumberControlPoints(Integer.parseInt(getAttrOfElement(docEle, "lsp-number-control-points", "value")));
            this.pdata.setNumberNeighborsConnection(Integer.parseInt(getAttrOfElement(docEle, "lsp-number-neighbors-connections", "value")));
            this.pdata.setControlPointsChoice(ControlPointsType.retrieve(getAttrOfElement(docEle, "lsp-control-points-choice", "value")));

            /* Monic parameters*/
            if (getAttrOfElement(docEle, "dbscan-epsilon", "value") != null) {
                this.pdata.setTopicEvolutionGenerated(true);
                this.pdata.setEpsilon(Double.parseDouble(getAttrOfElement(docEle, "dbscan-epsilon", "value")));
                this.pdata.setMinPoint(Integer.parseInt(getAttrOfElement(docEle, "dbscan-minpoints", "value")));
                this.pdata.setTheta(Double.parseDouble(getAttrOfElement(docEle, "monic-theta", "value")));
                this.pdata.setThetaSplit(Double.parseDouble(getAttrOfElement(docEle, "monic-theta-split", "value")));
            }

            this.pdata.setProjectionType(ProjectionType.retrieve(getAttrOfElement(docEle, "projection-technique", "value")));

            /* Topic Data parameters */
            this.tdata.setTopicType(TopicType.valueOf(getAttrOfElement(docEle, "topic-type", "value")));
            this.tdata.setTypeOfTopicVisualization(TopicVisualization.valueOf(getAttrOfElement(docEle, "topic-visualization-type", "value")));
            if (this.tdata.getTopicType() == TopicData.TopicType.PCA) {
                this.tdata.setPcaMinInformationTerms(Float.parseFloat(getAttrOfElement(docEle, "pca-min-terms", "value")));
                this.tdata.setPcaInformationTopics(Float.parseFloat(getAttrOfElement(docEle, "pca-min-topics", "value")));
            } else if (this.tdata.getTopicType() == TopicData.TopicType.COVARIANCE) {
                this.tdata.setCovariancePercentageTerms(Float.parseFloat(getAttrOfElement(docEle, "covariance-min-terms", "value")));
                this.tdata.setCovariancePercentageTopics(Float.parseFloat(getAttrOfElement(docEle, "covariance-min-topics", "value")));
            } else if (this.tdata.getTopicType() == TopicData.TopicType.LDA) {
                this.tdata.setLdaNumberOfTopics(Integer.parseInt(getAttrOfElement(docEle, "lda-number-topics", "value")));
                this.tdata.setLdaNumberOfIterations(Integer.parseInt(getAttrOfElement(docEle, "lda-number-iterations", "value")));
                this.tdata.setLdaAlpha(Double.parseDouble(getAttrOfElement(docEle, "lda-alpha", "value")));
                this.tdata.setLdaBeta(Double.parseDouble(getAttrOfElement(docEle, "lda-beta", "value")));
            }

            //this.parseProjections(docEle);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(OpenTemporalProjection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /* TODO */
    private void parseProjections(Element parent) {
        Element el;
        Node node;
        TemporalGraph graph;
        Integer year;
        NodeList n = parent.getElementsByTagName("projection");
        for (int i = 0; i < n.getLength(); i++) {
            node = n.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                el = (Element) node;
                year = Integer.parseInt(el.getAttribute("year"));
                graph = new TemporalGraph(tproj, year, 0);
                this.parseVertexList(graph, el);
                this.parseConnectivities(year, el);
                this.graphs.put(graph.getYear(), graph);
            }
        }
        int[] years = new int[graphs.size()];
        Iterator<Integer> it = graphs.keySet().iterator();
        for (int i = 0; i < years.length; i++) {
            years[i] = it.next();
        }
        tproj.setYears(years);
    }

    private void parseConnectivities(int year, Element projection) {
        boolean weighted, directed;
        Connectivity con;
        Edge edge;
        ArrayList<Edge> edges;
        Element connectivity_element, edge_element;
        NodeList connectivities_list = projection.getElementsByTagName("edges-list");
        for (int i = 0; i < connectivities_list.getLength(); i++) {
            weighted = directed = false;
            connectivity_element = (Element) connectivities_list.item(i);
            String name = connectivity_element.getAttribute("name");

            if (connectivity_element.getAttribute("weighted").compareTo("true") == 0) {
                weighted = true;
            }
            if (connectivity_element.getAttribute("directed").compareTo("true") == 0) {
                directed = true;
            }
            con = new Connectivity(ConnectivityType.retrieve(name), weighted, directed);
            NodeList edges_list = connectivity_element.getChildNodes();
            edges = new ArrayList<>();
            float weight, min_weight = Float.MAX_VALUE, max_weight = Float.MIN_VALUE;

            for (int j = 0; j < edges_list.getLength(); j++) {
                edge_element = (Element) edges_list.item(j);
                if (weighted) {
                    weight = Float.parseFloat(edge_element.getAttribute("weight"));
                    if (weight < min_weight) {
                        min_weight = weight;
                    }
                    if (weight > max_weight) {
                        max_weight = weight;
                    }
                    edge = new Edge(Float.parseFloat(edge_element.getAttribute("weight")),
                            Integer.parseInt(edge_element.getAttribute("target")),
                            Integer.parseInt(edge_element.getAttribute("source")));
                } else {
                    edge = new Edge(Integer.parseInt(edge_element.getAttribute("target")),
                            Integer.parseInt(edge_element.getAttribute("source")));
                }
                edges.add(edge);
            }
            if (weighted) {
                con.setMinWeight(min_weight);
                con.setMaxWeight(max_weight);
            }
            con.setEdges(edges);
            tproj.addConnectivity(year, con);
        }
    }

    private void parseVertexList(TemporalGraph graph, Element projection) {
        int id;
        Vertex v;
        TIntObjectHashMap< Vertex> vertex = new TIntObjectHashMap<>();
        Element vertex_list = (Element) projection.getChildNodes().item(0), scalar_value, v_el;
        NodeList scalars_values, n_vertex;
        Scalar scalar;
        String scalar_name;
        if (vertex_list != null && vertex_list.getChildNodes().getLength() > 0) {
            n_vertex = vertex_list.getChildNodes();
            for (int i = 0; i < n_vertex.getLength(); i++) {
                v_el = (Element) n_vertex.item(i);
                id = Integer.parseInt(v_el.getAttribute("id"));
                v = new Vertex(id,
                        Float.parseFloat(((Element) v_el.getChildNodes().item(0)).getAttribute("value")),
                        Float.parseFloat(((Element) v_el.getChildNodes().item(1)).getAttribute("value")));
                v.setPublishedYear(Integer.parseInt(v_el.getAttribute("year")));
                //scalars
                scalars_values = (NodeList) v_el.getChildNodes().item(3).getChildNodes();
                for (int j = 0; j < scalars_values.getLength(); j++) {
                    scalar_value = (Element) scalars_values.item(j);
                    scalar_name = scalar_value.getAttribute("name");
                    scalar = tproj.getVertexScalarByName(scalar_name);
                    if (scalar == null) {
                        scalar = tproj.addVertexScalar(scalar_name);
                    }
                    v.setScalar(scalar, Float.parseFloat(scalar_value.getAttribute("value")));
                }
                vertex.put(id, v);
            }
        }
        graph.setVertex(vertex);
    }

//    private void parseEdgesList(TemporalGraph graph, Element projection) {
//        String name_con;
//        Element c;
//        Connectivity con = null;
//        NodeList n = projection.getElementsByTagName("edges-list");
//        for (int i = 0; i < n.getLength(); i++) {
//            c = (Element) n.item(i);
//            name_con = c.getAttribute("name");
//            if (name_con.compareToIgnoreCase(PExConstants.BIBLIOGRAPHIC_COUPLING) == 0
//                || name_con.compareToIgnoreCase(PExConstants.CO_AUTHORSHIP) == 0) {
//                con = new Connectivity(name_con, false, true);
//            } else if (name_con.compareToIgnoreCase(PExConstants.CORE_CITATIONS) == 0) {
//                con = new Connectivity(name_con, true, false);
//            }
//            graph.addConnectivity(con);
//
//        }
//    }
    private void createIntermediateGraphs() {
        int year;
        Map.Entry<Integer, TemporalGraph> entry;
        TemporalGraph graph1, graph2;
        Vertex v_ant;
        ArrayList<Vertex> newD = new ArrayList<>(), updatedD = new ArrayList<>(), fixedD = new ArrayList<>();
        for (int i = 0; i < tproj.getNumberOfYears(); i++) {
            year = tproj.getYearWithIndex(i);
            entry = graphs.lowerEntry(year);
            graph2 = graphs.get(year);
            if (entry != null) {
                graph1 = entry.getValue();
                TIntObjectIterator<Vertex> iterator = graph2.getVertex().iterator();
                while (iterator.hasNext()) {
                    iterator.advance();
                    Vertex v = iterator.value();
                    int v_year = v.getPublishedYear();
                    if (v_year == year) { //new document
                        newD.add(v);
                    } else {
                        v_ant = graph1.getVertexById(v.getId());
                        if (v_ant.getX() == v.getX() && v_ant.getY() == v.getY()) {
                            fixedD.add(v);
                        } else {
                            updatedD.add(v);
                        }
                    }
                }
                this.createIntermediateGraphs(year, graph1, newD, updatedD, fixedD);
            } else {
                TemporalGraph graph;
                TIntObjectHashMap< Vertex> vertex;
                Vertex newVertex;
                Connectivity dotsCon, new_con;
                ArrayList<TemporalGraph> graphsAux = new ArrayList<>();
                for (int j = 0; j < TemporalProjection.getN(); j++) {
                    graph = new TemporalGraph(tproj, year, j);
                    vertex = new TIntObjectHashMap<>();
                    TIntObjectIterator<Vertex> iterator = graph2.getVertex().iterator();
                    while (iterator.hasNext()) {
                        iterator.advance();
                        if (j != 0) {
                            Vertex oldVertex = iterator.value();
                            int id = oldVertex.getId();
                            newVertex = new Vertex(id, oldVertex.getX(), oldVertex.getY());
                            newVertex.setScalar(sdots, 0.0f);
                            newVertex.setPublishedYear(oldVertex.getPublishedYear());
                            newVertex.setTemporalProjection(tproj);
                            ArrayList<Scalar> scalars = tproj.getVertexScalars();
                            for (Scalar s : scalars) {
                                newVertex.setScalar(s, oldVertex.getScalar(s, false));
                            }
                            newVertex.setAlpha((1.0f / TemporalProjection.getN()) * j);
                            vertex.put(id, newVertex);
                        }
                    }
                    graph.setVertex(vertex);
                    graphsAux.add(graph);
                }
                this.tproj.addGraphs(year, graphsAux);
            }
        }
    }

    private void createIntermediateGraphs(Integer year, TemporalGraph graph1, ArrayList<Vertex> newDocuments, ArrayList<Vertex> updatedDocuments, ArrayList<Vertex> fixedDocuments) {
        TemporalGraph graph;
        Vertex newVertex, v1;
        TIntObjectHashMap< Vertex> vertex;
        ArrayList<TemporalGraph> graphsAux = new ArrayList<>();
        for (int i = 0; i < TemporalProjection.getN(); i++) {
            graph = new TemporalGraph(tproj, year, i);
            vertex = new TIntObjectHashMap<>();

            for (Vertex v : fixedDocuments) {
                newVertex = new Vertex(v.getId(), v.getX(), v.getY());
                newVertex.setScalar(sdots, 0.0f);
                newVertex.setPublishedYear(v.getPublishedYear());
                newVertex.setTemporalProjection(tproj);
                ArrayList<Scalar> scalars = tproj.getVertexScalars();
                for (Scalar s : scalars) {
                    newVertex.setScalar(s, v.getScalar(s, false));
                }
                vertex.put(v.getId(), newVertex);
            }
            for (Vertex v : newDocuments) {
                newVertex = new Vertex(v.getId(), v.getX(), v.getY());
                newVertex.setScalar(sdots, 0.0f);
                newVertex.setPublishedYear(v.getPublishedYear());
                newVertex.setTemporalProjection(tproj);
                ArrayList<Scalar> scalars = tproj.getVertexScalars();
                for (Scalar s : scalars) {
                    newVertex.setScalar(s, v.getScalar(s, false));
                }
                newVertex.setAlpha((1.0f / TemporalProjection.getN()) * i);
                vertex.put(v.getId(), newVertex);
            }
            if (updatedDocuments != null) {
                for (Vertex v2 : updatedDocuments) {
                    v1 = graph1.getVertexById(v2.getId());
                    //calculando a reta que passa pelo vÃ©rtice v1 e pelo vÃ©rtice v2
                    double x1 = v1.getX();
                    double y1 = v1.getY();
                    double x2 = v2.getX();
                    double y2 = v2.getY();
                    double a = (y2 - y1) / (x2 - x1);
                    double b = y1 - a * x1;
                    double x = 0;
                    double prop = (1.0 / TemporalProjection.getN()) * i;
                    if (x2 > x1) {
                        x = x1 + prop * (x2 - x1);
                    } else if (x1 > x2) {
                        x = x1 - prop * (x1 - x2);
                    }
                    double y = a * x + b;
                    newVertex = new Vertex(v1.getId(), (float) x, (float) y);
                    newVertex.setScalar(sdots, 0.0f);
                    newVertex.setPublishedYear(v1.getPublishedYear());
                    newVertex.setTemporalProjection(tproj);
                    ArrayList<Scalar> scalars = tproj.getVertexScalars();
                    for (Scalar s : scalars) {
                        newVertex.setScalar(s, v2.getScalar(s, false));
                    }
                    vertex.put(v2.getId(), newVertex);
                }
            }
            graph.setVertex(vertex);
            graphsAux.add(graph);
        }
        this.tproj.addGraphs(year, graphsAux);
    }

    private String getAttrOfElement(Element parent, String element, String attr) {
        NodeList n = parent.getElementsByTagName(element);
        if (n != null && n.getLength() > 0) {
            return ((Element) n.item(0)).getAttribute(attr);
        }
        return null;
    }

    @Override
    public void done() {
        this.view.setStatus(false);
        this.view.dispose();
    }
}
