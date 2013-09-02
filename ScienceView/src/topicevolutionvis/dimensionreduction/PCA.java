/* ***** BEGIN LICENSE BLOCK *****
 *
 * Copyright (c) 2005-2007 Universidade de Sao Paulo, Sao Carlos/SP, Brazil.
 * All Rights Reserved.
 *
 * This file is part of Projection Explorer (PEx).
 *
 * How to cite this work:
 * 
 @inproceedings{paulovich2007pex,
 author = {Fernando V. Paulovich and Maria Cristina F. Oliveira and Rosane 
 Minghim},
 title = {The Projection Explorer: A Flexible Tool for Projection-based 
 Multidimensional Visualization},
 booktitle = {SIBGRAPI '07: Proceedings of the XX Brazilian Symposium on 
 Computer Graphics and Image Processing (SIBGRAPI 2007)},
 year = {2007},
 isbn = {0-7695-2996-8},
 pages = {27--34},
 doi = {http://dx.doi.org/10.1109/SIBGRAPI.2007.39},
 publisher = {IEEE Computer Society},
 address = {Washington, DC, USA},
 }
 *  
 * PEx is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 *
 * PEx is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 *
 * This code was developed by members of Computer Graphics and Image
 * Processing Group (http://www.lcad.icmc.usp.br) at Instituto de Ciencias
 * Matematicas e de Computacao - ICMC - (http://www.icmc.usp.br) of 
 * Universidade de Sao Paulo, Sao Carlos/SP, Brazil. The initial developer 
 * of the original code is Fernando Vieira Paulovich <fpaulovich@gmail.com>.
 *
 * Contributor(s): Rosane Minghim <rminghim@icmc.usp.br>
 *
 * You should have received a copy of the GNU General Public License along 
 * with PEx. If not, see <http://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ***** */
package topicevolutionvis.dimensionreduction;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import gnu.trove.list.array.TIntArrayList;
import java.io.IOException;
import java.util.Arrays;
import topicevolutionvis.matrix.SparseMatrix;
import topicevolutionvis.projection.temporal.TemporalProjection;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class PCA extends DimensionalityReduction {

    public PCA(int targetDimension) {
        super(targetDimension);
    }

    @Override
    public double[][] execute(SparseMatrix matrix, TemporalProjection tproj) throws IOException {
        double[][] points = matrix.toDenseMatrix();
        double[][] covmatrix_aux = this.createCovarianceMatrix(points);

        DoubleMatrix2D covmatrix = new DenseDoubleMatrix2D(covmatrix_aux);
        EigenvalueDecomposition dec = new EigenvalueDecomposition(covmatrix);
        DoubleMatrix2D decomp = dec.getV();

        //storing the eigenvalues
        this.eigenvalues = new double[covmatrix_aux.length];
        DoubleMatrix1D eigenvalues_aux = dec.getRealEigenvalues();
        for (int i = 0; i < covmatrix_aux.length; i++) {
            this.eigenvalues[i] = eigenvalues_aux.get((covmatrix_aux.length - i - 1));
        }

        double[][] points_aux2 = new double[points.length][];
        for (int i = 0; i < points.length; i++) {
            points_aux2[i] = new double[points[i].length];
            System.arraycopy(points[i], 0, points_aux2[i], 0, points[i].length);
        }

        double[][] decomp_aux = new double[covmatrix_aux.length][];
        for (int i = 0; i < covmatrix_aux.length; i++) {
            decomp_aux[i] = new double[targetDimension];

            for (int j = 0; j < targetDimension; j++) {
                decomp_aux[i][j] = decomp.getQuick(i, covmatrix_aux[0].length - j - 1);
            }
        }

        DoubleMatrix2D decompostion = new DenseDoubleMatrix2D(decomp_aux);
        DoubleMatrix2D points_aux = new DenseDoubleMatrix2D(points_aux2);
        DoubleMatrix2D proj = points_aux.zMult(decompostion, null, 1.0, 1.0, false, false);

        //copying the projection
        double[][] projection = new double[points.length][];
        double[][] projection_aux = proj.toArray();

        for (int i = 0; i < projection_aux.length; i++) {
            projection[i] = new double[targetDimension];
            System.arraycopy(projection_aux[i], 0, projection[i], 0, projection_aux[i].length);
        }

        return projection;
    }

    public void setUseSamples(boolean useSamples) {
        this.useSamples = useSamples;
    }

    public double[] getEigenvalues() {
        return eigenvalues;
    }

    private double[][] useSamples(double[][] points) {
        double[][] samples = new double[points.length / 4][];
        TIntArrayList indexes = new TIntArrayList();

        int i = 0;
        while (indexes.size() < samples.length) {
            int index = (int) (Math.random() * (points.length - 1));
            if (!indexes.contains(index)) {
                samples[i] = points[index];
                indexes.add(index);
                i++;
            }
        }

        return samples;
    }

    private double[][] createCovarianceMatrix(double[][] points) {
        if (this.useSamples) {
            points = this.useSamples(points);
        }

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

        double[][] covmatrix = new double[points[0].length][];
        for (int i = 0; i < covmatrix.length; i++) {
            covmatrix[i] = new double[points[0].length];
        }

        for (int i = 0; i < covmatrix.length; i++) {
            for (int j = 0; j < covmatrix.length; j++) {
                covmatrix[i][j] = this.covariance(points, i, j);
            }
        }
        return covmatrix;
    }

    //calculate the covariance between columns a and b
    private double covariance(double[][] points, int a, int b) {
        double covariance = 0.0d;
        for (int i = 0; i < points.length; i++) {
            covariance += points[i][a] * points[i][b];
        }
        covariance /= (points.length - 1);
        return covariance;
    }
    private boolean useSamples = false;
    private double[] eigenvalues;
}
