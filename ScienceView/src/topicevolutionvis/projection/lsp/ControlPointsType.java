package topicevolutionvis.projection.lsp;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class ControlPointsType implements Serializable {

    static {
        ControlPointsType.types = new ArrayList<>();
    }
    public static final ControlPointsType RANDOM = new ControlPointsType("Random");
    public static final ControlPointsType KMEDOIDS = new ControlPointsType("K-medoids");
    public static final ControlPointsType KMEANS = new ControlPointsType("K-means");

    /** 
     * Creates a new instance of Encoding 
     */
    private ControlPointsType(String name) {
        this.name = name;
        ControlPointsType.types.add(this);
    }

    public static ArrayList<ControlPointsType> getTypes() {
        return ControlPointsType.types;
    }

    public static ControlPointsType retrieve(String name) {
        for (ControlPointsType type : types) {
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

        final ControlPointsType other = (ControlPointsType) obj;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return 29 + (this.name != null ? this.name.hashCode() : 0);
    }
    public static final long serialVersionUID = 1L;
    private static ArrayList<ControlPointsType> types;
    private final String name;
}
