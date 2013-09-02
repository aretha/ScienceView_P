/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.topic;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.THashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import topicevolutionvis.datamining.clustering.monic.DocumentClusterEvent;
import topicevolutionvis.datamining.clustering.monic.ExternalTransitions;
import topicevolutionvis.datamining.clustering.monic.transitions.ContentChangeTransition;
import topicevolutionvis.graph.TemporalGraph;
import topicevolutionvis.graph.Vertex;
import topicevolutionvis.projection.temporal.TemporalProjection;
import topicevolutionvis.util.Utils;

/**
 *
 * @author USER
 */
public class TopicEventsAnimation {

    private TemporalProjection projection;
    private ExternalTransitions transitions;
    private TreeMap<Integer, ArrayList<TemporalGraph>> graphs;
    private final static int proximity_factor = 20;

    public TopicEventsAnimation(TemporalProjection projection) {
        this.projection = projection;
        this.graphs = projection.getGraphs();
        this.transitions = projection.getExternalTransitions();

    }

    public void create() {
        for (Integer current_year : transitions.getYears()) {
            ArrayList<TemporalGraph> animation_graphs = graphs.get(current_year + 1);
            for (DocumentClusterEvent event : transitions.getExternalTransistions(current_year)) {
                if (event.getType() == DocumentClusterEvent.SURVIVED) {
                    ContentChangeTransition contentChangeTransition = event.getContentChangeTransition();
                    Topic input = event.getInputDC().get(0), output = event.getOutputDC().get(0);
                    if (contentChangeTransition == null) {
                        ArrayList<Vertex> vertex_input = input.getVertexList();
                        for (int i = 0; i < TemporalProjection.getN() - 1; i++) {
                            TemporalGraph graph = animation_graphs.get(i);
                            TIntArrayList vertex_aux = new TIntArrayList(vertex_input.size());
                            for (Vertex v : vertex_input) {
                                vertex_aux.add(v.getId());
                            }
                            Topic t_input = TopicFactory.getInstance(projection, graph, vertex_aux);
                            if (input.usedConvex() && output.usedConvex()) {
                                t_input.setDrawPolygonOption(Topic.USE_CONVEX);
                            } else if (!input.usedConvex() && !output.usedConvex()) {
                                t_input.setDrawPolygonOption(Topic.USE_CONCAVE);
                            }
                            t_input.calcPolygon();
                            input.cloneInfo(t_input);
                            graph.addTopic(t_input);


                        }
                    } else {
                        if (contentChangeTransition.getSubtype() == ContentChangeTransition.ONLY_ADDED_DOCUMENTS) {
                            this.survivalOnlyAddedDocument(event, animation_graphs, contentChangeTransition);
                        } else if (contentChangeTransition.getSubtype() == ContentChangeTransition.ONLY_REMOVED_DOCUMENTS) {
                            this.survivalOnlyRemovedDocuments(event, animation_graphs, contentChangeTransition);
                        } else {//ContentChangeTransition.ADDED_AND_REMOVED_DOCUMENTES
                            this.survivalAddedAndRemovedDocuments(event, animation_graphs, contentChangeTransition);
                        }
                    }
                } else if (event.getType() == DocumentClusterEvent.NEW_CLUSTER) {
                    this.newcluster(event, animation_graphs);
                } else if (event.getType() == DocumentClusterEvent.DISAPPEARS) {
                    this.disappears(event, animation_graphs);
                } else if (event.getType() == DocumentClusterEvent.SPLIT) {
                    System.out.println();
                    this.split(event, animation_graphs);
                } else if (event.getType() == DocumentClusterEvent.MERGED) {
                    this.merge(event, animation_graphs);
                }
            }
        }
    }

