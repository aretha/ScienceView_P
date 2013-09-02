/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.preprocessing.transformation;

import java.util.Arrays;
import topicevolutionvis.matrix.SparseMatrix;
import topicevolutionvis.matrix.SparseVector;

/**
 *
 * @author barbosaa
 */
public class NTFIDF2Transformation implements MatrixTransformation {

    @Override
    public SparseMatrix tranform(SparseMatrix matrix, Object parameter) {
        matrix = (new NTFTransformation()).tranform(matrix, parameter);

        //Store the total count of the term along all documents
        double[] docsFreq = new double[matrix.getDimensions()];
        Arrays.fill(docsFreq, 0);

        for (int lin = 0; lin < matrix.getRowsCount(); lin++) {
            SparseVector sv = matrix.getRowWithIndex(lin);
            int svlength = sv.getIndex().length;

            for (int col = 0; col < svlength; col++) {
                docsFreq[sv.getIndex()[col]] += sv.getValue(col);
            }
        }

        //Calculate the redefined tfidf
        for (int lin = 0; lin < matrix.getRowsCount(); lin++) {
            SparseVector sv = matrix.getRowWithIndex(lin);
            int svlength = sv.getIndex().length;

            for (int col = 0; col < svlength; col++) {
                //get the term-frequency
                double tf = sv.getValues()[col];
                double idf = 0.0d;
                if (docsFreq[col] != 0) {
                    idf = Math.sqrt(matrix.getRowsCount() / docsFreq[sv.getIndex()[col]]);
                }

                //Calculate and store the tidf
                sv.getValues()[col] = (tf * idf);
            }

        }

        return matrix;
    }
}
