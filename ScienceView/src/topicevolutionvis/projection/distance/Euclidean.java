package topicevolutionvis.projection.distance;

import topicevolutionvis.matrix.SparseVector;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class Euclidean implements Dissimilarity {

    @Override
    public double calculate(SparseVector v1, SparseVector v2) {
        assert (v1.size() == v2.size()) : "ERROR: vectors of different sizes!";
        
        double n1 = v1.norm();
        double n2 = v2.norm();
        double dot = v1.dot(v2);
        
        return Math.sqrt(Math.abs(n1 * n1 + n2 * n2 - 2 * dot));
    }

}