    private void newcluster(DocumentClusterEvent event, ArrayList<TemporalGraph> animation_graphs) {
        Topic output = event.getOutputDC().get(0);
        ArrayList<Vertex> vertex_output = output.getVertexList();
        TemporalGraph last_graph = animation_graphs.get(TemporalProjection.getN() - 1);
        TIntArrayList vertex_aux = new TIntArrayList(vertex_output.size());
        for (Vertex v : vertex_output) {
            vertex_aux.add(v.getId());
        }
        Topic t_last_graph = TopicFactory.getInstance(projection, last_graph, vertex_aux);
        t_last_graph.calcPolygon();
        double target_area = t_last_graph.getGeometry().getEnvelope().getArea();
        int begin_increment = -1;
        for (int i = 0; i < TemporalProjection.getN() - 1; i++) {
            TemporalGraph graph = animation_graphs.get(i);
            vertex_aux = new TIntArrayList(vertex_output.size());
            for (Vertex v : vertex_output) {
                vertex_aux.add(v.getId());
            }
            Topic t_output = TopicFactory.getInstance(projection, graph, vertex_aux);
            t_output.calcPolygon();
            double current_area = t_output.getGeometry().getEnvelope().getArea();
            if (begin_increment == -1 && ((current_area < target_area) || ((100.0 * current_area) / target_area) < 450.0)) {
                begin_increment = i;
            }
            if (begin_increment != -1) {
                t_output.setAlphaPolygon((1.0f / (TemporalProjection.getN() - begin_increment)) * (i - begin_increment));
                if (i > TemporalProjection.getN() / 3) {
                    output.cloneInfo(t_output);
                }
                if (output.usedConvex()) {
                    t_output.setDrawPolygonOption(Topic.USE_CONVEX);
                } else {
                    t_output.setDrawPolygonOption(Topic.USE_CONCAVE);
                }
                graph.addTopic(t_output);
            }
        }
    }

