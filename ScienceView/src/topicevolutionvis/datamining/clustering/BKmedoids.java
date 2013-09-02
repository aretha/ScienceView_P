package topicevolutionvis.datamining.clustering;

import gnu.trove.list.array.TIntArrayList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import topicevolutionvis.matrix.SparseMatrix;
import topicevolutionvis.matrix.SparseVector;
import topicevolutionvis.matrix.SparseVectorUtils;
import topicevolutionvis.projection.distance.Dissimilarity;
import topicevolutionvis.projection.distance.DistanceMatrix;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class BKmedoids extends Clustering {

    /**
     * Creates a new instance of BKmedoids
     *
     * @param nrclusters
     */
    public BKmedoids(int nrclusters) {
        super(nrclusters);
    }

    public SparseMatrix getMedoidsMatrix(SparseMatrix matrix) {
        int[] m = this.getMedoids();
        SparseMatrix medoidsMatrix = new SparseMatrix();
        for (int i = 0; i < m.length; i++) {
            medoidsMatrix.addRow(matrix.getRowWithIndex(m[i]));
        }
        return medoidsMatrix;
    }

    @Override
    public ArrayList<TIntArrayList> execute(Dissimilarity diss, SparseMatrix matrix) throws IOException {
        DistanceMatrix dmat = new DistanceMatrix(matrix, diss);
        ArrayList<TIntArrayList> clusters = new ArrayList<>();
        clusters.add(new TIntArrayList());

        //Create a single cluster with all points
        for (int i = 0; i < dmat.getElementCount(); i++) {
            clusters.get(0).add(i);
        }

        //Choose a point as the initial centroid
        this.medoids = new TIntArrayList();
        this.medoids.add(0);

        int greatestCluster, greatestElements, numberIterations = 0;
        while (numberIterations < this.nrclusters * 5 && clusters.size() < this.nrclusters) {
            //Search the gratest cluster (number of points)
            greatestCluster = 0;
            greatestElements = clusters.get(0).size();

            for (int k = 1; k < clusters.size(); k++) {
                if (clusters.get(k).size() > greatestElements) {
                    greatestCluster = k;
                    greatestElements = clusters.get(k).size();
                }
            }

            //Split the greatest cluster
            this.splitCluster(greatestCluster, clusters, dmat, matrix, diss);

            //Eliminate empty clusters
            this.eliminateEmptyClusters(clusters);

//            //Search the smallest cluster (number of points)
//            int smallestCluster = 0;
//            int smallestElements = clusters.get(0).size();
//
//            for (int k = 1; k < clusters.size(); k++) {
//                if (clusters.get(k).size() < smallestElements) {
//                    smallestCluster = k;
//                    smallestElements = clusters.get(k).size();
//                }
//            }
//
//            //Join the smallest cluster
//            this.joinCluster(smallestCluster, clusters, dmat);

            numberIterations++;
        }

        return clusters;
    }

    private void eliminateEmptyClusters(ArrayList<TIntArrayList> clusters) {
        for (int i = 0; i < clusters.size(); i++) {
            if (clusters.get(i).isEmpty()) {
                clusters.remove(i);
                this.medoids.remove(i--);
            }
        }
    }

    private void splitCluster(int cluster, ArrayList<TIntArrayList> clusters, DistanceMatrix dmat, SparseMatrix matrix, Dissimilarity diss) throws IOException {
        //Copy the cluster which will be split
        TIntArrayList oldCluster = clusters.get(cluster);

        //Remove the cluster and its centroid
        clusters.remove(cluster);
        medoids.remove(cluster);

        //Choose two pivots
        int[] pivots = getPivots(matrix, diss, oldCluster);


        //Create two new clusters and its centroids as these pivots
        TIntArrayList cluster1 = new TIntArrayList();
        int medoids1 = pivots[0];

        TIntArrayList cluster2 = new TIntArrayList();
        int medoids2 = pivots[1];

        //For each cluster
        double distanceCentroid1, distanceCentroid2;
        for (int i = 0; i < oldCluster.size(); i++) {
            distanceCentroid1 = dmat.getDistance(oldCluster.get(i), medoids1);
            distanceCentroid2 = dmat.getDistance(oldCluster.get(i), medoids2);

            if (distanceCentroid1 < distanceCentroid2) {
                cluster1.add(oldCluster.get(i));
            } else if (distanceCentroid1 > distanceCentroid2) {
                cluster2.add(oldCluster.get(i));
            } else {
                if (cluster1.size() < cluster2.size()) {
                    cluster1.add(oldCluster.get(i));
                } else {
                    cluster2.add(oldCluster.get(i));
                }
            }
        }

        //Add the two new clusters
        clusters.add(cluster1);
        clusters.add(cluster2);

        //And its centroids
        this.medoids.add(medoids1);
        this.medoids.add(medoids2);

        //Update the medoids
        this.updateMedoids(dmat, clusters);
    }

    private void updateMedoids(DistanceMatrix dmat, ArrayList<TIntArrayList> clusters) {
        //Para cada cluster
        int medoid;
        double sumDistances, sumDistances2;
        for (int cluster = 0; cluster < clusters.size(); cluster++) {
            medoid = clusters.get(cluster).get(0);
            sumDistances = dmat.getMaxDistance();

            //para cada ponto do cluster
            for (int point = 0; point < clusters.get(cluster).size(); point++) {
                sumDistances2 = 0.0f;

                //Encontrar a m�dia da dist�ncia desse ponto para todos os outros pontos
                for (int point2 = 0; point2 < clusters.get(cluster).size(); point2++) {
                    sumDistances2 += dmat.getDistance(clusters.get(cluster).get(point), clusters.get(cluster).get(point2));
                }
                sumDistances2 /= clusters.get(cluster).size();

                //Assinalar como med�ide o ponto que minimiza a dist�ncia m�dia
                if (sumDistances > sumDistances2) {
                    sumDistances = sumDistances2;
                    medoid = clusters.get(cluster).get(point);
                }
            }
            medoids.set(cluster, medoid);
        }
    }

    protected SparseVector calculateMean(SparseMatrix matrix, TIntArrayList cluster) throws IOException {

        ArrayList<SparseVector> vectors = new ArrayList<>();
        for (int i = 0; i < cluster.size(); i++) {
            vectors.add(matrix.getRowWithIndex(cluster.get(i)));
        }
        return SparseVectorUtils.mean(vectors);
    }

    private int[] getPivots(SparseMatrix matrix, Dissimilarity diss, TIntArrayList gCluster) throws IOException {
        ArrayList<Pivot> pivots_aux = new ArrayList<>();
        int[] pivots = new int[2];

        //choosing the first pivot
        SparseVector mean = this.calculateMean(matrix, gCluster);
        int aux;
        double distance, size = 1 + (gCluster.size() / 10);
        for (int i = 0; i < size; i++) {
            aux = gCluster.get((int) ((gCluster.size() / size) * i));
            distance = diss.calculate(mean, matrix.getRowWithIndex(aux));
            pivots_aux.add(new Pivot(distance, aux));
        }

        Collections.sort(pivots_aux);

        pivots[0] = pivots_aux.get((int) (pivots_aux.size() * 0.75f)).id;

        //choosing the second pivot
        pivots_aux.clear();


        for (int i = 0; i < size; i++) {
            aux = gCluster.get((int) ((gCluster.size() / size) * i));
            distance = diss.calculate(matrix.getRowWithIndex(pivots[0]), matrix.getRowWithIndex(aux));
            pivots_aux.add(new Pivot(distance, aux));
        }

        Collections.sort(pivots_aux);

        pivots[1] = pivots_aux.get((int) (pivots_aux.size() * 0.75f)).id;

        return pivots;
    }

    public int[] getMedoids() {
        int[] c = new int[this.medoids.size()];
        for (int i = 0; i < this.medoids.size(); i++) {
            c[i] = this.medoids.get(i);
        }
        return c;
    }
    private TIntArrayList medoids;
    protected static final double EPSILON = 0.00001f;

    public class Pivot implements Comparable<Pivot> {

        public Pivot(double distance, int id) {
            this.distance = distance;
            this.id = id;
        }

        @Override
        public int compareTo(Pivot o) {
            if (Math.abs(this.distance - o.distance) < EPSILON) {
                return 0;
            } else if ((this.distance - o.distance) > EPSILON) {
                return 1;
            } else {
                return -1;
            }
        }
        public double distance;
        public int id;
    }
}
