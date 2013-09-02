package topicevolutionvis.projection.distance.kolmogorov;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class CompressorType implements Serializable {

    static {
        CompressorType.types = new ArrayList<>();
    }

    public static final CompressorType BZIP2 = new CompressorType("Bzip2");
    public static final CompressorType GZIP = new CompressorType("Gzip");
    
    /** 
     * Creates a new instance of Encoding 
     */
    private CompressorType(String name) {
        this.name = name;
        CompressorType.types.add(this);
    }

    public static ArrayList<CompressorType> getTypes() {
        return CompressorType.types;
    }
    
    public static CompressorType retrieve(String name) {
        for (CompressorType type : types) {
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

        final CompressorType other = (CompressorType) obj;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return 29 + (this.name != null ? this.name.hashCode() : 0);
    }

    public static final long serialVersionUID = 1L;
    private static ArrayList<CompressorType> types;
    private String name;
}