    private void merge(DocumentClusterEvent event, ArrayList<TemporalGraph> animation_graphs) {
        TIntArrayList vertex_aux;
        THashMap<Topic, TIntArrayList> ids_documents_added = null, ids_documents_removed = null;
        GeometryFactory geometryFactory = new GeometryFactory();
        ArrayList<Topic> input = event.getInputDC();
        Topic output = event.getOutputDC().get(0);
        ArrayList<Vertex> vertex_ouputt = output.getVertexList();
        ContentChangeTransition contentChangeTransition = event.getContentChangeTransition();
        if (contentChangeTransition != null) {
            ids_documents_added = contentChangeTransition.getIdsDocumentsAdded();
            ids_documents_removed = contentChangeTransition.getIdsDocumentsRemoved();
        }
        TIntArrayList all_added = null, all_removed = null, removed_in_this_topic;
        int[] begin_increment_all_added = null;
        int[] begin_increment_all_removed = null;
        int[] begin_increment_removed_each_topic = null;
        if (ids_documents_added != null) {
            all_added = ids_documents_added.get(output);
            begin_increment_all_added = new int[all_added.size()];
            Arrays.fill(begin_increment_all_added, -1);
        }
        if (ids_documents_removed != null) {
            all_removed = Utils.unionValues(ids_documents_removed);
            begin_increment_all_removed = new int[all_removed.size()];
            Arrays.fill(begin_increment_all_removed, -1);
            begin_increment_removed_each_topic = new int[all_removed.size()];
            Arrays.fill(begin_increment_removed_each_topic, -1);

        }
        for (int i = 0; i < TemporalProjection.getN() - 1; i++) {
            TemporalGraph graph = animation_graphs.get(i);
            vertex_aux = new TIntArrayList(vertex_ouputt.size());
            for (Topic t : input) {
                for (Vertex v : t.getVertexList()) {
                    vertex_aux.add(v.getId());
                }
            }
            if (all_removed != null) {
                for (TIntIterator it = all_removed.iterator(); it.hasNext();) {
                    vertex_aux.remove(it.next());
                }
            }
            Topic t_output = TopicFactory.getInstance(projection, graph, vertex_aux);
            if (all_added != null) {
                t_output.setNextAddedVertex(all_added);
            }
            t_output.calcPolygon();
            ArrayList<Coordinate> fake_vertex = new ArrayList<>();
            if (all_removed != null) {
                for (int j = 0; j < all_removed.size(); j++) {
                    Vertex v = graph.getVertexById(all_removed.get(j));
                    Coordinate v_coord = new Coordinate(v.getX(), v.getY());
                    if (!t_output.contains(geometryFactory.createPoint(v_coord))) {
                        DistanceOp op = new DistanceOp(t_output.getGeometry(), geometryFactory.createPoint(v_coord));
                        if (begin_increment_all_removed[j] == -1) {
                            begin_increment_all_removed[j] = i;
                        }
                        Coordinate[] closest_points = op.nearestPoints();
                        fake_vertex.add((new LineSegment(closest_points[0], closest_points[1])).pointAlong(1.0 - ((i - begin_increment_all_removed[j]) * (1.0 / (TemporalProjection.getN() - begin_increment_all_removed[j])))));
                    } else {
                        vertex_aux.add(v.getId());
                    }
                }
            }
            if (all_added != null) {
                for (int j = 0; j < all_added.size(); j++) {
                    Vertex v = graph.getVertexById(all_added.get(j));
                    Coordinate v_coord = new Coordinate(v.getX(), v.getY());
                    if (t_output.contains(geometryFactory.createPoint(v_coord))) {
                        vertex_aux.add(v.getId());
                    } else {
                        DistanceOp op = new DistanceOp(t_output.getGeometry(), geometryFactory.createPoint(v_coord));
                        if (op.distance() < proximity_factor || TemporalProjection.getN() - i <= 10) {
                            if (begin_increment_all_added[j] == -1) {
                                begin_increment_all_added[j] = i;
                            }
                            Coordinate[] closest_points = op.nearestPoints();
                            fake_vertex.add((new LineSegment(closest_points[0], closest_points[1])).pointAlong((i - begin_increment_all_added[j]) * (1.0 / (TemporalProjection.getN() - begin_increment_all_added[j]))));
                        }
                    }
                }
            }
            t_output = TopicFactory.getInstance(projection, graph, vertex_aux, fake_vertex);
            t_output.setAlphaPolygon((1.0f / TemporalProjection.getN()) * i);
            if (all_added != null) {
                t_output.setNextAddedVertex(all_added);
            }
            if (i > TemporalProjection.getN() / 2) {
                output.cloneInfo(t_output);
            }
            if (output.usedConvex()) {
                t_output.setDrawPolygonOption(Topic.USE_CONVEX);
            } else {
                t_output.setDrawPolygonOption(Topic.USE_CONCAVE);
            }
            t_output.calcPolygon();
            graph.addTopic(t_output);

            int count_removed = 0;
            for (Topic topic_merge : input) {
                vertex_aux = new TIntArrayList(topic_merge.size());
                for (Vertex v : topic_merge.getVertexList()) {
                    vertex_aux.add(v.getId());
                }

                removed_in_this_topic = null;
                if (ids_documents_removed != null) {
                    removed_in_this_topic = ids_documents_removed.get(topic_merge);
                    if (removed_in_this_topic != null) {
                        for (TIntIterator it = removed_in_this_topic.iterator(); it.hasNext();) {
                            vertex_aux.remove(it.next());
                        }
                    }
                }

                t_output = TopicFactory.getInstance(projection, graph, vertex_aux);
                if (all_added != null) {
                    t_output.setNextAddedVertex(all_added);
                }
                t_output.calcPolygon();
                fake_vertex = null;

                if (removed_in_this_topic != null) {
                    fake_vertex = new ArrayList<>(removed_in_this_topic.size());
                    for (int j = 0; j < removed_in_this_topic.size(); j++) {
                        Vertex v = graph.getVertexById(removed_in_this_topic.get(j));
                        Coordinate v_coord = new Coordinate(v.getX(), v.getY());
                        if (!t_output.contains(geometryFactory.createPoint(v_coord))) {
                            DistanceOp op = new DistanceOp(t_output.getGeometry(), geometryFactory.createPoint(v_coord));
                            if (begin_increment_removed_each_topic[count_removed + j] == -1) {
                                begin_increment_removed_each_topic[count_removed + j] = i;
                            }
                            Coordinate[] closest_points = op.nearestPoints();
                            fake_vertex.add((new LineSegment(closest_points[0], closest_points[1])).pointAlong(1.0 - ((i - begin_increment_removed_each_topic[count_removed + j]) * (1.0 / (TemporalProjection.getN() - begin_increment_removed_each_topic[count_removed + j])))));
                        } else {
                            vertex_aux.add(v.getId());
                        }
                    }
                    count_removed += removed_in_this_topic.size();
                }

                t_output = TopicFactory.getInstance(projection, graph, vertex_aux, fake_vertex);
                if (i < TemporalProjection.getN() / 2) {
                    topic_merge.cloneInfo(t_output);
                }
                if (all_added != null) {
                    t_output.setNextAddedVertex(all_added);
                }
                if (topic_merge.usedConvex()) {
                    t_output.setDrawPolygonOption(Topic.USE_CONVEX);
                } else {
                    t_output.setDrawPolygonOption(Topic.USE_CONCAVE);
                }
                t_output.setAlphaPolygon(1.0f - ((1.0f / TemporalProjection.getN()) * i));
                t_output.setUseDashedStroke(true);
                t_output.calcPolygon();
                graph.addTopic(t_output);
            }
        }
    }

