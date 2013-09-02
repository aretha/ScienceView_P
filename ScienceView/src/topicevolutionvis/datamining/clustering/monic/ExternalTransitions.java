/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.datamining.clustering.monic;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
import topicevolutionvis.topic.Topic;

/**
 *
 * @author Aretha
 */
public class ExternalTransitions {

    private TreeMap<Integer, ArrayList<DocumentClusterEvent>> transitions = new TreeMap<>();

    public void addExternalTransition(Integer year, DocumentClusterEvent dc) {
        ArrayList<DocumentClusterEvent> aux = this.transitions.get(year);
        if (aux != null) {
            aux.add(dc);
        } else {
            aux = new ArrayList<>();
            aux.add(dc);
            transitions.put(year, aux);
        }
    }

    public Set<Integer> getYears() {
        return this.transitions.keySet();
    }

    public ArrayList<DocumentClusterEvent> getExternalTransistions(Integer year) {
        return this.transitions.get(year);
    }

    public boolean appearedInSplitThisYear(Integer year, Topic t) {
        if (this.transitions.get(year) != null) {
            for (DocumentClusterEvent dc_event : this.transitions.get(year)) {
                if (dc_event.getType() == DocumentClusterEvent.SPLIT) {
                    if (dc_event.getOutputDC().contains(t)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public ArrayList<DocumentClusterEvent> getExternalTransitionsByType(Integer year, Integer type) {
        ArrayList<DocumentClusterEvent> events = new ArrayList<>();
        for (DocumentClusterEvent dc_event : this.transitions.get(year)) {
            if (dc_event.getType() == type) {
                events.add(dc_event);
            }
        }
        return events;
    }

    public boolean hasEvents() {
        return !this.transitions.isEmpty();
    }

    public void clear() {
        this.transitions.clear();
    }
}
