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

        for (int i = 0; i < points.length; i++) {
            //calculating
            for (int j = 0; j < points[i].length; j++) {
                mean[j] += points[i][j];
            }
        }

        for (int i = 0; i < mean.length; i++) {
            mean[i] /= points.length;
        }

        //extracting the mean
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                points[i][j] -= mean[j];
            }
        }

        //calculating the standard deviation
        double[] deviation = new double[points[0].length];
        Arrays.fill(deviation, 0.0f);

        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                deviation[j] += (((double) (points[i][j])) * ((double) (points[i][j])));
            }
        }

        for (int i = 0; i < mean.length; i++) {
            deviation[i] = Math.sqrt((deviation[i] / (points.length - 1)));

            if (deviation[i] < EPSILON) {
                deviation[i] = EPSILON;
            }
        }

        //normalization
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                if (deviation[j] != 0.0f) {
                    points[i][j] /= deviation[j];
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
