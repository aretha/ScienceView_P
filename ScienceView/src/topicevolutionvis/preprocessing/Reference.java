/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.preprocessing;

/**
 *
 * @author Aretha
 */
public class Reference implements Comparable<Reference> {

    public String reference;
    public int frequency;
    public int indexDatabase;

    public Reference(String reference, int frequency, int indexDatabase) {
        this.reference = reference;
        this.frequency = frequency;
        this.indexDatabase = indexDatabase;
    }

    @Override
    public int compareTo(Reference o) {
        return o.frequency - this.frequency;
    }
}
