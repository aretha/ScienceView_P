/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.preprocessing.transformation;

import topicevolutionvis.matrix.SparseMatrix;
import topicevolutionvis.matrix.SparseVector;

/**
 *
 * @author barbosaa
 */
class TFIDFTransformation implements MatrixTransformation {

    @Override
    public SparseMatrix tranform(SparseMatrix matrix, Object parameter) {
        //Store the number of documents which the term occur
        double[] docsFreq = new double[matrix.getDimensions()];

        //Count the number of documents which the terms occurr
        SparseVector sv;
        for (int lin = 0; lin < matrix.getRowsCount(); lin++) {
            sv = matrix.getRowWithIndex(lin);
            int svlength = sv.getIndex().length;
            for (int col = 0; col < svlength; col++) {
                docsFreq[sv.getIndex()[col]]++;
            }
        }

        //Calculate the tfidf
        for (int lin = 0; lin < matrix.getRowsCount(); lin++) {
            sv = matrix.getRowWithIndex(lin);
            int svlength = sv.getIndex().length;

            for (int col = 0; col < svlength; col++) {
                //get the term-frequency
                double tf = sv.getValues()[col];

                double idf = 0.0d;
                if (docsFreq[col] != 0) {
                    idf = Math.log(matrix.getRowsCount() / docsFreq[sv.getIndex()[col]]);
                }

                //Calculate and store the tidf
                sv.getValues()[col] = (tf * idf);
            }
        }

        return matrix;
    }
}
