package topicevolutionvis.datamining.clustering;

import gnu.trove.list.array.TIntArrayList;
import java.io.IOException;
import java.util.ArrayList;
import topicevolutionvis.matrix.SparseMatrix;
import topicevolutionvis.projection.distance.Dissimilarity;


/**
 *
 * @author Fernando Vieira Paulovich
 */
public abstract class Clustering {

    public Clustering(int nrclusters) {
        this.nrclusters = nrclusters;
    }

    public abstract ArrayList<TIntArrayList> execute(Dissimilarity diss, SparseMatrix matrix) throws IOException;

    protected int nrclusters;
}
