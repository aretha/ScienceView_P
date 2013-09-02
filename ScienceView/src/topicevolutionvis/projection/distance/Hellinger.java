/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.projection.distance;

import topicevolutionvis.matrix.SparseVector;

/**
 *
 * @author USER
 */
public class Hellinger implements Dissimilarity {

    @Override
    public double calculate(SparseVector v1, SparseVector v2) {
        assert (v1.size() == v2.size()) : "ERROR: vectors of different sizes!";
        double[] v1_dense = v1.toDenseVector();
        double[] v2_dense = v2.toDenseVector();
        double sum = 0;
        for (int i = 0; i < v1_dense.length; i++) {
            sum += Math.pow(Math.sqrt(v1_dense[i]) - Math.sqrt(v2_dense[i]), 2);
        }
        return Math.sqrt(sum) / (Math.log(2) / Math.log(2));
    }
}
