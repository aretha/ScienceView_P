package topicevolutionvis.dimensionreduction;

import java.io.IOException;
import java.util.Arrays;
import topicevolutionvis.matrix.SparseMatrix;
import topicevolutionvis.matrix.SparseVector;
import topicevolutionvis.projection.distance.Dissimilarity;
import topicevolutionvis.projection.distance.DissimilarityFactory;
import topicevolutionvis.projection.temporal.TemporalProjection;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class Fastmap extends DimensionalityReduction {

    public Fastmap(int targetDimension) {
        super(targetDimension);
    }

    @Override
    public double[][] execute(SparseMatrix matrix, TemporalProjection tproj) throws IOException {
        //creating the projection
        int n_rows = matrix.getRowsCount();
        double[][] projection = new double[n_rows][];
        for (int i = 0; i < n_rows; i++) {
            projection[i] = new double[this.targetDimension];
            Arrays.fill(projection[i], 0.0f);
        }
        double pDistance, lvxi;
        int[] pivots;
        Dissimilarity diss = DissimilarityFactory.getInstance(tproj.getProjectionData().getDissimilarityType());
        for (int curDim = 0; curDim < this.targetDimension; curDim++) {
            //choosen pivots for this recursion
            pivots = this.chooseDistantObjects(matrix, projection, curDim, diss);
            pDistance = this.distance(matrix.getRowWithIndex(pivots[0]), matrix.getRowWithIndex(pivots[1]),
                    projection[pivots[0]], projection[pivots[1]], curDim, diss);

            //if the distance between the pivots is 0, then set 0 for each instance for this dimension
            if (pDistance == 0.0f) {
                //for each instance in the table
                for (int i = 0; i < n_rows; i++) {
                    projection[i][curDim] = 0.0f;
                }
            } else {
                for (int i = 0; i < n_rows; i++) {
                    //current dimension xi = (distance between the instance and the first pivot)^2 +
                    //                       (distance between both pivots)^2 -
                    //                       (distance between the instance and the secod pivot)^2)
                    //                        all divided by 2 times the (distance between both pivots)

                    lvxi = ((Math.pow(this.distance(matrix.getRowWithIndex(pivots[0]), matrix.getRowWithIndex(i),
                            projection[pivots[0]], projection[i], curDim, diss), 2)
                            + Math.pow(this.distance(matrix.getRowWithIndex(pivots[0]), matrix.getRowWithIndex(pivots[1]),
                            projection[pivots[0]], projection[pivots[1]], curDim, diss), 2)
                            - Math.pow(this.distance(matrix.getRowWithIndex(pivots[1]), matrix.getRowWithIndex(i),
                            projection[pivots[1]], projection[i], curDim, diss), 2))
                            / (2 * this.distance(matrix.getRowWithIndex(pivots[0]), matrix.getRowWithIndex(pivots[1]),
                            projection[pivots[0]], projection[pivots[1]], curDim, diss)));

                    projection[i][curDim] = lvxi;
                }
            }
        }

        return projection;
    }

    private double distance(SparseVector vectA, SparseVector vectB, double[] projA,
            double[] projB, int dimension, Dissimilarity diss) throws IOException {
        //original distance
        double dist = diss.calculate(vectA, vectB);

        //transforming the distance if necessary
        for (int i = 0; i < dimension; i++) {
            dist = Math.sqrt(Math.abs(Math.pow(dist, 2) - Math.pow((projA[i] - projB[i]), 2)));
        }

        return dist;
    }

    private int[] chooseDistantObjects(SparseMatrix matrix, double[][] projection,
            int dimension, Dissimilarity diss) throws IOException {

        int[] choosen = new int[2];

        //chossing the first object randomly
        int x = (int) (Math.random() * (matrix.getRowsCount() - 1));
        double aux, maxdist = Double.MIN_VALUE;

        //for each instance
        for (int i = 0; i < matrix.getRowsCount(); i++) {
            aux = this.distance(matrix.getRowWithIndex(x), matrix.getRowWithIndex(i), projection[x], projection[i], dimension, diss);
            if (aux > maxdist) {
                maxdist = aux;
                x = i;
            }
        }

        int y = 0;
        maxdist = Double.MIN_VALUE;

        for (int i = 0; i < matrix.getRowsCount(); i++) {
            aux = this.distance(matrix.getRowWithIndex(x), matrix.getRowWithIndex(i),
                    projection[x], projection[i], dimension, diss);

            if (aux > maxdist) {
                maxdist = aux;
                y = i;
            }
        }

        choosen[0] = x;
        choosen[1] = y;

        return choosen;
    }
}
