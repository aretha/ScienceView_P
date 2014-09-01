/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.matrix.normalization;

import java.io.IOException;
import java.util.Arrays;
import topicevolutionvis.matrix.SparseMatrix;
import topicevolutionvis.matrix.SparseVector;

/**
 *
 * @author barbosaa
 */
class StandardizationColumns extends Normalization {

    private static final double EPSILON = 0.0000001f;

    @Override
    public SparseMatrix execute(SparseMatrix matrix) throws IOException {
        assert (matrix.getRowsCount() > 0) : "More than zero vectors must be used!";

        double[][] points = matrix.toDenseMatrix();

        //calculating the mean
        double[] mean = new double[points[0].length];
        Arrays.fill(mean, 0.0f);

        for (double[] point : points) {
            //calculating
            for (int j = 0; j < point.length; j++) {
                mean[j] += point[j];
            }
        }

        for (int i = 0; i < mean.length; i++) {
            mean[i] /= points.length;
        }

        for (double[] point : points) {
            for (int j = 0; j < point.length; j++) {
                point[j] -= mean[j];
            }
        }

        //calculating the standard deviation
        double[] deviation = new double[points[0].length];
        Arrays.fill(deviation, 0.0f);

        for (double[] point : points) {
            for (int j = 0; j < point.length; j++) {
                deviation[j] += (((double) (point[j])) * ((double) (point[j])));
            }
        }

        for (int i = 0; i < mean.length; i++) {
            deviation[i] = Math.sqrt((deviation[i] / (points.length - 1)));

            if (deviation[i] < EPSILON) {
                deviation[i] = EPSILON;
            }
        }

        for (double[] point : points) {
            for (int j = 0; j < point.length; j++) {
                if (deviation[j] != 0.0f) {
                    point[j] /= deviation[j];
                }
            }
        }


        SparseMatrix stdmatrix = new SparseMatrix();
        stdmatrix.setAttributes(matrix.getAttributes());

        for (int i = 0; i < matrix.getRowsCount(); i++) {
            SparseVector oldv = matrix.getRowWithIndex(i);
            stdmatrix.addRow(new SparseVector(points[i], oldv.getId(), oldv.getKlass()));
        }
        return stdmatrix;

    }
}
