/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.matrix.normalization;

import java.io.IOException;
import topicevolutionvis.matrix.SparseMatrix;

/**
 *
 * @author barbosaa
 */
class RemoveMean extends Normalization {

    @Override
    public SparseMatrix execute(SparseMatrix matrix) throws IOException {
        assert (matrix.getRowsCount() > 0) : "More than zero vectors must be used!";

        double mean;
        double[] vect;
        for (int i = 0; i < matrix.getRowsCount(); i++) {
            vect = matrix.getRowWithIndex(i).getValues();

            //calculate the mean
            mean = 0;
            for (int j = 0; j < vect.length; j++) {
                mean = mean + vect[j];
            }
            mean /= vect.length;

            //extracting the mean
            for (int j = 0; j < vect.length; j++) {
                vect[j] = vect[j] - mean;
            }
        }

        return matrix;
    }
}
