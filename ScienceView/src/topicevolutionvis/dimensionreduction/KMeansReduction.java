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

import gnu.trove.list.array.TIntArrayList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import topicevolutionvis.matrix.SparseMatrix;
import topicevolutionvis.projection.temporal.TemporalProjection;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class KMeansReduction extends DimensionalityReduction {

    /**
     * Creates a new instance of KMeansReduction
     *
     * @param targetDimension
     */
    public KMeansReduction(int targetDimension) {
        super(targetDimension);
    }

    @Override
    public double[][] execute(SparseMatrix matrix, TemporalProjection tproj) throws IOException {
        double[][] points = matrix.toDenseMatrix();

        ArrayList<TIntArrayList> dimClusters = this.doClustering(points);

        double[][] newPoints = new double[points.length][];

        for (int i = 0; i < newPoints.length; i++) {
            newPoints[i] = new double[dimClusters.size()];
            Arrays.fill(newPoints[i], 0.0f);
        }

        for (int j = 0; j < dimClusters.size(); j++) {
            TIntArrayList cluster = dimClusters.get(j);

            for (int i = 0; i < newPoints.length; i++) {
                for (int k = 0; k < cluster.size(); k++) {
                    newPoints[i][j] += points[i][cluster.get(k)];
                }
                newPoints[i][j] /= cluster.size();
            }
        }

        return newPoints;
    }

    private ArrayList<TIntArrayList> doClustering(double[][] points) throws IOException {
        ArrayList<TIntArrayList> clusters = new ArrayList<>();

        //Variavel auxiliar para armazenar as centr�ides antigas
        double[][] oldCentroids;

        //indica se as centr�ides foram modificadas
        boolean isCentroidModified = true;

        //serve para executar o algoritmo at� um n�mero limitado de vazes
        int numberIterations = 0;

        //inicializar as centr�ides com alguns pontos
        this.centroids = new double[points.length][];
        for (int i = 0; i < points.length; i++) {
            this.centroids[i] = new double[this.targetDimension];
            System.arraycopy(points[i], 0, this.centroids[i], 0, this.targetDimension);
        }

        while (isCentroidModified && numberIterations < 30) {
            clusters.clear();
            for (int i = 0; i < this.targetDimension; i++) {
                clusters.add(new TIntArrayList());
            }

            //colocar os pontos dentro dos seus respectivos clusters
            for (int dim = 0; dim < points[0].length; dim++) {
                int nearestCentroid = 0; //armazenar� a centr�ide que a dimens�o pertencer�
                double distance1 = this.calculateDistance(points, dim, nearestCentroid);

                //para cada cluster existente, verificar qual centr�ide a dimens�o
                //est� mais pr�xima
                for (int cluster = 1; cluster < this.targetDimension; cluster++) {
                    double distance2 = this.calculateDistance(points, dim, cluster);

                    if (distance1 > distance2) {
                        nearestCentroid = cluster;
                        distance1 = distance2;
                    }
                }

                //update the centroid
                this.updateCentroid(clusters.get(nearestCentroid).size(), nearestCentroid, dim, points);

                //colocar o ponto no cluster apropriado
                clusters.get(nearestCentroid).add(dim);
            }

            //Armazena os valores antigos das centr�ides
            oldCentroids = this.centroids;

            //Atualiza as centr�ides
//            this.updateCentroids(points, clusters);

            //Se n�o houve modifica��o, para o looping
            isCentroidModified = this.isCentroidModified(oldCentroids);

            //Incrementa a quantidade de vezes que foram realocados os pontos
            numberIterations++;
        }

        for (int c = 0; c < clusters.size(); c++) {
            if (clusters.get(c).isEmpty()) {
                clusters.remove(c);
                c--;
            }
        }

        return clusters;
    }

    public double calculateDistance(double[][] points, int dim, int nearestCentroid) throws IOException {
        if (!this.isNormalized) {
            this.normalize(points);
        }

        double norm = 0.0;
        for (int i = 0; i < points.length; i++) {
            norm += this.centroids[i][nearestCentroid] * this.centroids[i][nearestCentroid];
        }
        norm = Math.sqrt(norm);

        double sim = 0.0;
        for (int i = 0; i < points.length; i++) {
            sim += points[i][dim] * this.centroids[i][nearestCentroid];
        }
        sim = sim / norm;

        return Math.sqrt(Math.abs(2 * (1.0 - sim)));
    }

    private void updateCentroid(int nrOldPoints, int centroid, int dim, double[][] points) {
        for (int i = 0; i < points.length; i++) {
            this.centroids[i][centroid] = (((this.centroids[i][centroid] * nrOldPoints) + points[i][dim]) / (nrOldPoints + 1));
        }
    }

//    private void updateCentroids(float[][] points, ArrayList<ArrayList<Integer>> clusters) {
//        //zerar as centroids
//        this.centroids = new float[points.length][];
//        for (int i = 0; i < points.length; i++) {
//            this.centroids[i] = new float[this.targetDimension];
//            Arrays.fill(this.centroids[i], 0.0f);
//        }
//
//        for (int j = 0; j < clusters.size(); j++) {
//            ArrayList<Integer> cluster = clusters.get(j);
//
//            if (cluster.size() > 0) {
//                for (int i = 0; i < this.centroids.length; i++) {
//                    for (int k = 0; k < cluster.size(); k++) {
//                        this.centroids[i][j] += points[i][cluster.get(k)];
//                    }
//                    this.centroids[i][j] /= cluster.size();
//                }
//            } else {
//                for (int i = 0; i < this.centroids.length; i++) {
//                    this.centroids[i][j] = points[i][j];
//                }
//            }
//        }
//    }
    private boolean isCentroidModified(double[][] oldCentroids) {
        for (int centroid = 0; centroid < oldCentroids.length; centroid++) {
            for (int coord = 0; coord < oldCentroids[centroid].length; coord++) {
                if (oldCentroids[centroid][coord] != this.centroids[centroid][coord]) {
                    return true;
                }
            }
        }

        return false;
    }

    private void normalize(double[][] points) {
        this.isNormalized = true;

        double[] termSum = new double[points[0].length];
        Arrays.fill(termSum, 0.0f);

        for (int j = 0; j < points[0].length; j++) {
            for (int i = 0; i < points.length; i++) {
                termSum[j] += points[i][j] * points[i][j];
            }

            termSum[j] = Math.sqrt(termSum[j]);
        }

        for (int j = 0; j < points[0].length; j++) {
            for (int i = 0; i < points.length; i++) {
                if (termSum[j] != 0.0) {
                    points[i][j] = points[i][j] / termSum[j];
                } else {
                    points[i][j] = 0.0f;
                }
            }
        }
    }
    private double[][] centroids;
    private boolean isNormalized = false;
}
