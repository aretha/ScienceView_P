package topicevolutionvis.projection.distance;

import topicevolutionvis.matrix.SparseVector;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public interface Dissimilarity {

    public double calculate(SparseVector v1, SparseVector v2);

}