    private void split(DocumentClusterEvent event, ArrayList<TemporalGraph> animation_graphs) {
        TIntArrayList vertex_aux;
        GeometryFactory geometryFactory = new GeometryFactory();
        THashMap<Topic, TIntArrayList> ids_documents_added = null, ids_documents_removed = null;
        Topic input = event.getInputDC().get(0);
        ArrayList<Topic> output = event.getOutputDC();
        ArrayList<Vertex> vertex_input = input.getVertexList();
        ContentChangeTransition contentChangeTransition = event.getContentChangeTransition();
        if (contentChangeTransition != null) {
            ids_documents_added = contentChangeTransition.getIdsDocumentsAdded();
            ids_documents_removed = contentChangeTransition.getIdsDocumentsRemoved();
        }
        TIntArrayList all_added = null, added_in_this_topic, removed_in_this_topic, all_removed = null;
        THashMap<Topic, TIntArrayList> removed_in_each_topic = new THashMap<>(output.size());
        int[] begin_increment_all_added = null;
        int[] begin_increment_all_removed = null;
        int[] begin_increment_added_each_topic = null;
        if (ids_documents_added != null) {
            all_added = Utils.unionValues(ids_documents_added);
            begin_increment_all_added = new int[all_added.size()];
            Arrays.fill(begin_increment_all_added, -1);
            begin_increment_added_each_topic = new int[all_added.size()];
            Arrays.fill(begin_increment_added_each_topic, -1);
        }
        if (ids_documents_removed != null) {
            all_removed = ids_documents_removed.get(input);
            int id_removed;
            begin_increment_all_removed = new int[all_removed.size()];
            Arrays.fill(begin_increment_all_removed, -1);
            for (TIntIterator it = all_removed.iterator(); it.hasNext();) {
                id_removed = it.next();
                Topic closest_topic = null;
                double min_distance = Double.MAX_VALUE;
                for (Topic t : output) {
                    double value = t.distanceTo(animation_graphs.get(0).getVertexById(id_removed));
                    if (value < min_distance) {
                        min_distance = value;
                        closest_topic = t;
                    }
                }
                if (removed_in_each_topic.containsKey(closest_topic)) {
                    removed_in_each_topic.get(closest_topic).add(id_removed);
                } else {
                    TIntArrayList aux = new TIntArrayList();
                    aux.add(id_removed);
                    removed_in_each_topic.put(closest_topic, aux);
                }
            }
        }
        for (int i = 0; i < TemporalProjection.getN() - 1; i++) {
            TemporalGraph graph = animation_graphs.get(i);
            vertex_aux = new TIntArrayList(vertex_input.size());
            for (Vertex v : vertex_input) {
                vertex_aux.add(v.getId());
            }
            if (all_removed != null) {
                for (TIntIterator it = all_removed.iterator(); it.hasNext();) {
                    vertex_aux.remove(it.next());
                }
            }
            Topic t_input = TopicFactory.getInstance(projection, graph, vertex_aux);
            if (all_added != null) {
                t_input.setNextAddedVertex(all_added);
            }
            t_input.calcPolygon();
            ArrayList<Coordinate> fake_vertex = new ArrayList<>();
            if (all_removed != null) {
                fake_vertex = new ArrayList<>(all_removed.size());
                for (int j = 0; j < all_removed.size(); j++) {
                    Vertex v = graph.getVertexById(all_removed.get(j));
                    Coordinate v_coord = new Coordinate(v.getX(), v.getY());
                    if (!t_input.contains(geometryFactory.createPoint(v_coord))) {
                        DistanceOp op = new DistanceOp(t_input.getGeometry(), geometryFactory.createPoint(v_coord));
                        if (begin_increment_all_removed[j] == -1) {
                            begin_increment_all_removed[j] = i;
                        }
                        Coordinate[] closest_points = op.nearestPoints();
                        fake_vertex.add((new LineSegment(closest_points[0], closest_points[1])).pointAlong(1.0 - ((i - begin_increment_all_removed[j]) * (1.0 / (TemporalProjection.getN() - begin_increment_all_removed[j])))));
                    } else {
                        vertex_aux.add(v.getId());
                    }
                }
            }
            if (all_added != null) {
                for (int j = 0; j < all_added.size(); j++) {
                    Vertex v = graph.getVertexById(all_added.get(j));
                    Coordinate v_coord = new Coordinate(v.getX(), v.getY());
                    if (t_input.contains(geometryFactory.createPoint(v_coord))) {
                        vertex_aux.add(v.getId());
                    } else {
                        DistanceOp op = new DistanceOp(t_input.getGeometry(), geometryFactory.createPoint(v_coord));
                        if (op.distance() < proximity_factor || TemporalProjection.getN() - i <= 10) {
                            if (begin_increment_all_added[j] == -1) {
                                begin_increment_all_added[j] = i;
                            }
                            Coordinate[] closest_points = op.nearestPoints();
                            fake_vertex.add((new LineSegment(closest_points[0], closest_points[1])).pointAlong((i - begin_increment_all_added[j]) * (1.0 / (TemporalProjection.getN() - begin_increment_all_added[j]))));
                        }
                    }
                }
            }
            t_input = TopicFactory.getInstance(projection, graph, vertex_aux, fake_vertex);
            if (input.usedConvex()) {
                t_input.setDrawPolygonOption(Topic.USE_CONVEX);
            } else {
                t_input.setDrawPolygonOption(Topic.USE_CONCAVE);
            }
            t_input.setAlphaPolygon(1.0f - ((1.0f / TemporalProjection.getN()) * i));
            if (i < TemporalProjection.getN() / 2) {
                input.cloneInfo(t_input);
            }
            t_input.calcPolygon();
            t_input.setUseDashedStroke(true);
            graph.addTopic(t_input);

            int count_added = 0, count_removed = 0;
            for (Topic topic_split : output) {
                vertex_aux = new TIntArrayList(topic_split.size());
                for (Vertex v : topic_split.getVertexList()) {
                    vertex_aux.add(v.getId());
                }
                added_in_this_topic = null;
                if (ids_documents_added != null) {
                    added_in_this_topic = ids_documents_added.get(topic_split);
                    if (added_in_this_topic != null) {
                        t_input.setNextAddedVertex(added_in_this_topic);
                        TIntIterator it = added_in_this_topic.iterator();
                        while (it.hasNext()) {
                            vertex_aux.remove(it.next());
                        }
                    }
                }

                t_input = TopicFactory.getInstance(projection, graph, vertex_aux);
                t_input.calcPolygon();
                fake_vertex = null;
                if (all_removed != null) {
                    removed_in_this_topic = removed_in_each_topic.get(topic_split);
                    if (removed_in_this_topic != null) {
                        fake_vertex = new ArrayList<>(removed_in_this_topic.size());
                        for (int j = 0; j < removed_in_this_topic.size(); j++) {
                            Vertex v = graph.getVertexById(removed_in_this_topic.get(j));
                            Coordinate v_coord = new Coordinate(v.getX(), v.getY());
                            if (!t_input.contains(geometryFactory.createPoint(v_coord))) {
                                DistanceOp op = new DistanceOp(t_input.getGeometry(), geometryFactory.createPoint(v_coord));
                                if (begin_increment_all_removed[count_removed + j] == -1) {
                                    begin_increment_all_removed[count_removed + j] = i;
                                }
                                Coordinate[] closest_points = op.nearestPoints();
                                fake_vertex.add((new LineSegment(closest_points[0], closest_points[1])).pointAlong(1.0 - ((i - begin_increment_all_removed[count_removed + j]) * (1.0 / (TemporalProjection.getN() - begin_increment_all_removed[count_removed + j])))));
                            } else {
                                vertex_aux.add(v.getId());
                            }
                        }
                        count_removed += removed_in_this_topic.size();
                    }
                }
                if (added_in_this_topic != null) {
                    fake_vertex = new ArrayList<>(added_in_this_topic.size());
                    for (int j = 0; j < added_in_this_topic.size(); j++) {
                        Vertex v = graph.getVertexById(added_in_this_topic.get(j));
                        Coordinate v_coord = new Coordinate(v.getX(), v.getY());
                        if (t_input.contains(geometryFactory.createPoint(v_coord))) {
                            vertex_aux.add(v.getId());
                        } else {
                            DistanceOp op = new DistanceOp(t_input.getGeometry(), geometryFactory.createPoint(v_coord));
                            if (op.distance() < proximity_factor) {
                                if (begin_increment_added_each_topic[count_added + j] == -1) {
                                    begin_increment_added_each_topic[count_added + j] = i;
                                }
                                Coordinate[] closest_points = op.nearestPoints();
                                Coordinate fake = (new LineSegment(closest_points[0], closest_points[1])).pointAlong((i - begin_increment_added_each_topic[count_added + j]) * (1.0 / (TemporalProjection.getN() - begin_increment_added_each_topic[count_added + j])));
                                fake_vertex.add(fake);
                            }
                        }
                    }
                    count_added += added_in_this_topic.size();
                }
                t_input = TopicFactory.getInstance(projection, graph, vertex_aux, fake_vertex);
                if (i > TemporalProjection.getN() / 2) {
                    topic_split.cloneInfo(t_input);
                }
                if (added_in_this_topic != null) {
                    t_input.setNextAddedVertex(added_in_this_topic);
                }
//                if (topic_split.usedConvex()) {
//                    t_input.setDrawPolygonOption(Topic.USE_CONVEX);
//                } else {
//                    t_input.setDrawPolygonOption(Topic.USE_CONCAVE);
//                }
                t_input.setAlphaPolygon((1.0f / TemporalProjection.getN()) * i);

                t_input.calcPolygon();
                graph.addTopic(t_input);
            }

        }
    }

