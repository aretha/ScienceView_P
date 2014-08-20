package topicevolutionvis.dimensionreduction;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class DimensionalityReductionType implements Serializable {

    static {
        DimensionalityReductionType.types = new ArrayList<>();
    }
    public static final DimensionalityReductionType NONE = new DimensionalityReductionType("None");
    public static final DimensionalityReductionType PCA = new DimensionalityReductionType("PCA");
    public static final DimensionalityReductionType FASTMAP = new DimensionalityReductionType("Fastmap");
    public static final DimensionalityReductionType KMEANS = new DimensionalityReductionType("K-means");

    /** 
     * Creates a new instance of Encoding 
     */
    private DimensionalityReductionType(String name) {
        this.name = name;
        DimensionalityReductionType.types.add(this);
    }

    public static ArrayList<DimensionalityReductionType> getTypes() {
        return DimensionalityReductionType.types;
    }

    public static DimensionalityReductionType retrieve(String name) {
        for (DimensionalityReductionType type : types) {
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

        final DimensionalityReductionType other = (DimensionalityReductionType) obj;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return 29 + (this.name != null ? this.name.hashCode() : 0);
    }
    public static final long serialVersionUID = 1L;
    private static ArrayList<DimensionalityReductionType> types;
    private final String name;
}
