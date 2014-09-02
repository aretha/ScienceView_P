/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.datamining.clustering.monic;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

import topicevolutionvis.datamining.clustering.monic.transitions.CompactnessTransition;
import topicevolutionvis.datamining.clustering.monic.transitions.ContentChangeTransition;
import topicevolutionvis.graph.TemporalGraph;
import topicevolutionvis.graph.Vertex;
import topicevolutionvis.projection.temporal.TemporalProjection;
import topicevolutionvis.topic.Topic;
import topicevolutionvis.topic.TopicEventsAnimation;
import topicevolutionvis.topic.TopicFactory;
import topicevolutionvis.util.Utils;
import topicevolutionvis.view.TemporalProjectionViewer;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.DBSCAN;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author USER
 */
public class MONIC extends SwingWorker<Void, Void> {

    private TemporalProjection projection;
    private TemporalProjectionViewer viewer;
    private ExternalTransitions transitions = new ExternalTransitions();
    private MONICSettings view;
    private double eps, theta, theta_split;
    private int minpts;
    private long dbscan_time = 0, monic_time = 0, topic_time = 0, polygon_time_without_animation = 0;

    public MONIC(TemporalProjectionViewer viewer, MONICSettings view, double eps, int minpts, double theta, double theta_split) throws Exception {
        this.viewer = viewer;
        this.projection = viewer.getTemporalProjection();
        this.view = view;
        this.eps = eps;
        this.minpts = minpts;
        this.theta = theta;
        this.theta_split = theta_split;
    }

