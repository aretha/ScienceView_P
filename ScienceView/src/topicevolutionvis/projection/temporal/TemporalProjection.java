/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.projection.temporal;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import topicevolutionvis.database.DatabaseCorpus;
import topicevolutionvis.datamining.clustering.monic.ExternalTransitions;
import topicevolutionvis.graph.*;
import topicevolutionvis.graph.scalar.QuerySolver;
import topicevolutionvis.projection.ProjectionData;
import topicevolutionvis.topic.TopicData;
import topicevolutionvis.util.Utils;

/**
 *
 * @author Aretha
 */
public class TemporalProjection implements Cloneable {

    //control the current graph being displayed on the screen
    private ProjectionData pdata;
    protected TopicData tdata = new TopicData();
    private int[] years;
    private ArrayList<Scalar> vertexScalars = new ArrayList<>();
    private TreeMap<Integer, ArrayList<TemporalGraph>> graphs = new TreeMap<>();
    private TIntObjectHashMap<String> titles = new TIntObjectHashMap<>();
    private final TIntObjectHashMap<ArrayList<Connectivity>> connectivities = new TIntObjectHashMap<>();
    private static final int N = 60;      //número de grafos intermediários que devem ser criados para cada ano
    private double minX = Double.POSITIVE_INFINITY;
    private double maxX = Double.NEGATIVE_INFINITY;
    private double minY = Double.POSITIVE_INFINITY;
    private double maxY = Double.NEGATIVE_INFINITY;
    private TIntArrayList selected_docs;
    private ExternalTransitions transitions = null;

    @Override
    public Object clone() {
        TemporalProjection proj = new TemporalProjection();
        proj.setProjectionData(this.pdata);
        proj.setTopicData(this.tdata);
        proj.setGraphs(this.graphs);
        proj.setYears(this.years);
        proj.setVertexScalars(this.vertexScalars);
        proj.setMinx(this.minX);
        proj.setMiny(this.minY);
        proj.setMaxx(this.maxX);
        proj.setMaxy(this.maxY);
        return proj;
    }

    public void setSelectedDocs(TIntArrayList selected_docs) {
        this.selected_docs = selected_docs;
    }

    public void clearSelectedDocs() {
        this.selected_docs.clear();
    }

    public TIntArrayList getSelectedDocs() {
        return this.selected_docs;
    }

    public void setVertexScalars(ArrayList<Scalar> vertexScalars) {
        this.vertexScalars = vertexScalars;
    }

    public void setProjectionData(ProjectionData pdata) {
        this.pdata = pdata;
        this.titles = new TIntObjectHashMap<>(pdata.getNumberOfDocuments());
    }

    public void setTitleDocument(int id_doc, String title) {
        this.titles.put(id_doc, title);
    }

    public String getTitleDocument(int id_doc) {
        return this.titles.get(id_doc);
    }

    public void setTopicData(TopicData tdata) {
        this.tdata = tdata;
    }

    public DatabaseCorpus getDatabaseCorpus() {
        return this.pdata.getDatabaseCorpus();
    }

    public TopicData getTopicData() {
        return this.tdata;
    }

    public void updateScalarForIntermediates(Scalar scalar, boolean normalized) {
        int year;
        TemporalGraph graph;
        for (ArrayList<TemporalGraph> intermediates : this.graphs.values()) {
            graph = intermediates.get(N - 1);
            year = graph.getYear();
            for (TemporalGraph graph_ant : this.graphs.get(year)) {
                if (graph_ant.getVertex().size() > 0) {
                    TIntObjectIterator<Vertex> iterator = graph_ant.getVertex().iterator();
                    while (iterator.hasNext()) {
                        iterator.advance();
                        Vertex v = iterator.value();
                        v.setScalar(scalar, graph.getVertexById(v.getId()).getScalar(scalar, normalized));
                    }
                }
            }
        }
    }

    public int nextYearAfter(int year) {
        int index = Utils.indexOf(years, year);
        if (index + 1 < years.length) {
            return years[index + 1];
        }
        return -1;
    }

    public TemporalGraph getMainGraph(int year) {
        return this.graphs.get(year).get(N - 1);
    }