    private void disappears(DocumentClusterEvent event, ArrayList<TemporalGraph> animation_graphs) {
        Topic input = event.getInputDC().get(0);
        ArrayList<Vertex> vertex_input = input.getVertexList();
        for (int i = 0; i < TemporalProjection.getN() - 1; i++) {
            if (i != TemporalProjection.getN() - 2) {
                TemporalGraph graph = animation_graphs.get(i);
                TIntArrayList vertex_aux = new TIntArrayList(vertex_input.size());
                for (Vertex v : vertex_input) {
                    vertex_aux.add(v.getId());
                }
                Topic t_input = TopicFactory.getInstance(projection, graph, vertex_aux);
                t_input.calcPolygon();
                t_input.setAlphaPolygon(1.0f - ((1.0f / TemporalProjection.getN()) * i));
                if (i < TemporalProjection.getN() / 2) {
                    input.cloneInfo(t_input);
                }
                if (input.usedConvex()) {
                    t_input.setDrawPolygonOption(Topic.USE_CONVEX);
                } else {
                    t_input.setDrawPolygonOption(Topic.USE_CONCAVE);
                }
                graph.addTopic(t_input);
            }
        }
    }

    private void survivalAddedAndRemovedDocuments(DocumentClusterEvent event, ArrayList<TemporalGraph> animation_graphs, ContentChangeTransition contentChangeTransition) {
        Topic input = event.getInputDC().get(0), output = event.getOutputDC().get(0);
        ArrayList<Vertex> vertex_output = output.getVertexList();
        GeometryFactory geometryFactory = new GeometryFactory();
        TIntArrayList ids_documents_removed = contentChangeTransition.getIdsDocumentsRemoved().get(input);
        int n_removed = ids_documents_removed.size();
        TIntArrayList ids_documents_added = contentChangeTransition.getIdsDocumentsAdded().get(output);
        int[] begin_increment = new int[ids_documents_added.size() + ids_documents_removed.size()];
        Arrays.fill(begin_increment, -1);
        for (int i = 0; i < TemporalProjection.getN() - 1; i++) {
            TemporalGraph graph = animation_graphs.get(i);
            ArrayList<Coordinate> fake_vertex = new ArrayList<>(begin_increment.length);
            TIntArrayList vertex_aux = new TIntArrayList(vertex_output.size());
            for (Vertex v : vertex_output) {
                vertex_aux.add(v.getId());
            }
            for (TIntIterator it = ids_documents_added.iterator(); it.hasNext();) {
                vertex_aux.remove(it.next());
            }
            Topic t_output = TopicFactory.getInstance(projection, graph, vertex_aux);
            t_output.setNextAddedVertex(ids_documents_added);
            t_output.calcPolygon();
            graph.addFakeTopic(t_output);
            for (int j = 0; j < ids_documents_removed.size(); j++) {
                Vertex v = graph.getVertexById(ids_documents_removed.get(j));
                Coordinate v_coord = new Coordinate(v.getX(), v.getY());
                if (!t_output.contains(geometryFactory.createPoint(v_coord))) {
                    if (begin_increment[j] == -1) {
                        begin_increment[j] = i;
                    }
                    Vertex v_ini = animation_graphs.get(begin_increment[j]).getVertexById(ids_documents_removed.get(j));
                    DistanceOp op = new DistanceOp(t_output.getGeometry(), geometryFactory.createPoint(new Coordinate(v_ini.getX(), v_ini.getY())));
                    Coordinate[] closest_points = op.nearestPoints();
                    LineSegment line_segment = new LineSegment(closest_points[0], closest_points[1]);
                    Coordinate fake = (new LineSegment(closest_points[0], closest_points[1])).pointAlong(1.0 - ((i - begin_increment[j]) * (1.0 / (TemporalProjection.getN() - begin_increment[j]))));
                    graph.lines_segments.add(line_segment);
                    fake_vertex.add(fake);
                } else {
                    vertex_aux.add(v.getId());
                }
            }
            for (int j = 0; j < ids_documents_added.size(); j++) {
                Vertex v = graph.getVertexById(ids_documents_added.get(j));
                Coordinate v_coord = new Coordinate(v.getX(), v.getY());
                if (t_output.contains(geometryFactory.createPoint(v_coord))) {
                    vertex_aux.add(v.getId());
                } else {
                    DistanceOp op = new DistanceOp(t_output.getGeometry(), geometryFactory.createPoint(v_coord));
                    if (op.distance() < proximity_factor || TemporalProjection.getN() - i <= 10) {
                        if (begin_increment[n_removed + j] == -1) {
                            begin_increment[n_removed + j] = i;
                        }
                        Coordinate[] closest_points = op.nearestPoints();
                        LineSegment line_segment = new LineSegment(closest_points[0], closest_points[1]);
                        Coordinate fake = (new LineSegment(closest_points[0], closest_points[1])).pointAlong((i - begin_increment[n_removed + j]) * (1.0 / (TemporalProjection.getN() - begin_increment[n_removed + j])));
                        graph.lines_segments.add(line_segment);
                        fake_vertex.add(fake);
                    }
                }
            }

            t_output = TopicFactory.getInstance(projection, graph, vertex_aux, fake_vertex);
            t_output.setNextAddedVertex(ids_documents_added);
            if (i > TemporalProjection.getN() / 2) {
                output.cloneInfo(t_output);
            } else {
                input.cloneInfo(t_output);
            }
            if (input.usedConvex() && output.usedConvex()) {
                t_output.setDrawPolygonOption(Topic.USE_CONVEX);
            } else if (!input.usedConvex() && !output.usedConvex()) {
                t_output.setDrawPolygonOption(Topic.USE_CONCAVE);
            } else if (input.usedConvex()) {
                if (i < TemporalProjection.getN() / 2) {
                    t_output.setDrawPolygonOption(Topic.USE_CONVEX);
                } else {
                    t_output.setDrawPolygonOption(Topic.USE_CONCAVE);
                }
            } else if (output.usedConvex()) {
                if (i < TemporalProjection.getN() / 2) {
                    t_output.setDrawPolygonOption(Topic.USE_CONCAVE);
                } else {
                    t_output.setDrawPolygonOption(Topic.USE_CONVEX);
                }
            }
            t_output.calcPolygon();
            graph.addTopic(t_output);
        }
    }

