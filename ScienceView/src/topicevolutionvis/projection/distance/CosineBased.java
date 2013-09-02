package topicevolutionvis.projection.distance;

import topicevolutionvis.matrix.SparseVector;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class CosineBased implements Dissimilarity {

    @Override
    public double calculate(SparseVector v1, SparseVector v2) {
        assert (v1.size() == v2.size()) : "ERROR: vectors of different sizes!";

        double n1 = v1.norm();
        double n2 = v2.norm();

        double cosine = -1.0d;
        if (n1 != 0.0d && n2 != 0.0d) {
            cosine = v1.dot(v2) / (n1 * n2);

            if (cosine > 1.0d) {
                cosine = 1.0d;
            } else if (cosine < -1.0d) {
                cosine = -1.0d;
            }

        } else if (n1 == 0.0d && n2 == 0.0d) {
            cosine = 1.0d;
        }

        return (1.0d - cosine);
    }
}
