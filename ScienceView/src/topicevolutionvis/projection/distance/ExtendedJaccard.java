package topicevolutionvis.projection.distance;

import topicevolutionvis.matrix.SparseVector;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class ExtendedJaccard implements Dissimilarity {

    @Override
    public double calculate(SparseVector v1, SparseVector v2) {
        assert (v1.size() == v2.size()) : "ERROR: vectors of different sizes!";

        double n1 = v1.norm();
        double n2 = v2.norm();

        if (n1 != 0.0d && n2 != 0.0d) {
            double dot = v1.dot(v2);
            double coef = (dot / (n1 * n1 + n2 * n2 - dot));

            return (1.0d / (1.0d + coef));

        } else if (n1 == 0.0d && n2 == 0.0d) {
            return 0.0d;
        } else {
            return 1.0d;
        }
    }

}
