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
import java.sql.ResultSet;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import topicevolutionvis.database.CollectionsManager;
import topicevolutionvis.database.ConnectionManager;
import topicevolutionvis.database.DatabaseCorpus;
import topicevolutionvis.database.SqlManager;
import topicevolutionvis.dimensionreduction.DimensionalityReductionType;
import topicevolutionvis.graph.*;
import topicevolutionvis.projection.ProjectionData;
import topicevolutionvis.projection.distance.DissimilarityType;
import topicevolutionvis.util.PExConstants;
import topicevolutionvis.view.ScienceViewMainFrame;
import topicevolutionvis.view.tools.OpenProjectionDialog;

/**
 *
 * @author aretha
 */
public class OpenTemporalProjection extends SwingWorker<Void, Void> {

    private final String filename;
    private File xml_file, db_file;
    private final OpenProjectionDialog view;
    private TemporalProjection tproj;
    private ProjectionData pdata;
    private final TreeMap<Integer, TemporalGraph> graphs = new TreeMap<>();
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
            tproj.setProjectionData(pdata);
            sdots = tproj.addVertexScalar(PExConstants.DOTS);

            this.unzip();
            this.loadProjection();
            this.loadDatabase();

            this.createIntermediateGraphs();

            ScienceViewMainFrame.getInstance().addTemporalProjectionViewer(tproj);

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

    private void loadDatabase() throws Exception {

        String line;
        int id_collection = CollectionsManager.getNextCollectionId();
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(db_file), "UTF8"));
        Connection conn = ConnectionManager.getInstance().getConnection();
        while (((line = in.readLine()) != null)) {
            if (line.trim().compareToIgnoreCase("") != 0) {
                line = line.replace("???", Integer.toString(id_collection));
                try (PreparedStatement stmt = conn.prepareStatement(line)) {
                    stmt.executeUpdate();
                }
            }
        }
        pdata.setDatabaseCorpus(new DatabaseCorpus(pdata.getCollectionName()));

//        Properties props = new Properties();
//        props.load(new FileInputStream("./config/database.properties"));
//        RunScript.execute(props.getProperty("jdbc.url"), props.getProperty("jdbc.username"), props.getProperty("jdbc.password"), this.db_file.getAbsolutePath(), null, true);
//        pdata.setDatabaseCorpus(new DatabaseCorpus(pdata.getCollectionName()));
    }

    private void loadProjection() throws Exception {
        Document dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xml_file.getAbsolutePath());

        //get the root element
        Element docEle = dom.getDocumentElement();
        this.pdata.setCollectionName(getAttrOfElement(docEle, "collection-name", "value"));

        //checando se a coleção já existe
        try (PreparedStatement stmt = SqlManager.getInstance().getSqlStatement("SELECT.COLLECTION.BY.NAME", -1, -1)) {
            stmt.setString(1, pdata.getCollectionName());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id_aux = rs.getInt(1);
                    String message = "A dataset with the name \"" + pdata.getCollectionName() + "\" already exists in the database. \n"
                            + "Do you want to replace the existing dataset?";
                    int answer = JOptionPane.showOptionDialog(view, message, "Open Dataset Warning",
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
                    if (answer == JOptionPane.NO_OPTION) {
                        return;
                    } else {
                        try (PreparedStatement stmt2 = SqlManager.getInstance().getSqlStatement("SELECT.COLLECTION.BY.NAME", -1, -1)) {
                            stmt2.setString(1, Integer.toString(id_aux));
                            stmt2.executeUpdate();
                        }
                    }
                }
            }
        }

        this.pdata.setDissimilarityType(DissimilarityType.retrieve(getAttrOfElement(docEle, "distance-type", "value")));
        this.pdata.setDimensionReductionType(DimensionalityReductionType.retrieve(getAttrOfElement(docEle, "dimensionality-reduction", "value")));
        this.pdata.setNumberGrams(Integer.parseInt(getAttrOfElement(docEle, "number-grams", "value")));
        this.pdata.setSourceFile(getAttrOfElement(docEle, "source-file", "value"));
        this.pdata.setLunhLowerCut(Integer.parseInt(getAttrOfElement(docEle, "luhn-lower-cut", "value")));
        this.pdata.setLunhUpperCut(Integer.parseInt(getAttrOfElement(docEle, "luhn-upper-cut", "value")));

        this.parseProjections(docEle);
    }

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
                    //calculando a reta que passa pelo vértice v1 e pelo vértice v2
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