    public ArrayList<TemporalGraph> getMainGraphs() {
        ArrayList<TemporalGraph> main_graphs = new ArrayList<>();
        for (int i = 0; i < this.years.length; i++) {
            main_graphs.add(this.getMainGraph(years[i]));
        }
        return main_graphs;
    }

    public Scalar addVertexScalar(String name) {
        Scalar scalar = new Scalar(name);
        if (!this.vertexScalars.contains(scalar)) {
            this.vertexScalars.add(scalar);
        }
        scalar.setIndex(this.vertexScalars.indexOf(scalar));
        return this.vertexScalars.get(scalar.getIndex());
    }

    public void addConnectivity(int year, Connectivity con) {
        if (this.connectivities.containsKey(year)) {
            this.connectivities.get(year).add(con);
        } else {
            ArrayList<Connectivity> cons = new ArrayList<>();
            cons.add(con);
            this.connectivities.put(year, cons);
        }
    }

    public Connectivity getConnectivity(int year, ConnectivityType type) {
        for (Connectivity con : this.connectivities.get(year)) {
            if (con.getType().equals(type)) {
                return con;
            }
        }
        return null;

    }

    public ArrayList<Connectivity> getConnectivities(int year) {
        return this.connectivities.get(year);
    }

    public void setConnectivities(int year, ArrayList<Connectivity> cons) {
        this.connectivities.put(year, cons);
    }

    public ArrayList<Scalar> getVertexScalars() {
        return this.vertexScalars;
    }

    public Scalar getVertexScalarByName(String name) {
        for (Scalar s : this.vertexScalars) {
            if (s.toString().equals(name)) {
                return s;
            }
        }
        return null;
    }

    public Scalar createQueryScalar(String word) throws IOException {
        //Adding a new scalar
        String scalarName = "'" + word + "'";
        Scalar scalar = this.addVertexScalar(scalarName);
        QuerySolver qS = new QuerySolver(this);
        qS.createCdata(scalar, this.pdata.getDatabaseCorpus().searchTerm(word), this.pdata.getDatabaseCorpus().getNumberOfDocuments());
        return scalar;
    }

    public ProjectionData getProjectionData() {
        return this.pdata;
    }

    public TemporalGraph getLastGraph() {
        return this.graphs.lastEntry().getValue().get(N - 1);
    }

    public TreeMap<Integer, ArrayList<TemporalGraph>> getGraphs() {
        return this.graphs;
    }

    public void setGraphs(TreeMap<Integer, ArrayList<TemporalGraph>> intermediateGraphs) {
        this.graphs = intermediateGraphs;
    }

    public void addGraphs(Integer year, ArrayList<TemporalGraph> graphs) {
        this.graphs.put(year, graphs);
    }

    public int getNumberOfYears() {
        return this.years.length;
    }

    public double getMinx() {
        return minX;
    }

    public void setMinx(double minx) {
        this.minX = minx;
    }

    public double getMaxx() {
        return maxX;
    }

    public void setMaxx(double maxX) {
        this.maxX = maxX;
    }

    public double getMiny() {
        return minY;
    }

    public void setMiny(double minY) {
        this.minY = minY;
    }

    public double getMaxy() {
        return maxY;
    }

    public void setMaxy(double maxY) {
        this.maxY = maxY;
    }

    public void setYears(int[] b) {
        years = new int[b.length];
        System.arraycopy(b, 0, years, 0, b.length);
    }

    public int[] getYears() {
        return this.years;
    }

    public int getYearWithIndex(int index) {
        if (index < this.years.length) {
            return this.years[index];
        }
        return this.years[this.years.length - 1];
    }

    public void setExternalTransitions(ExternalTransitions transitions) {
        this.transitions = transitions;
    }

    public ExternalTransitions getExternalTransitions() {
        return this.transitions;
    }

    public void clearExternalTransitions() {
        this.transitions.clear();
    }

    /**
     * Returns the number of intermediate graphs between each year.
     *
     * @return number of intermediate graphs between each year
     */
    public static int getN() {
        return N;
    }
}
