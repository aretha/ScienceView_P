package topicevolutionvis.projection.distance;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class DissimilarityFactory {

    public static Dissimilarity getInstance(DissimilarityType type) {

        if (type.equals(DissimilarityType.COSINE_BASED)) {
            return new CosineBased();
        } else if (type.equals(DissimilarityType.CITY_BLOCK)) {
            return new CityBlock();
        } else if (type.equals(DissimilarityType.EUCLIDEAN)) {
            return new Euclidean();
        } else if (type.equals(DissimilarityType.EXTENDED_JACCARD)) {
            return new ExtendedJaccard();
        } else if (type.equals(DissimilarityType.INFINITY_NORM)) {
            return new InfinityNorm();
        } else if (type.equals(DissimilarityType.KULLBACK_DIVERGENCE)) {
            return new KullbackLeiblerDivergence();
        } else if (type.equals(DissimilarityType.JENSEN_SHANON)) {
            return new JensenShanon();
        } else if (type.equals(DissimilarityType.HELLINGER)) {
            return new Hellinger();
        }

        return null;
    }
}