    private void survivalOnlyRemovedDocuments(DocumentClusterEvent event, ArrayList<TemporalGraph> animation_graphs, ContentChangeTransition contentChangeTransition) {
        Topic input = event.getInputDC().get(0), output = event.getOutputDC().get(0);
        ArrayList<Vertex> vertex_output = output.getVertexList();
        GeometryFactory geometryFactory = new GeometryFactory();
        TIntArrayList ids_documents_removed = contentChangeTransition.getIdsDocumentsRemoved().get(input);
        int[] begin_increment = new int[ids_documents_removed.size()];
        Arrays.fill(begin_increment, -1);
        for (int i = 0; i < TemporalProjection.getN() - 1; i++) {
            TemporalGraph graph = animation_graphs.get(i);
            ArrayList<Coordinate> fake_vertex = new ArrayList<>(ids_documents_removed.size());
            TIntArrayList vertex_aux = new TIntArrayList(vertex_output.size());
            for (Vertex v : vertex_output) {
                vertex_aux.add(v.getId());
            }
            for (TIntIterator it = ids_documents_removed.iterator(); it.hasNext();) {
                vertex_aux.remove(it.next());
            }
            Topic t_output = TopicFactory.getInstance(projection, graph, vertex_aux);
            t_output.calcPolygon();
            graph.addFakeTopic(t_output);
            for (int j = 0; j < ids_documents_removed.size(); j++) {
                Vertex v = graph.getVertexById(ids_documents_removed.get(j));
                Coordinate v_coord = new Coordinate(v.getX(), v.getY());
                if (!t_output.contains(geometryFactory.createPoint(v_coord))) {
                    if (begin_increment[j] == -1) {
                        begin_increment[j] = i;
                    }
                    DistanceOp op = new DistanceOp(t_output.getGeometry(), geometryFactory.createPoint(v_coord));
                    Coordinate[] closest_points = op.nearestPoints();
                    Coordinate fake = (new LineSegment(closest_points[0], closest_points[1])).pointAlong(1.0 - ((i - begin_increment[j]) * (1.0 / (TemporalProjection.getN() - begin_increment[j]))));
                    fake_vertex.add(fake);
                } else {
                    vertex_aux.add(v.getId());
                }
            }

            t_output = TopicFactory.getInstance(projection, graph, vertex_aux, fake_vertex);
            if (i > TemporalProjection.getN() / 2) {
                output.cloneInfo(t_output);
            } else {
                input.cloneInfo(t_output);
            }
            if (input.usedConvex() && output.usedConvex()) {
                t_output.setDrawPolygonOption(Topic.USE_CONVEX);
            } else if (!input.usedConvex() && !output.usedConvex()) {
                t_output.setDrawPolygonOption(Topic.USE_CONCAVE);
            }
            t_output.calcPolygon();
            graph.addTopic(t_output);
        }
    }

