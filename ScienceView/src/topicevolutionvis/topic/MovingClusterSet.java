/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.topic;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author USER
 */
public class MovingClusterSet {

    HashMap<Integer, MovingCluster> set = new HashMap<>();

    public ArrayList<MovingCluster> getMovingClustersPresentAtYear(int year) {
        ArrayList<MovingCluster> subset = new ArrayList<>();
        for (MovingCluster mc : set.values()) {
            if (mc.hasContentAtYear(year)) {
                subset.add(mc);
            }
        }
        return subset;
    }

    public void addMovingCluster(MovingCluster mc) {
        this.set.put(mc.getId(), mc);
    }
}
