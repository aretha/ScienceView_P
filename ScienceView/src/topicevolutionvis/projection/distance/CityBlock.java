package topicevolutionvis.projection.distance;

import topicevolutionvis.matrix.SparseVector;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class CityBlock implements Dissimilarity {

    @Override
    public double calculate(SparseVector v1, SparseVector v2) {
        assert (v1.size() == v2.size()) : "ERROR: vectors of different sizes!";
        assert (v1.getClass() == v2.getClass()) :
                "Error: only supported comparing vectors of the same type";


        if (v2.getIndex().length > v1.getIndex().length) {
            SparseVector tmp = (SparseVector) v1;
            v1 = v2;
            v2 = tmp;
        }

        int v1length = v1.getIndex().length;
        int v2length = v2.getIndex().length;
        int[] v1index = v1.getIndex();
        int[] v2index = v2.getIndex();
        double[] v1values = v1.getValues();
        double[] v2values = v2.getValues();

        int i = 0;
        int j = 0;
        double dist = 0.0f;

        while (i < v1length) {
            if (j < v2length) {
                if (v1index[i] == v2index[j]) {
                    dist += Math.abs(v1values[i] - v2values[j]);
                    i++;
                    j++;
                } else if (v1index[i] < v2index[j]) {
                    dist += Math.abs(v1values[i]);
                    i++;
                } else {
                    dist += Math.abs(v2values[j]);
                    j++;
                }
            } else {
                break;
            }
        }

        while (i < v1length) {
            dist += Math.abs(v1values[i]);
            i++;
        }

        while (j < v2length) {
            dist += Math.abs(v2values[j]);
            j++;
        }

        return dist;
    }
}
