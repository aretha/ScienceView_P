/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.datamining.clustering.monic;

import java.util.ArrayList;
import topicevolutionvis.datamining.clustering.monic.transitions.ContentChangeTransition;
import topicevolutionvis.datamining.clustering.monic.transitions.InternalTransition;
import topicevolutionvis.topic.Topic;

/**
 *
 * @author USER
 */
public class DocumentClusterEvent {

    public static final int SURVIVED = 0;
    public static final int NEW_CLUSTER = 1;
    public static final int SPLIT = 2;
    public static final int MERGED = 3;
    public static final int DISAPPEARS = 4;
    private int type, year;
    private ArrayList<Topic> input, output;
    private ArrayList<InternalTransition> internal_transitions = new ArrayList<>();

    public DocumentClusterEvent(int year, int type) {
        this.year = year;
        this.type = type;
        this.input = new ArrayList<>();
        this.output = new ArrayList<>();
    }

    public void addTopicToOutput(Topic t) {
        this.output.add(t);
    }

    public void addTopicToInput(Topic t) {
        this.input.add(t);
    }

    public void addTopicsToOutput(ArrayList<Topic> topics) {
        this.output.addAll(topics);
    }

    public ArrayList<Topic> getInputDC() {
        return this.input;
    }

    public ArrayList<Topic> getOutputDC() {
        return this.output;
    }

    public int getType() {
        return this.type;
    }

    public void addInternalTransition(InternalTransition transition) {
        this.internal_transitions.add(transition);
    }

    public ArrayList<InternalTransition> getInternalTransitions() {
        return this.internal_transitions;
    }

    public ContentChangeTransition getContentChangeTransition() {
        for (InternalTransition internal_transition : internal_transitions) {
            if (internal_transition.getType() == InternalTransition.CONTENT_CHANGE_TRANSITION) {
                return (ContentChangeTransition) internal_transition;
            }
        }
        return null;
    }
}