    @Override
    protected Void doInBackground() throws Exception {
        int next_available_id = 1;
        view.setStatus("Clustering...", true);
        int current_year, next_year, j;
        double overlap;
        DocumentClusterEvent event;
        Topic survival_candidate;
        THashMap<Topic, TIntArrayList> documents_added, documents_removed;
        ArrayList<Vertex> split_union = new ArrayList<>();
        ArrayList<Topic> topics_i, topics_j, split_candidates = new ArrayList<>(), absorption_candidates, split_list = new ArrayList<>();
        HashMap<Topic, Topic> absorptions_and_survivals = new HashMap<>();
        viewer.setUpdatingTopics(true);
        System.out.println("DBSCAN pra cada ano...");
        for (int i = 0; i < projection.getNumberOfYears(); i++) {
            this.runDBScan(projection.getMainGraph(projection.getYearWithIndex(i)));
        }
        this.monic_time = System.currentTimeMillis();
        System.out.println("MONIC...");
        for (int i = 0; i < projection.getNumberOfYears() - 1; i++) {
            current_year = projection.getYearWithIndex(i);

            next_year = projection.getYearWithIndex(i + 1);
            split_candidates.clear();
            split_union.clear();

            topics_i = projection.getMainGraph(current_year).getTopics();
            topics_j = projection.getMainGraph(next_year).getTopics();

            if (topics_i == null) {
                if (topics_j != null) {
                    for (Topic t_j : topics_j) {
                        event = new DocumentClusterEvent(next_year, DocumentClusterEvent.NEW_CLUSTER);
                        t_j.setId(next_available_id);
                        event.addTopicToOutput(t_j);
                        transitions.addExternalTransition(next_year, event);
                        next_available_id++;
                    }
                }
            } else {
                for (Topic t_i : topics_i) {
                    split_candidates.clear();
                    split_union.clear();
                    survival_candidate = null;
                    for (Topic t_j : topics_j) {
                        overlap = overlap(t_i.getVertexList(), t_j.getVertexList());
                        if (overlap >= this.theta) {
                            survival_candidate = t_j;
                        } else if (overlap >= this.theta_split) {
                            split_candidates.add(t_j);
                            this.unionWithTopic(split_union, t_j.getVertexList());
                        }
                    }
                    if (survival_candidate == null && split_candidates.size() <= 1) {
                        event = new DocumentClusterEvent(current_year, DocumentClusterEvent.DISAPPEARS);
                        event.addTopicToInput(t_i);
                        transitions.addExternalTransition(current_year, event);
                    } else if (survival_candidate != null) {
                        absorptions_and_survivals.put(t_i, survival_candidate);
                        split_candidates.clear();
                        split_union.clear();
                    } else if (!split_candidates.isEmpty()) {
                        if (split_candidates.size() > 1) {
                            if (overlap(t_i.getVertexList(), split_union) > theta) { //SPLIT
                                event = new DocumentClusterEvent(current_year, DocumentClusterEvent.SPLIT);
                                event.addTopicToInput(t_i);
                                event.addTopicsToOutput(split_candidates);
                                for (Topic t : split_candidates) {
                                    t.setId(next_available_id);
                                    next_available_id++;
                                }
                                documents_added = this.findDocumentsAdded(t_i, split_candidates);
                                documents_removed = this.findDocumentsRemoved(t_i, split_candidates);
                                if (documents_added != null || documents_removed != null) { //content changed
                                    event.addInternalTransition(new ContentChangeTransition(documents_added, documents_removed));
                                }
                                transitions.addExternalTransition(current_year, event);
                                split_list.addAll(split_candidates);

                            } else if (survival_candidate != null) {
                                absorptions_and_survivals.put(t_i, survival_candidate);
                                split_candidates.clear();
                                split_union.clear();
                            } else {
                                event = new DocumentClusterEvent(current_year, DocumentClusterEvent.DISAPPEARS);
                                event.addTopicToInput(t_i);
                                transitions.addExternalTransition(current_year, event);
                            }
                        } else if (survival_candidate != null) {
                            absorptions_and_survivals.put(t_i, survival_candidate);
                            split_candidates.clear();
                            split_union.clear();
                        }
                    } else {
                        absorptions_and_survivals.put(t_i, survival_candidate);
                        split_candidates.clear();
                        split_union.clear();
                    }
                }
                for (Topic dc_j : topics_j) {
                    absorption_candidates = this.makeList(absorptions_and_survivals, dc_j);
                    if (absorption_candidates.size() > 1) { // ABSORPTION
                        event = new DocumentClusterEvent(current_year, DocumentClusterEvent.MERGED);
                        dc_j.setId(next_available_id);
                        event.addTopicToOutput(dc_j);
                        for (Topic candidate : absorption_candidates) {
                            event.addTopicToInput(candidate);
                            absorptions_and_survivals.remove(candidate);
                        }
                        documents_added = this.findDocumentsAdded(absorption_candidates, dc_j);
                        documents_removed = this.findDocumentsRemoved(absorption_candidates, dc_j);
                        if (documents_added != null || documents_removed != null) { //content changed
                            event.addInternalTransition(new ContentChangeTransition(documents_added, documents_removed));
                        }
                        transitions.addExternalTransition(current_year, event);
                        next_available_id++;
                    } else if (absorption_candidates.size() == 1) {
                        event = new DocumentClusterEvent(current_year, DocumentClusterEvent.SURVIVED);
                        dc_j.setId(absorption_candidates.get(0).getId());
                        event.addTopicToInput(absorption_candidates.get(0));
                        event.addTopicToOutput(dc_j);
                        documents_added = this.findDocumentsAdded(absorption_candidates.get(0), dc_j);
                        documents_removed = this.findDocumentsRemoved(absorption_candidates.get(0), dc_j);
                        if (documents_added != null || documents_removed != null) { //content changed
                            event.addInternalTransition(new ContentChangeTransition(documents_added, documents_removed));
                        }
                        double[] dispersion_before = Utils.dispersion(absorption_candidates.get(0).getVertexList());
                        double[] dispersion_after = Utils.dispersion(dc_j.getVertexList());
                        double dispersion = Math.abs(dispersion_before[0] + dispersion_before[1] - dispersion_after[0] - dispersion_after[1]);
                        if (dispersion > 0) { //compactness transition
                            event.addInternalTransition(new CompactnessTransition(dispersion));
                        }
                        transitions.addExternalTransition(current_year, event);
                        absorptions_and_survivals.remove(absorption_candidates.get(0));
                    } else {
                        if (!split_list.contains(dc_j)) {
                            event = new DocumentClusterEvent(current_year, DocumentClusterEvent.NEW_CLUSTER);
                            dc_j.setId(next_available_id);
                            event.addTopicToOutput(dc_j);
                            transitions.addExternalTransition(current_year, event);
                            next_available_id++;
                        }
                    }
                }
            }
        }
        viewer.setUpdatingTopics(false);
        return null;
    }

    private void unionWithTopic(ArrayList<Vertex> a, ArrayList<Vertex> b) {
        for (Vertex v : b) {
            if (!containsThisVertex(a, v)) {
                a.add(v);
            }
        }
    }

