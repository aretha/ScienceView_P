/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.dimensionreduction;

import java.io.IOException;

/**
 *
 * @author barbosaa
 */
public class DimensionalityReductionFactory {

    public static DimensionalityReduction getInstance(DimensionalityReductionType type, int target) throws IOException {
        DimensionalityReduction dr = null;
        if (type.equals(DimensionalityReductionType.KMEANS)) {
            dr = new KMeansReduction(target);
        } else if (type.equals(DimensionalityReductionType.PCA)) {
            dr = new PCA(target);
        } else if (type.equals(DimensionalityReductionType.FASTMAP)) {
            dr = new Fastmap(target);
        }
        return dr;
    }
}