    private void survivalOnlyAddedDocument(DocumentClusterEvent event, ArrayList<TemporalGraph> animation_graphs, ContentChangeTransition internalTransition) {
        Topic input = event.getInputDC().get(0), output = event.getOutputDC().get(0);
        ArrayList<Vertex> vertex_input = input.getVertexList();
        TIntArrayList ids_documents_added = internalTransition.getIdsDocumentsAdded().get(output);
        GeometryFactory geometryFactory = new GeometryFactory();
        int[] begin_increment = new int[ids_documents_added.size()];
        Arrays.fill(begin_increment, -1);
        for (int i = 0; i < TemporalProjection.getN() - 1; i++) {
            TemporalGraph graph = animation_graphs.get(i);
            TIntArrayList vertex_aux = new TIntArrayList(vertex_input.size());
            ArrayList<Coordinate> fake_vertex = new ArrayList<>(ids_documents_added.size());
            for (Vertex v : vertex_input) {
                vertex_aux.add(v.getId());
            }
            Topic t_input = TopicFactory.getInstance(projection, graph, vertex_aux);
            t_input.setNextAddedVertex(ids_documents_added);
            t_input.calcPolygon();
            graph.addFakeTopic(t_input);
            for (int j = 0; j < ids_documents_added.size(); j++) {
                Vertex v = graph.getVertexById(ids_documents_added.get(j));
                Coordinate v_coord = new Coordinate(v.getX(), v.getY());
                if (t_input.contains(geometryFactory.createPoint(v_coord))) {
                    vertex_aux.add(v.getId());
                } else {
                    DistanceOp op = new DistanceOp(t_input.getGeometry(), geometryFactory.createPoint(v_coord));
                    if (op.distance() < proximity_factor || TemporalProjection.getN() - i <= 10) {
                        if (begin_increment[j] == -1) {
                            begin_increment[j] = i;
                        }
                        Coordinate[] closest_points = op.nearestPoints();
                        Coordinate fake = (new LineSegment(closest_points[0], closest_points[1])).pointAlong((i - begin_increment[j]) * (1.0 / (TemporalProjection.getN() - begin_increment[j])));
                        fake_vertex.add(fake);
                    }
                }
            }
            t_input = TopicFactory.getInstance(projection, graph, vertex_aux, fake_vertex);
            if (i > TemporalProjection.getN() / 2) {
                output.cloneInfo(t_input);
            } else {
                input.cloneInfo(t_input);
            }
            if (input.usedConvex() && output.usedConvex()) {
                t_input.setDrawPolygonOption(Topic.USE_CONVEX);
            } else if (!input.usedConvex() && !output.usedConvex()) {
                t_input.setDrawPolygonOption(Topic.USE_CONCAVE);
            }
            t_input.setNextAddedVertex(ids_documents_added);
            t_input.calcPolygon();
            graph.addTopic(t_input);
        }
    }
}
