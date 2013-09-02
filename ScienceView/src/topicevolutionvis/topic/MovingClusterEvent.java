/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.topic;

import java.util.ArrayList;

/**
 *
 * @author USER
 */
public class MovingClusterEvent {

    public static final int SPLIT = 0;
    public static final int MERGE = 1;
    public static final int NEW = 2;
    public int type = -1;
    ArrayList<MovingCluster> input = new ArrayList<>();
    ArrayList<MovingCluster> output = new ArrayList<>();

    public MovingClusterEvent(int type) {
        this.type = type;
    }
    
    public void addInput(MovingCluster mc){
        this.input.add(mc);
    }
    
    public void addOutput(MovingCluster mc){
        this.output.add(mc);
    }
    
}
