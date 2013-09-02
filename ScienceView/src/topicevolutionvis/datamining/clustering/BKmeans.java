package topicevolutionvis.datamining.clustering;

import gnu.trove.list.array.TIntArrayList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import topicevolutionvis.matrix.SparseMatrix;
import topicevolutionvis.matrix.SparseVector;
import topicevolutionvis.matrix.SparseVectorUtils;
import topicevolutionvis.projection.distance.Dissimilarity;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class BKmeans extends Clustering {

    /**
     * Creates a new instance of BKmeans
     *
     * @param nrclusters
     */
    public BKmeans(int nrclusters) {
        super(nrclusters);
    }

    @Override
    public ArrayList<TIntArrayList> execute(Dissimilarity diss, SparseMatrix matrix) throws IOException {

//            long start = System.currentTimeMillis();
        this.diss = diss;
        this.clusters = new ArrayList<>();
        this.centroids = new ArrayList<>();

        //initially the gCluster has all elements
        TIntArrayList gCluster = new TIntArrayList(matrix.getRowsCount());
        for (int i = 0; i < matrix.getRowsCount(); i++) {
            gCluster.add(i);
        }
        this.clusters.add(gCluster);

        //considering just one element as the centroid
        this.centroids.add(matrix.getRowWithIndex(0));

        for (int j = 0; j < this.nrclusters - 1; j++) {
            //Search the cluster with the bigger number of elements
            gCluster = this.getClusterToSplit(this.clusters);

            //split the greatest cluster into two clusters
            if (gCluster.size() > 1) {
                this.splitCluster(matrix, diss, gCluster);
            }
        }

        //removing possible empty clusters
        for (int i = this.clusters.size() - 1; i >= 0; i--) {
            if (this.clusters.get(i).size() <= 0) {
                this.clusters.remove(i);
            }
        }


        for (int i = this.clusters.size() - 1; i >= 0; i--) {
            if (this.clusters.get(i).isEmpty()) {
                this.clusters.remove(i);
                this.centroids.remove(i);
            }
        }

        return this.clusters;
    }

    public ArrayList<SparseVector> getCentroids() {
        return this.centroids;
    }

    public int[] getMedoids(SparseMatrix matrix) throws IOException {
        int[] m = new int[this.centroids.size()];

        for (int i = 0; i < m.length; i++) {
            int point = -1;
            double distance = Double.MAX_VALUE;

            for (int j = 0; j < this.clusters.get(i).size(); j++) {
                double distance2 = this.diss.calculate(this.centroids.get(i),
                        matrix.getRowWithIndex(this.clusters.get(i).get(j)));

                if (distance > distance2) {
                    point = this.clusters.get(i).get(j);
                    distance = distance2;
                }
            }

            m[i] = point;
        }

        return m;
    }

    protected TIntArrayList getClusterToSplit(ArrayList<TIntArrayList> clusters) {
        TIntArrayList gCluster = clusters.get(0);

        for (int i = 0; i < clusters.size(); i++) {
            if (clusters.get(i).size() > gCluster.size()) {
                gCluster = clusters.get(i);
            }
        }

        return gCluster;
    }

    protected void splitCluster(SparseMatrix matrix, Dissimilarity diss, TIntArrayList gCluster) throws IOException {
        this.centroids.remove(clusters.indexOf(gCluster));
        this.clusters.remove(gCluster);

        //getting the two pivots
        int[] pivots = this.getPivots(matrix, diss, gCluster);

        //Create two new clusters
        TIntArrayList cluster_1 = new TIntArrayList();
        cluster_1.add(pivots[0]);
        SparseVector centroid_1 = matrix.getRowWithIndex(pivots[0]);

        TIntArrayList cluster_2 = new TIntArrayList();
        cluster_2.add(pivots[1]);
        SparseVector centroid_2 = matrix.getRowWithIndex(pivots[1]);

        int iterations = 0;

        do {
            centroid_1 = this.calculateMean(matrix, cluster_1);
            centroid_2 = this.calculateMean(matrix, cluster_2);

            cluster_1.clear();
            cluster_2.clear();

            //For each cluster
            for (int i = 0; i < gCluster.size(); i++) {
                double distCentr_1 = diss.calculate(matrix.getRowWithIndex(gCluster.get(i)), centroid_1);
                double distCentr_2 = diss.calculate(matrix.getRowWithIndex(gCluster.get(i)), centroid_2);

                if (distCentr_1 < distCentr_2) {
                    cluster_1.add(gCluster.get(i));
                } else if (distCentr_2 < distCentr_1) {
                    cluster_2.add(gCluster.get(i));
                } else {
                    if (cluster_1.size() > cluster_2.size()) {
                        cluster_2.add(gCluster.get(i));
                    } else {
                        cluster_1.add(gCluster.get(i));
                    }
                }
            }

            if (cluster_1.size() < 1) {
                cluster_1.add(cluster_2.get(0));
                cluster_2.remove(cluster_2.get(0));
            } else if (cluster_2.size() < 1) {
                cluster_2.add(cluster_1.get(0));
                cluster_1.remove(cluster_1.get(0));
            }

        } while (++iterations < this.nrIterations);

        //Add the two new clusters
        this.clusters.add(cluster_1);
        this.clusters.add(cluster_2);

        //add the new centroids
        this.centroids.add(centroid_1);
        this.centroids.add(centroid_2);
    }

    protected int[] getPivots(SparseMatrix matrix, Dissimilarity diss, TIntArrayList gCluster) throws IOException {
        ArrayList<Pivot> pivots_aux = new ArrayList<>();
        int[] pivots = new int[2];

        //choosing the first pivot
        SparseVector mean = this.calculateMean(matrix, gCluster);

        double size = 1 + (gCluster.size() / 10);
        for (int i = 0; i < size; i++) {
            int aux = gCluster.get((int) ((gCluster.size() / size) * i));
            pivots_aux.add(new Pivot(diss.calculate(mean, matrix.getRowWithIndex(aux)), aux));
        }

        Collections.sort(pivots_aux);

        pivots[0] = pivots_aux.get((int) (pivots_aux.size() * 0.75f)).id;

        //choosing the second pivot
        pivots_aux.clear();

        for (int i = 0; i < size; i++) {
            int aux = gCluster.get((int) ((gCluster.size() / size) * i));
            pivots_aux.add(new Pivot(diss.calculate(matrix.getRowWithIndex(pivots[0]), matrix.getRowWithIndex(aux)), aux));
        }

        Collections.sort(pivots_aux);

        pivots[1] = pivots_aux.get((int) (pivots_aux.size() * 0.75f)).id;

        return pivots;
    }

    protected SparseVector calculateMean(SparseMatrix matrix, TIntArrayList cluster) throws IOException {
        ArrayList<SparseVector> vectors = new ArrayList<>();
        for (int i = 0; i < cluster.size(); i++) {
            vectors.add(matrix.getRowWithIndex(cluster.get(i)));
        }
        return SparseVectorUtils.mean(vectors);
    }

    public class Pivot implements Comparable<Pivot> {

        public Pivot(double distance, int id) {
            this.distance = distance;
            this.id = id;
        }

        @Override
        public int compareTo(Pivot o) {
            if (Math.abs(this.distance - o.distance) < EPSILON) {
                return 0;
            } else if (this.distance - o.distance > EPSILON) {
                return 1;
            } else {
                return -1;
            }
        }
        public double distance;
        public int id;
    }
    protected ArrayList<TIntArrayList> clusters;
    protected ArrayList<SparseVector> centroids;
    protected Dissimilarity diss;
    protected static final double EPSILON = 0.00001d;
    protected int nrIterations = 15;
}
