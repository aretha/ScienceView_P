/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.dimensionreduction;

import java.io.IOException;
import java.util.ArrayList;
import topicevolutionvis.matrix.SparseMatrix;
import topicevolutionvis.matrix.SparseVector;
import topicevolutionvis.projection.temporal.TemporalProjection;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public abstract class DimensionalityReduction {

    public DimensionalityReduction(int targetDimension) {
        this.targetDimension = targetDimension;
    }

    public SparseMatrix reduce(SparseMatrix matrix, TemporalProjection projection) throws IOException {
        SparseMatrix redmatrix = new SparseMatrix();
       // Dissimilarity diss = DissimilarityFactory.getInstance(projection.getProjectionData().getDissimilarityType());
        double[][] red = this.execute(matrix, projection);

        //transforming the reduce form into a dense matrix
        for (int i = 0; i < matrix.getRowsCount(); i++) {
            SparseVector vector = matrix.getRowWithIndex(i);
            SparseVector dvector = new SparseVector(red[i], vector.getId(), vector.getKlass());
            redmatrix.addRow(dvector);
        }

        //setting the new attributes
        ArrayList<String> attr = new ArrayList<>(redmatrix.getDimensions());
        for (int i = 0; i < redmatrix.getDimensions(); i++) {
            attr.add("attr");
        }
        redmatrix.setAttributes(attr);

        return redmatrix;
    }

    public abstract double[][] execute(SparseMatrix matrix, TemporalProjection projection) throws IOException;
    protected int targetDimension;
}
