/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.projection.lsp;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.CholeskyDecomposition;
import gnu.trove.list.array.TIntArrayList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import lspsolver.Solver;
import topicevolutionvis.datamining.clustering.BKmeans;
import topicevolutionvis.matrix.SparseMatrix;
import topicevolutionvis.matrix.SparseVector;
import topicevolutionvis.projection.Projection;
import topicevolutionvis.projection.ProjectionData;
import topicevolutionvis.projection.distance.Dissimilarity;
import topicevolutionvis.projection.distance.DissimilarityFactory;
import topicevolutionvis.projection.distance.DistanceMatrix;
import topicevolutionvis.projection.idmap.IDMAPProjection;
import topicevolutionvis.util.ANN;
import topicevolutionvis.util.KNN;
import topicevolutionvis.util.Pair;
import topicevolutionvis.wizard.ProjectionViewWizard;

/**
 *
 * @author Aretha
 */
public class LSPProjection2D extends Projection {

    private double[][] projection_cp = null;
    private int[] controlPoints = null;
    private Dissimilarity diss;
    private Pair[][] mesh;
    private boolean knn = false;
    private int numberNeighborsConnection;
    private int numberControlPoint;

    public void setParameters(int numberControlPoint, int numberNeighborsConnection) {
        this.numberControlPoint = numberControlPoint;
        this.numberNeighborsConnection = numberNeighborsConnection;
    }

