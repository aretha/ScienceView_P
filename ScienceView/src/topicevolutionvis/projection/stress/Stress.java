/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.projection.stress;

import java.util.TreeMap;
import org.jfree.data.xy.XYSeriesCollection;
import topicevolutionvis.graph.TemporalGraph;
import topicevolutionvis.graph.Vertex;
import topicevolutionvis.matrix.SparseMatrix;
import topicevolutionvis.projection.distance.Dissimilarity;

/**
 *
 * @author barbosaa
 */
public abstract class Stress {

    public abstract XYSeriesCollection calculate(SparseMatrix matrix, Dissimilarity diss, TreeMap<Integer, TemporalGraph> graphs);

    public float euclideanDistance(Vertex v1, Vertex v2) {
        return (float) Math.sqrt( Math.pow(v1.getX() - v2.getX(), 2) +  Math.pow(v1.getY() - v2.getY(), 2));
    }

}
