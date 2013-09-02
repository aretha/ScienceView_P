/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.datamining.clustering.monic.transitions;

/**
 *
 * @author USER
 */
public class InternalTransition {

    public static final int CONTENT_CHANGE_TRANSITION = 0;
    public static final int COMPACTNESS_TRANSITION = 1;
    public static final int LOCATION_TRANSITION = 2;
    public static final int NO_CHANGE = 3;

    public InternalTransition(int type) {
        this.type = type;
    }
    
    public int getType(){
        return this.type;
    }
    
    private int type;
}
