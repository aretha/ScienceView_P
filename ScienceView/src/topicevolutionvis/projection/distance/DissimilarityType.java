package topicevolutionvis.projection.distance;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class DissimilarityType implements Serializable {

   static {
        DissimilarityType.types = new ArrayList<>();
    }

    public static final DissimilarityType EUCLIDEAN = new DissimilarityType("Euclidean");
    public static final DissimilarityType COSINE_BASED = new DissimilarityType("Cosine-based");
    public static final DissimilarityType CITY_BLOCK = new DissimilarityType("City block");
    public static final DissimilarityType KOLMOGOROV = new DissimilarityType("Kolmogorov");
    public static final DissimilarityType EXTENDED_JACCARD = new DissimilarityType("Extended Jaccard");
    public static final DissimilarityType INFINITY_NORM = new DissimilarityType("Infinity Norm");
    public static final DissimilarityType KULLBACK_DIVERGENCE = new DissimilarityType("Kullback Divergence");
    public static final DissimilarityType JENSEN_SHANON = new DissimilarityType("Jensen-Shanon");
    public static final DissimilarityType HELLINGER = new DissimilarityType("Hellinger");
    
    /** 
     * Creates a new instance of Encoding 
     */
    private DissimilarityType(String name) {
        this.name = name;
        DissimilarityType.types.add(this);
    }

    public static ArrayList<DissimilarityType> getTypes() {
        return DissimilarityType.types;
    }

    public static DissimilarityType retrieve(String name) {
        for (DissimilarityType type : types) {
            if (type.name.equals(name)) {
                return type;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final DissimilarityType other = (DissimilarityType) obj;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return 29 + (this.name != null ? this.name.hashCode() : 0);
    }

    public static final long serialVersionUID = 1L;
    private static ArrayList<DissimilarityType> types;
    private final String name;
}
