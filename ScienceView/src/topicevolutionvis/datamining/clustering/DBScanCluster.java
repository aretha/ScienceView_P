/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.datamining.clustering;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.array.TIntArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.SwingWorker;
import topicevolutionvis.graph.Scalar;
import topicevolutionvis.graph.TemporalGraph;
import topicevolutionvis.graph.Vertex;
import topicevolutionvis.projection.temporal.TemporalProjection;
import topicevolutionvis.topic.TopicFactory;
import topicevolutionvis.view.TemporalProjectionViewer;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.DBScan;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author Aretha
 */
public class DBScanCluster extends SwingWorker<Void, Void> {

    private TemporalProjection projection;
    private TemporalProjectionViewer viewer;
    private DBScanSettings view;
    private double eps;
    private int minpts;
    public Scalar sdbscan;

    public DBScanCluster(TemporalProjectionViewer viewer, DBScanSettings view, double eps, int minpts) throws Exception {
        this.projection = viewer.getTemporalProjection();
        this.viewer = viewer;
        this.eps = eps;
        this.view = view;
        this.minpts = minpts;

    }

    @Override
    protected Void doInBackground() throws Exception {

        sdbscan = projection.addVertexScalar("dbscan");

        view.setStatus("Clustering...", true);

        FastVector atts = new FastVector();
        atts.addElement(new Attribute("x-pos"));
        atts.addElement(new Attribute("y-pos"));

        int index;
        int[] ids;
        double[] assignments;
        String[] options;
        Instance instance;
        Instances data;
        DBScan clusterer;
        ClusterEvaluation eval;
        HashMap<Double, TIntArrayList> map;
        for (ArrayList<TemporalGraph> graphs : projection.getGraphs().values()) {
            for (TemporalGraph graph : graphs) {
                data = new Instances("Teste", atts, graph.getVertex().size());
                ids = new int[graph.getVertex().size()];
                index = 0;
                TIntObjectIterator<Vertex> iterator = graph.getVertex().iterator();
                while (iterator.hasNext()) {
                    iterator.advance();
                    Vertex v = iterator.value();
                    instance = new Instance(2);
                    instance.setValue(0, v.getX());
                    instance.setValue(1, v.getY());
                    data.add(instance);
                    ids[index] = v.getId();
                    index++;
                }

                if (index > 0) {
                    clusterer = new DBScan();
                    options = new String[8];
                    options[0] = "-E";
                    options[1] = Double.toString(eps);
                    options[2] = "-M";
                    options[3] = Integer.toString(minpts);
                    options[4] = "-I";
                    options[5] = "weka.clusterers.forOPTICSAndDBScan.Databases.SequentialDatabase";
                    options[6] = "-D";
                    options[7] = "weka.clusterers.forOPTICSAndDBScan.DataObjects.EuclidianDataObject";
                    clusterer.setOptions(options);
                    clusterer.buildClusterer(data);

                    eval = new ClusterEvaluation();
                    eval.setClusterer(clusterer);
                    eval.evaluateClusterer(new Instances(data));

                    assignments = eval.getClusterAssignments();
                    float incr = 1.0f / eval.getNumClusters();
                    map = new HashMap<>();

                    TIntArrayList aux;
                    for (int i = 0; i < assignments.length; i++) {
                        graph.getVertexById(ids[i]).setScalar(sdbscan, (assignments[i] + 1) * incr);
                        if (assignments[i] + 1 != 0) {
                            if (map.get(assignments[i] + 1) == null) {
                                aux = new TIntArrayList(ids.length);
                                aux.add(ids[i]);
                                map.put(assignments[i] + 1, aux);
                            } else {
                                map.get(assignments[i] + 1).add(ids[i]);
                            }
                        }
                    }
                    for (TIntArrayList vertexCluster : map.values()) {
                        TopicFactory.getInstance(projection, graph, vertexCluster);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void done() {

        if (!this.isCancelled()) {
            view.setStatus("Finished", false);
            view.dispose();
            viewer.repaint();
        }
        projection.updateScalarForIntermediates(sdbscan, false);
        viewer.updateScalars(sdbscan);
    }
}
