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
package topicevolutionvis.matrix.normalization;

import java.io.IOException;
import java.util.Arrays;
import topicevolutionvis.matrix.SparseMatrix;
import topicevolutionvis.matrix.SparseVector;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class Standardization extends Normalization {

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
                deviation[j] += ((point[j]) * (point[j]));
            }
        }

        for (int i = 0; i < mean.length; i++) {
            deviation[i] = Math.sqrt((deviation[i] / (points.length - 1)));
        }

        //normalization
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                points[i][j] /= deviation[j];
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