    private boolean containsThisVertex(ArrayList<Vertex> a, Vertex v_test) {
        for (Vertex v : a) {
            if (v_test.getId() == v.getId()) {
                return true;
            }
        }
        return false;
    }

    private THashMap<Topic, TIntArrayList> findDocumentsAdded(Topic a, Topic b) {
        THashMap<Topic, TIntArrayList> documents_added = new THashMap<>(b.size());
        for (Vertex vb : b.getVertexList()) {
            if (!this.containsThisVertex(a.getVertexList(), vb)) {
                if (!documents_added.containsKey(b)) {
                    TIntArrayList aux = new TIntArrayList();
                    aux.add(vb.getId());
                    documents_added.put(b, aux);
                } else {
                    documents_added.get(b).add(vb.getId());
                }
            }
        }
        if (documents_added.isEmpty()) {
            return null;
        }
        return documents_added;
    }

    private THashMap<Topic, TIntArrayList> findDocumentsAdded(ArrayList<Topic> a, Topic b) {
        THashMap<Topic, TIntArrayList> documents_added = new THashMap<>(b.size());
        boolean contains;
        for (Vertex vb : b.getVertexList()) {
            contains = false;
            for (Topic t : a) {
                if (!contains && this.containsThisVertex(t.getVertexList(), vb)) {
                    contains = true;
                }
            }
            if (!contains) {
                if (!documents_added.containsKey(b)) {
                    TIntArrayList aux = new TIntArrayList();
                    aux.add(vb.getId());
                    documents_added.put(b, aux);
                } else {
                    documents_added.get(b).add(vb.getId());
                }
            }
        }
        if (documents_added.isEmpty()) {
            return null;
        }
        return documents_added;
    }

    private THashMap<Topic, TIntArrayList> findDocumentsAdded(Topic a, ArrayList<Topic> b) {
        THashMap<Topic, TIntArrayList> documents_added = new THashMap<>(b.size());
        for (Topic t : b) {
            for (Vertex vb : t.getVertexList()) {
                if (!this.containsThisVertex(a.getVertexList(), vb)) {
                    if (!documents_added.containsKey(t)) {
                        TIntArrayList aux = new TIntArrayList();
                        aux.add(vb.getId());
                        documents_added.put(t, aux);
                    } else {
                        documents_added.get(t).add(vb.getId());
                    }
                }
            }
        }
        if (documents_added.isEmpty()) {
            return null;
        }
        return documents_added;
    }

    private THashMap<Topic, TIntArrayList> findDocumentsRemoved(Topic a, Topic b) {
        THashMap<Topic, TIntArrayList> documents_removed = new THashMap<>(1);
        for (Vertex va : a.getVertexList()) {
            if (!this.containsThisVertex(b.getVertexList(), va)) {
                if (!documents_removed.containsKey(a)) {
                    TIntArrayList aux = new TIntArrayList();
                    aux.add(va.getId());
                    documents_removed.put(a, aux);
                } else {
                    documents_removed.get(a).add(va.getId());
                }
            }
        }
        if (documents_removed.isEmpty()) {
            return null;
        }
        return documents_removed;
    }

    private THashMap<Topic, TIntArrayList> findDocumentsRemoved(ArrayList<Topic> a, Topic b) {
        THashMap<Topic, TIntArrayList> documents_removed = new THashMap<>();
        for (Topic t : a) {
            for (Vertex va : t.getVertexList()) {
                if (!this.containsThisVertex(b.getVertexList(), va)) {
                    if (!documents_removed.containsKey(t)) {
                        TIntArrayList aux = new TIntArrayList();
                        aux.add(va.getId());
                        documents_removed.put(t, aux);
                    } else {
                        documents_removed.get(t).add(va.getId());
                    }
                }
            }
        }
        if (documents_removed.isEmpty()) {
            return null;
        }
        return documents_removed;
    }

    private THashMap<Topic, TIntArrayList> findDocumentsRemoved(Topic a, ArrayList<Topic> b) {
        THashMap<Topic, TIntArrayList> documents_removed = new THashMap<>();
        boolean contains;
        for (Vertex va : a.getVertexList()) {
            contains = false;
            for (Topic t : b) {
                if (contains == false && this.containsThisVertex(t.getVertexList(), va)) {
                    contains = true;
                }
            }
            if (!contains) {
                if (!documents_removed.containsKey(a)) {
                    TIntArrayList aux = new TIntArrayList();
                    aux.add(va.getId());
                    documents_removed.put(a, aux);
                } else {
                    documents_removed.get(a).add(va.getId());
                }
            }
        }
        if (documents_removed.isEmpty()) {
            return null;
        }
        return documents_removed;
    }

