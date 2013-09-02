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
package topicevolutionvis.util;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import topicevolutionvis.matrix.SparseMatrix;
import topicevolutionvis.projection.distance.Dissimilarity;
import topicevolutionvis.projection.distance.DistanceMatrix;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class KNN {

    public KNN(int nrNeighbors) {
        this.nrNeighbors = nrNeighbors;
    }

    public Pair[][] execute(SparseMatrix matrix, Dissimilarity diss) throws IOException {
        return this.execute(new DistanceMatrix(matrix, diss));
    }

    public Pair[][] exectute(SparseMatrix matrix, Dissimilarity diss, TIntArrayList update) {
        try {
            TIntObjectHashMap<TIntArrayList> neighbors = new TIntObjectHashMap<>();
            DistanceMatrix dist_matrix = new DistanceMatrix(matrix, diss);

            return null;
        } catch (IOException ex) {
            Logger.getLogger(KNN.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Pair[][] execute(DistanceMatrix dmat) throws IOException {
//        long start = System.currentTimeMillis();
        Pair[][] neighbors;

        if (this.nrNeighbors > dmat.getElementCount() - 1) {
            throw new IOException("Number of neighbors (" + this.nrNeighbors + ") bigger than the number of "
                    + "elements minus one (" + dmat.getElementCount() + ")(an element is not computed as a neighbor "
                    + "of itself)!");
        }

        //init the neighbors list
        neighbors = new Pair[dmat.getElementCount()][];
        for (int i = 0; i < neighbors.length; i++) {
            neighbors[i] = new Pair[this.nrNeighbors];

            for (int j = 0; j < neighbors[i].length; j++) {
                neighbors[i][j] = new Pair(-1, Double.MAX_VALUE);
            }
        }

        for (int i = 0; i < dmat.getElementCount(); i++) {
            for (int j = 0; j < dmat.getElementCount(); j++) {
                if (i == j) {
                    continue;
                }

                double dist = dmat.getDistance(i, j);

                if (dist < neighbors[i][neighbors[i].length - 1].value) {
                    for (int k = 0; k < neighbors[i].length; k++) {
                        if (neighbors[i][k].value > dist) {
                            for (int n = neighbors[i].length - 1; n > k; n--) {
                                neighbors[i][n].index = neighbors[i][n - 1].index;
                                neighbors[i][n].value = neighbors[i][n - 1].value;
                            }

                            neighbors[i][k].index = j;
                            neighbors[i][k].value = dist;
                            break;
                        }
                    }
                }
            }
        }

//        long finish = System.currentTimeMillis();

//        Logger.getLogger(this.getClass().getName()).log(Level.INFO,
//                "KNN time: " + (finish - start) / 1000.0f + "s");

        return neighbors;
    }
    private int nrNeighbors = 5;
}
