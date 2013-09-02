/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.projection.distance;

import cc.mallet.util.Maths;
import topicevolutionvis.matrix.SparseVector;

/**
 *
 * @author USER
 */
public class JensenShanon implements Dissimilarity {

    @Override
    public double calculate(SparseVector v1, SparseVector v2) {
        assert (v1.size() == v2.size()) : "ERROR: vectors of different sizes!";
        return Maths.jensenShannonDivergence(v1.toDenseVector(), v2.toDenseVector());

    }
}
