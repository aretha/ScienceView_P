/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.matrix.normalization;

import java.io.IOException;
import topicevolutionvis.matrix.SparseMatrix;
import topicevolutionvis.matrix.SparseVector;

/**
 *
 * @author USER
 */
public class NormalizeRows2 extends Normalization {

    @Override
    public SparseMatrix execute(SparseMatrix matrix) throws IOException {
        for (int i = 0; i < matrix.getRowsCount(); i++) {
            SparseVector vector = matrix.getRowWithIndex(i);
            int[] index_vector = vector.getIndex();
            double max = Double.MIN_VALUE, min = Double.MAX_VALUE;
            for (int j = 0; j < index_vector.length; j++) {
                double value = vector.getValue(index_vector[j]);
                if (value > max) {
                    max = value;
                }
                if (value < min) {
                    min = value;
                }
            }
            for (int j = 0; j < index_vector.length; j++) {
                double value = vector.getValue(index_vector[j]);
                vector.setValue(index_vector[j], (value - min) / (max - min));
            }
        }
        return null;
    }
}
