/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.topic;

import cc.mallet.types.IDSorter;
import com.vividsolutions.jts.geom.Coordinate;
import gnu.trove.list.array.TIntArrayList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import topicevolutionvis.dimensionreduction.lda.LDAOutput;
import topicevolutionvis.graph.TemporalGraph;
import topicevolutionvis.projection.temporal.TemporalProjection;

/**
 *
 * @author USER
 */
public class LDATopic extends Topic implements Cloneable {

    private final TopicData tdata;
    private LDAOutput output_lda;

    @Override
    public Object clone() throws CloneNotSupportedException {
        Topic newTopic = new LDATopic(new TIntArrayList(), this.tprojection, this.graph, this.output_lda);
        for (int i = 0; i < this.vertex_id.size(); i++) {
            newTopic.vertex_id.add(vertex_id.get(i));
        }
        for (Coordinate c : this.fake_vertex) {
            newTopic.fake_vertex.add(new Coordinate(c));
        }
        newTopic.setId(this.getId());
        newTopic.setAlphaPolygon(this.getAlphaPolygon());
        newTopic.setDrawPolygonOption(this.getDrawPolygonOption());
        newTopic.setSelected(this.selected);
        this.cloneInfo(newTopic);
        newTopic.graph = this.graph;
        newTopic.setNextAddedVertex(this.getNextAddedVertex());
        return newTopic;
    }

    public LDATopic(TIntArrayList vertex, TemporalProjection tprojection, TemporalGraph graph, LDAOutput output_lda) {
        super(vertex, tprojection, graph);
        this.tprojection = tprojection;
        this.tdata = tprojection.getTopicData();
        this.id = this.tdata.getNextAvailableId();
        this.output_lda = output_lda;
    }

    public LDATopic(TIntArrayList vertex, ArrayList<Coordinate> fake_vertex, TemporalProjection tprojection, TemporalGraph graph, LDAOutput output_lda) {
        super(vertex, fake_vertex, tprojection, graph);
        this.tprojection = tprojection;
        this.tdata = tprojection.getTopicData();
        this.id = this.tdata.getNextAvailableId();
        this.output_lda = output_lda;
    }

    @Override
    public void createTopic() {
        //identificando quais os tópicos acontecem mais nestes vértices
        ArrayList<TopicCandidate> topics_candidates = new ArrayList<>(output_lda.getNumTopics());
        this.initializeArray(topics_candidates);
        for (int i = 0; i < topics_candidates.size(); i++) {
            for (int j = 0; j < this.vertex_id.size(); j++) {
                topics_candidates.get(i).setValue(topics_candidates.get(i).value + output_lda.getProbabilityDocumentVsTopics(vertex_id.get(j), i));
            }
        }

        double sum = sumProbabilites(topics_candidates);
        for (TopicCandidate topics_candidate : topics_candidates) {
            topics_candidate.setValue(topics_candidate.value / sum);
        }

        Collections.sort(topics_candidates, new Comparator<TopicCandidate>() {
            @Override
            public int compare(TopicCandidate o1, TopicCandidate o2) {
                return Double.compare(o2.value, o1.value);
            }
        });

        double sum_topics = 0.0d;
        double totalInfTerms;
        int n_topic = 0, n_terms;
        TopicCandidate tc;
        Iterator<IDSorter> it;
        IDSorter word;;
        while (sum_topics < this.tdata.getLDAInformationTopics()) {
            tc = topics_candidates.get(n_topic);
            sum_topics += tc.value;
            it = this.output_lda.getWordsProbabilitesForTopic(tc.id).iterator();
            totalInfTerms = 0.0d;

            this.newTopic(tc.value);
            n_terms = 0;
            while (totalInfTerms < this.tdata.getLDAMinInformationTerms() || n_terms < 4) {
                word = it.next();
                totalInfTerms += word.getWeight() / this.output_lda.getSumWordsForTopic(tc.id);
                this.addTopicTag(this.output_lda.getWordFromAlphabet(word.getID()), word.getWeight() / this.output_lda.getSumWordsForTopic(tc.id));
                n_terms++;
            }
            n_topic++;
        }
    }

    private double sumProbabilites(ArrayList<TopicCandidate> topics_candidates) {
        double sum = 0;

        for (TopicCandidate topics_candidate : topics_candidates) {
            sum += topics_candidate.value;
        }
        return sum;
    }

    private void initializeArray(ArrayList<TopicCandidate> input) {
        for (int i = 0; i < this.output_lda.getNumTopics(); i++) {
            input.add(new TopicCandidate(i, 0.0));
        }
    }

    public class TopicCandidate {

        public int id;
        public double value;

        public TopicCandidate(int id, double value) {
            this.id = id;
            this.value = value;
        }

        public void setValue(double value) {
            this.value = value;
        }
    }
}