    @Override
    public double[][] project(SparseMatrix matrix, ProjectionData pdata, ProjectionViewWizard view) {
        this.matrix = matrix;

        double projection[][] = null;

        ArrayList<TIntArrayList> clusters = null;
        ArrayList<SparseVector> centroids = null;
        try {
            this.diss = DissimilarityFactory.getInstance(pdata.getDissimilarityType());

            if (this.controlPoints == null) {
                if (pdata.getControlPointsChoice() == ControlPointsType.KMEANS) {
                    if (view != null) {
                        view.setStatus("Calculating the B-KMEANS...", 40);
                    }

                    //clustering the points
//                  BKmedoids bkmedoids = new BKmedoids(pdata.getNumberControlPoints());
//                  clusters = bkmedoids.execute(diss, matrix);
//                  centroids = bkmedoids.getMedoidsMatrix(matrix);
//                  controlPoints = bkmedoids.getMedoids();

                    BKmeans bkmeans = new BKmeans(numberControlPoint);
                    clusters = bkmeans.execute(diss, matrix);
                    centroids = bkmeans.getCentroids();
                    controlPoints = bkmeans.getMedoids(matrix);


                    //if less medoids are returned than the expected (due to the
                    //clustering method employed), choose on the clusters other
                    //medoids
                    TIntArrayList medoids_aux = new TIntArrayList();
                    for (int i = 0; i < controlPoints.length; i++) {
                        medoids_aux.add(controlPoints[i]);
                    }

                    while (medoids_aux.size() < numberControlPoint) {
                        for (int c = 0; c < clusters.size() && medoids_aux.size() < this.numberControlPoint; c++) {
                            if (clusters.get(c).size() > matrix.getRowsCount() / numberControlPoint) {
                                for (int i = 0; i < clusters.get(c).size(); i++) {
                                    if (!medoids_aux.contains(clusters.get(c).get(i))) {
                                        medoids_aux.add(clusters.get(c).get(i));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    controlPoints = new int[medoids_aux.size()];
                    for (int i = 0; i < controlPoints.length; i++) {
                        controlPoints[i] = medoids_aux.get(i);
                    }
                    this.numberControlPoint = controlPoints.length;

                } else if (pdata.getControlPointsChoice() == ControlPointsType.RANDOM) {
                    //Random choice
                    controlPoints = new int[numberControlPoint];
                    for (int i = 0; i < controlPoints.length; i++) {
                        controlPoints[i] = (int) (Math.random() * matrix.getRowsCount());
                    }
                }
            } else {
                //clustering the points
//                BKmedoids bkmedoids = new BKmedoids(pdata.getNumberControlPoints());
//                clusters = bkmedoids.execute(diss, matrix);
//                centroids = bkmedoids.getMedoidsMatrix(matrix);
//
                BKmeans bkmeans = new BKmeans(numberControlPoint);
                clusters = bkmeans.execute(this.diss, matrix);
                centroids = bkmeans.getCentroids();
            }

            if (view != null) {
                view.setStatus("Calculating the nearest neigbbors...", 35);
            }

            //getting the nearest neighbors
            if (knn) {
                KNN ann = new KNN(this.numberNeighborsConnection);
                mesh = ann.execute(matrix, diss);
            } else {
                ANN appknn = new ANN(numberNeighborsConnection);
                mesh = appknn.execute(matrix, this.diss, clusters, centroids);
            }

            if (projection_cp == null) {
                SparseMatrix matrix_cp = new SparseMatrix();
                for (int i = 0; i < this.controlPoints.length; i++) {
                    matrix_cp.addRow(matrix.getRowWithIndex(this.controlPoints[i]));
                }
                DistanceMatrix dmat_cp = new DistanceMatrix(matrix_cp, this.diss);

                //Projecting the control points
                if (view != null) {
                    view.setStatus("Projecting...", 40);
                }
                IDMAPProjection idmap = new IDMAPProjection();
                projection_cp = idmap.project(dmat_cp, pdata, view);

//                ISOMAPProjection isomap = new ISOMAPProjection();
//                projection_cp = isomap.project(dmat_cp, pdata, view);
            }
            //creating the KNN mesh
            if (view != null) {
                view.setStatus("Creating the mesh...", 45);
            }

            MeshGenerator meshgen = new MeshGenerator();
            mesh = meshgen.execute(mesh, matrix, this.diss);

            //creating the final projection
            if (view != null) {
                view.setStatus("Solving the system...", 65);
            }

            projection = this.createFinalProjection(mesh);

        } catch (IOException ex) {
            Logger.getLogger(LSPProjection2D.class.getName()).log(Level.SEVERE, null, ex);
        }
        return projection;
    }

    public Pair[][] getUsedMesh() {
        return this.mesh;
    }

    public void setControlPoints(int[] controlPoints, double[][] projection_cp) {
        this.controlPoints = controlPoints;
        this.projection_cp = projection_cp;
    }

    private double[][] createFinalProjection(Pair[][] neighbors) throws IOException {

        double projection[][] = new double[matrix.getRowsCount()][];
//        if (System.getProperty("os.name").toLowerCase().equals("windows xp")
//                || System.getProperty("os.name").toLowerCase().equals("windows vista")
//                || System.getProperty("os.name").toLowerCase().indexOf("linux") > -1) {
        //this.projectUsingColt(neighbors, projection);
        this.projectUsingProgram(neighbors, projection);
//        }
//        else {
//            this.projectUsingColt(neighbors, projection);
//        }

        return projection;
    }

    public void setUseKnn(boolean knn) {
        this.knn = knn;
    }

    private void projectUsingColt(Pair[][] neighbors, double[][] projection) {
        long start = System.currentTimeMillis();

        int nRows = neighbors.length + this.numberControlPoint;
        int nColumns = neighbors.length;
        SparseDoubleMatrix2D A = new SparseDoubleMatrix2D(nRows, nColumns);

        for (int i = 0; i < neighbors.length; i++) {
            // new approach to increase the neighborhood precision
            A.setQuick(i, i, 1.0);

            // for (int j = 0; j < neighbors[i].length; j++) {
            // A.setQuick(i, neighbors[i][j].index, (-(1.0f / neighbors[i].length)));
            // }

            double max = Double.NEGATIVE_INFINITY;
            double min = Double.POSITIVE_INFINITY;

            for (Pair item : neighbors[i]) {
                if (max < item.value) {
                    max = item.value;
                }
                if (min > item.value) {
                    min = item.value;
                }
            }

            double sum = 0;
            for (Pair item : neighbors[i]) {
                if (max > min) {
                    double dist = (((item.value - min) / (max - min)) * (0.9)) + 0.1;
                    sum += (1 / dist);
                }
            }

            for (Pair item : neighbors[i]) {
                if (max > min) {
                    double dist = (((item.value - min) / (max - min)) * (0.9)) + 0.1;
                    A.setQuick(i, item.index, -((1 / dist) / sum));
                } else {
                    A.setQuick(i, item.index, -(1.0 / neighbors[i].length));
                }
            }
        }

        // Creating the Fij
        for (int i = 0; i < this.numberControlPoint; i++) {
            A.setQuick((projection.length + i), this.controlPoints[i], 60.0);
        }

        SparseDoubleMatrix2D B = new SparseDoubleMatrix2D(nRows, 2);
        for (int i = 0; i < this.projection_cp.length; i++) {
            B.setQuick((neighbors.length + i), 0, this.projection_cp[i][0] * 60);
            B.setQuick((neighbors.length + i), 1, this.projection_cp[i][1] * 60);
        }

        // Solving
        DoubleMatrix2D AtA = A.zMult(A, null, 1.0, 1.0, true, false);
        DoubleMatrix2D AtB = A.zMult(B, null, 1.0, 1.0, true, false);

        CholeskyDecomposition chol = new CholeskyDecomposition(AtA);
        DoubleMatrix2D X = chol.solve(AtB);

        for (int i = 0; i < X.rows(); i++) {
            projection[i] = new double[2];
            projection[i][0] = X.getQuick(i, 0);
            projection[i][1] = X.getQuick(i, 1);
        }

        long finish = System.currentTimeMillis();

        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Solving the system using Colt time: {0}s", (finish - start) / 1000.0f);
    }

    private void projectUsingProgram(Pair[][] neighbors, double[][] projection) {
        long start = System.currentTimeMillis();

        double max, min, sum, dist;
        int nRows = neighbors.length + this.numberControlPoint;
        int nColumns = neighbors.length;

        Solver solver = new Solver(nRows, nColumns);

        try {
            ////////////////////////////////////////////
            //creating matrix A
            for (int i = 0; i < neighbors.length; i++) {
                //new approach to increase the neighborhood precision
                solver.addToA(i, i, 1.0);

                max = Double.NEGATIVE_INFINITY;
                min = Double.POSITIVE_INFINITY;

                for (Pair item : neighbors[i]) {
                    if (max < item.value) {
                        max = item.value;
                    }
                    if (min > item.value) {
                        min = item.value;
                    }
                }

                sum = 0;
                for (Pair item : neighbors[i]) {
                    if (max > min) {
                        dist = (((item.value - min) / (max - min)) * (0.9)) + 0.1;
                        sum += (1 / dist);
                    }
                }

                for (Pair item : neighbors[i]) {
                    if (max > min) {
                        dist = (((item.value - min) / (max - min)) * (0.9)) + 0.1;
                        solver.addToA(i, item.index, -((1.0 / dist) / sum));
                    } else {
                        solver.addToA(i, item.index, -(1.0 / neighbors[i].length));
                    }
                }
            }

            for (int i = 0; i < this.numberControlPoint; i++) {
                solver.addToA((projection.length + i), this.controlPoints[i], 10d);
            }

            ////////////////////////////////////////////
            //creating matrix B
            for (int i = 0; i < this.projection_cp.length; i++) {
                solver.addToB((neighbors.length + i), 0, this.projection_cp[i][0] * 10);
                solver.addToB((neighbors.length + i), 1, this.projection_cp[i][1] * 10);
            }

            double[] result = solver.solve();
            for (int i = 0; i < result.length; i += 2) {
                projection[i / 2] = new double[2];
                projection[i / 2][0] = result[i];
                projection[i / 2][1] = result[i + 1];
            }

        } catch (IOException ex) {
            Logger.getLogger(LSPProjection2D.class.getName()).log(Level.SEVERE, null, ex);
        }

        long finish = System.currentTimeMillis();

        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Solving the system using  LSPSolver time: {0}s", (finish - start) / 1000.0f);
    }

//    private void projectUsingUJMP(ProjectionData pdata, Pair[][] neighbors, float[][] projection) {
//        long start = System.currentTimeMillis();
//
//        int nRows = neighbors.length + pdata.getNumberControlPoints();
//        int nColumns = neighbors.length;
//
//        DefaultDenseDoubleMatrix2DFactory factory = new DefaultDenseDoubleMatrix2DFactory();
//        DenseDoubleMatrix2D A = factory.zeros(nRows, nColumns);
//
//        for (int i = 0; i < neighbors.length; i++) {
//            A.setAsDouble(1.0, i, i);
//            for (int j = 0; j < neighbors[i].length; j++) {
//                A.setAsFloat((-(1.0f / neighbors[i].length)), i, neighbors[i][j].index);
//            }
//            float max = Float.NEGATIVE_INFINITY;
//            float min = Float.POSITIVE_INFINITY;
//            for (int j = 0; j < neighbors[i].length; j++) {
//                if (max < neighbors[i][j].value) {
//                    max = neighbors[i][j].value;
//                }
//
//                if (min > neighbors[i][j].value) {
//                    min = neighbors[i][j].value;
//                }
//            }
//            float sum = 0;
//            for (int j = 0; j < neighbors[i].length; j++) {
//                if (max > min) {
//                    float dist = (((neighbors[i][j].value - min) / (max - min)) * (0.9f)) + 0.1f;
//                    sum += (1 / dist);
//                }
//            }
//
//            for (int j = 0; j < neighbors[i].length; j++) {
//                if (max > min) {
//                    float dist = (((neighbors[i][j].value - min) / (max - min)) * (0.9f)) + 0.1f;
//                    A.setAsFloat((-((1 / dist) / sum)), i, neighbors[i][j].index);
//                } else {
//                    A.setAsFloat((-(1.0f / neighbors[i].length)), i, neighbors[i][j].index);
//                }
//            }
//        }
//
//        //Creating the Fij
//        for (int i = 0; i < pdata.getNumberControlPoints(); i++) {
//            A.setAsFloat(60.0f, (projection.length + i), this.controlPoints[i]);
//        }
//
//        DenseDoubleMatrix2D B = factory.zeros(nRows, 2);
//        for (int i = 0; i < this.projection_cp.length; i++) {
//            B.setAsFloat(this.projection_cp[i][0] * 60, (neighbors.length + i), 0);
//            B.setAsFloat(this.projection_cp[i][1] * 60, (neighbors.length + i), 1);
//        }
//
//        DenseDoubleMatrix2D At = (DenseDoubleMatrix2D) A.transpose();
//        DenseDoubleMatrix2D AtA = (DenseDoubleMatrix2D) At.mtimes(A);
//        DenseDoubleMatrix2D AtB = (DenseDoubleMatrix2D) At.mtimes(B);
//        DenseDoubleMatrix2D X = (DenseDoubleMatrix2D) AtA.chol().solve(AtB);
//
//        for (int i = 0; i < X.getRowCount(); i++) {
//            projection[i] = new float[2];
//            projection[i][0] =  X.getAsFloat(i, 0);
//            projection[i][1] =  X.getAsFloat(i, 1);
//        }
//        long finish = System.currentTimeMillis();
//
//        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Solving the system using UJMP time: {0}s", (finish - start) / 1000.0f);
//    }
//    private void projectUsingColt(ProjectionData pdata, Pair[][] neighbors, float[][] projection) {
//        long start = System.currentTimeMillis();
//
//        int nRows = neighbors.length + pdata.getNumberControlPoints();
//        int nColumns = neighbors.length;
//        SparseDoubleMatrix2D A = new SparseDoubleMatrix2D(nRows, nColumns);
//
//        for (int i = 0; i < neighbors.length; i++) {
//            //new approach to increase the neighborhood precision
//            A.setQuick(i, i, 1.0f);
//
////            for (int j = 0; j < neighbors[i].length; j++) {
////                A.setQuick(i, neighbors[i][j].index, (-(1.0f / neighbors[i].length)));
////            }
//
//            float max = Float.NEGATIVE_INFINITY;
//            float min = Float.POSITIVE_INFINITY;
//
//            for (int j = 0; j < neighbors[i].length; j++) {
//                if (max < neighbors[i][j].value) {
//                    max = neighbors[i][j].value;
//                }
//
//                if (min > neighbors[i][j].value) {
//                    min = neighbors[i][j].value;
//                }
//            }
//
//            float sum = 0;
//            for (int j = 0; j < neighbors[i].length; j++) {
//                if (max > min) {
//                    float dist = (((neighbors[i][j].value - min) / (max - min)) * (0.9f)) + 0.1f;
//                    sum += (1 / dist);
//                }
//            }
//
//            for (int j = 0; j < neighbors[i].length; j++) {
//                if (max > min) {
//                    float dist = (((neighbors[i][j].value - min) / (max - min)) * (0.9f)) + 0.1f;
//                    A.setQuick(i, neighbors[i][j].index, (-((1 / dist) / sum)));
//                } else {
//                    A.setQuick(i, neighbors[i][j].index, (-(1.0f / neighbors[i].length)));
//                }
//            }
//        }
//
//        //Creating the Fij
//        for (int i = 0; i < pdata.getNumberControlPoints(); i++) {
//            A.setQuick((projection.length + i), this.controlPoints[i], 60.0f);
//        }
//
//        SparseDoubleMatrix2D B = new SparseDoubleMatrix2D(nRows, 2);
//        for (int i = 0; i < this.projection_cp.length; i++) {
//            B.setQuick((neighbors.length + i), 0, this.projection_cp[i][0] * 60);
//            B.setQuick((neighbors.length + i), 1, this.projection_cp[i][1] * 60);
//        }
//
//        //Solving
//        DoubleMatrix2D AtA = A.zMult(A, null, 1.0, 1.0, true, false);
//        DoubleMatrix2D AtB = A.zMult(B, null, 1.0, 1.0, true, false);
//
//        start = System.currentTimeMillis();
//        CholeskyDecomposition chol = new CholeskyDecomposition(AtA);
//        DoubleMatrix2D X = chol.solve(AtB);
//
//        for (int i = 0; i < X.rows(); i++) {
//            projection[i] = new float[2];
//            projection[i][0] = (float) X.getQuick(i, 0);
//            projection[i][1] = (float) X.getQuick(i, 1);
//        }
//
//        long finish = System.currentTimeMillis();
//
//        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Solving the system using Colt time: {0}s", (finish - start) / 1000.0f);
//    }
    @Override
    public double[][] project(DistanceMatrix dmat, ProjectionData pdata, ProjectionViewWizard view) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ProjectionViewWizard getProjectionView(ProjectionData pdata) {
        return new LSPProjectionParametersView(pdata);
    }
}