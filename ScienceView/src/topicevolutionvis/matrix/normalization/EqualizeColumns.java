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
class EqualizeColumns extends Normalization {

    private static final int nrbins = 1000;

    @Override
    public SparseMatrix execute(SparseMatrix matrix) throws IOException {
        assert (matrix.getRowsCount() > 0) : "More than zero vectors must be used!";

        double[][] points = matrix.toDenseMatrix();

        //for each column
        for (int j = 0; j < matrix.getDimensions(); j++) {
            double[] hist = new double[EqualizeColumns.nrbins];
            Arrays.fill(hist, 0.0d);

            //find the maximum and minimum values
            double min = Double.POSITIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;

            for (int i = 0; i < points.length; i++) {
                if (points[i][j] > max) {
                    max = points[i][j];
                }

                if (points[i][j] < min) {
                    min = points[i][j];
                }
            }

            //creating the histogram
            for (int i = 0; i < points.length; i++) {
                if (max > min) {
                    int index = (int) (((points[i][j] - min) / (max - min)) * (EqualizeColumns.nrbins - 1));
                    hist[index] = hist[index] + 1;
                } else {
                    hist[0] = hist[0] + 1;
                }
            }

            //compute the cumulative histogram
            hist[0] = hist[0] / points.length;
            for (int i = 1; i < hist.length; i++) {
                hist[i] = hist[i - 1] + hist[i] / points.length;
            }

            //transformnig the values based on the new histogram
            for (int i = 0; i < points.length; i++) {
                if (max > min) {
                    int index = (int) (((points[i][j] - min) / (max - min)) * (EqualizeColumns.nrbins - 1));
                    points[i][j] = (hist[index] * (max - min)) + min;
                } else {
                    points[i][j] = 0.0f;
                }
            }
        }

        SparseMatrix eqmatrix = new SparseMatrix();
        eqmatrix.setAttributes(matrix.getAttributes());

        for (int i = 0; i < matrix.getRowsCount(); i++) {
            SparseVector oldv = matrix.getRowWithIndex(i);
            eqmatrix.addRow(new SparseVector(points[i], oldv.getId(), oldv.getKlass()));
        }

        return eqmatrix;

    }
}
