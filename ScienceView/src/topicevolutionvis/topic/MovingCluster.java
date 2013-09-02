/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.topic;

import java.util.ArrayList;
import java.util.TreeMap;
import topicevolutionvis.graph.Vertex;

/**
 *
 * @author USER
 */
public class MovingCluster {

    private TreeMap<Integer, ArrayList<Vertex>> content;
    static int next_available_id = 0;
    private int id;

    public MovingCluster(Integer year, ArrayList<Vertex> value) {
        content = new TreeMap<>();
        content.put(year, value);
        id = next_available_id;
        next_available_id++;
    }

    public Integer getFirstYear() {
        return content.firstKey();
    }

    public int getId() {
        return this.id;
    }

    public int getNumberOfDocumentsAtYear(Integer year) {
        return content.get(year).size();
    }

    public ArrayList<Vertex> getContentAtYear(Integer year) {
        return content.get(year);
    }

    public boolean hasContentAtYear(Integer year) {
        return content.containsKey(year);
    }

    public void addContentForYear(Integer year, ArrayList<Vertex> value) {
        this.content.put(year, value);
    }
}