    private ArrayList<Topic> makeList(HashMap<Topic, Topic> absorptions_and_survivals, Topic dc_j) {
        ArrayList<Topic> list = new ArrayList<>();
        for (Entry<Topic, Topic> candidate : absorptions_and_survivals.entrySet()) {
            if (candidate.getValue().getVertexList().equals(dc_j.getVertexList())) {
                list.add(candidate.getKey());
            }
        }
        return list;
    }

    @Override
    public void done() {

        try {
            if (!isCancelled()) {
                get();
            }
        } catch (ExecutionException e) {
            // Exception occurred, deal with it
            Logger.getLogger(MONIC.class.getName()).log(Level.SEVERE, null, e);
        } catch (InterruptedException e) {
            Logger.getLogger(MONIC.class.getName()).log(Level.SEVERE, null, e);
            // Shouldn't happen, we're invoked when computation is finished
            throw new AssertionError(e);
        }
        System.out.println("DBSCAN time: " + this.dbscan_time);
        System.out.println("Topic extraction time: " + this.topic_time);
        System.out.println("Main Polygon time: " + this.polygon_time_without_animation);
        this.projection.setExternalTransitions(transitions);
        this.viewer.updateTopicsTree();

        for (Integer year : projection.getYears()) {
            ArrayList<Topic> topics_year = projection.getMainGraph(year).getTopics();
            System.out.println("Year " + year + ": " + topics_year.size() + " clusters");
            for (Topic t : topics_year) {
                System.out.println(t.getId() + "(" + t.size() + ") & " + t.toString());
            }

            if (this.transitions.hasEvents()) {
                ArrayList<DocumentClusterEvent> events = this.transitions.getExternalTransistions(year);
                if (events != null) {
                    System.out.println("Transitions");
                    for (DocumentClusterEvent dce : events) {
                        if (!dce.getInputDC().isEmpty()) {
                            System.out.print(dce.getInputDC().get(0).getId());
                            for (int y = 1; y < dce.getInputDC().size(); y++) {
                                System.out.print(", " + dce.getInputDC().get(y).getId());
                            }
                            System.out.print(" -> ");
                        }
                        if (!dce.getOutputDC().isEmpty()) {
                            System.out.print(dce.getOutputDC().get(0).getId());
                            for (int y = 1; y < dce.getOutputDC().size(); y++) {
                                System.out.print(", " + dce.getOutputDC().get(y).getId());
                            }
                        } else {
                            System.out.print("vazio");
                        }
                        System.out.print(" [");
                        String typeString;
                        switch (dce.getType()) {
                            case DocumentClusterEvent.SURVIVED:
                                typeString = "SURVIVED";
                                break;
                            case DocumentClusterEvent.NEW_CLUSTER:
                                typeString = "NEW_CLUSTER";
                                break;
                            case DocumentClusterEvent.SPLIT:
                                typeString = "SPLIT";
                                break;
                            case DocumentClusterEvent.MERGED:
                                typeString = "ABSORBED";
                                break;
                            case DocumentClusterEvent.DISAPPEARS:
                                typeString = "DISAPPEARS";
                                break;
                            default:
                                typeString = "INVALID";
                                break;
                        }

                        System.out.print(typeString + "] ");
                        if (!dce.getInputDC().isEmpty() && !dce.getOutputDC().isEmpty()) {
                            if (typeString.compareToIgnoreCase("SPLIT") == 0) {
                                Topic input = dce.getInputDC().get(0);
                                System.out.print("(");
                                for (int y = 0; y < dce.getOutputDC().size(); y++) {
                                    System.out.print(overlap(input.getVertexList(), dce.getOutputDC().get(y).getVertexList()) + ", ");
                                }
                                System.out.print(")");

                            } else if (typeString.compareToIgnoreCase("ABSORBED") == 0) {
                                Topic output = dce.getOutputDC().get(0);
                                System.out.print("(");
                                for (int y = 0; y < dce.getInputDC().size(); y++) {
                                    System.out.print(overlap(dce.getInputDC().get(y).getVertexList(), output.getVertexList()) + ", ");
                                }
                                System.out.print(")");
                            } else {
                                Topic output = dce.getOutputDC().get(0);
                                System.out.print("(");
                                System.out.print(overlap(dce.getInputDC().get(0).getVertexList(), output.getVertexList()));
                                System.out.print(")");
                            }
                        }
//                        if (dce.getType() == DocumentClusterEvent.SURVIVED) {
//                            for (InternalTransition internal_transition : dce.getInternalTransitions()) {
//                                if (internal_transition.getType() == InternalTransition.CONTENT_CHANGE_TRANSITION) {
//                                    ContentChangeTransition content_change_transition = (ContentChangeTransition) internal_transition;
//                                    if(content_change_transition.getSubtype() == ContentChangeTransition.){
//                                        
//                                    }
//                                }
//                            }
//                        }
                        System.out.println();
                    }
                }
            }
        }

        System.out.println("ANIMATION...");
        TopicEventsAnimation animation = new TopicEventsAnimation(projection);
        animation.create();
        this.viewer.getProjectionData().setTopicEvolutionGenerated(true);
        this.viewer.updateReport();
        
        this.monic_time = (System.currentTimeMillis()-this.monic_time);
        System.out.println("MONIC time: "+this.monic_time);

        if (!this.isCancelled()) {
            view.setStatus("Finished", false);
            view.dispose();
            viewer.repaint();
        }
    }

    private long runDBScan(TemporalGraph graph) {
        int[] ids;
        double[] assignments;
        String[] options;
        Instance instance;
        Instances data;
        DBSCAN clusterer;
        ClusterEvaluation eval;
        TIntArrayList aux;
        TIntObjectHashMap<TIntArrayList> map = new TIntObjectHashMap<>();

        long time1 = System.currentTimeMillis();
        FastVector atts = new FastVector();
        atts.addElement(new Attribute("x-pos"));
        atts.addElement(new Attribute("y-pos"));

        data = new Instances("Graph", atts, graph.getVertex().size());
        ids = new int[graph.getVertex().size()];
        int index = 0;
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
            try {
                clusterer = new DBSCAN();
                options = new String[8];
                options[0] = "-E";
                options[1] = Double.toString(eps);
                options[2] = "-M";
                options[3] = Integer.toString(minpts);
                options[4] = "-I";
                options[5] = "weka.clusterers.forOPTICSAndDBScan.Databases.SequentialDatabase";
                options[6] = "-D";
                options[7] = "weka.clusterers.forOPTICSAndDBScan.DataObjects.EuclideanDataObject";
                clusterer.setOptions(options);
                clusterer.buildClusterer(data);

                eval = new ClusterEvaluation();
                eval.setClusterer(clusterer);
                eval.evaluateClusterer(new Instances(data));

                assignments = eval.getClusterAssignments();

                for (int i = 0; i < assignments.length; i++) {
                    if (assignments[i] + 1 != 0) {
                        if (!map.containsKey((int) assignments[i] + 1)) {
                            aux = new TIntArrayList(ids.length);
                            aux.add(ids[i]);
                            map.put((int) assignments[i] + 1, aux);
                        } else {
                            map.get((int) assignments[i] + 1).add(ids[i]);
                        }
                    }
                }
                long time4 = System.currentTimeMillis();
                this.dbscan_time += (time4 - time1);

                Topic t;
                for (TIntObjectIterator<TIntArrayList> it = map.iterator(); it.hasNext();) {

                    it.advance();

                    if (it.value().size() > 0) {
                        long time2 = System.currentTimeMillis();
                        t = TopicFactory.getInstance(projection, graph, it.value());
                        t.createTopic();
                        long time3 = System.currentTimeMillis();
                        t.calcPolygon();
                        long time5 = System.currentTimeMillis();
                        graph.addTopic(t);
                        this.topic_time += (time3 - time2);
                        this.polygon_time_without_animation += (time5 - time3);

                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(MONIC.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return -1;
    }

    public static double overlap(ArrayList<Vertex> a, ArrayList<Vertex> b) {
        double result = 0;
        for (Vertex v_a : a) {
            for (Vertex v_b : b) {
                if (v_a.getId() == v_b.getId()) {
                    result++;
                    break;
                }
            }
        }
        return result / (double) a.size();
    }
}
