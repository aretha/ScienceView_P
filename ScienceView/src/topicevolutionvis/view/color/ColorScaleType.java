package topicevolutionvis.view.color;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class ColorScaleType implements Serializable {

    static {
        ColorScaleType.types = new ArrayList<>();
    }

    public static final ColorScaleType HEATEDOBJECTS = new ColorScaleType("Heated Objects Scalae");
    public static final ColorScaleType GRAYSCALE = new ColorScaleType("Gray Scale");
    public static final ColorScaleType LINEARGRAYSCALE = new ColorScaleType("Linear Gray Scale");
    public static final ColorScaleType LOCSSCALE = new ColorScaleType("Linearized Optimal Color Scale (LOCS)");
    public static final ColorScaleType RAINBOWCALE = new ColorScaleType("Rainbow Scale");
    public static final ColorScaleType PSEUDORAINBOWCALE = new ColorScaleType("Pseudo Rainbow Scale");
    
    /**
     * Creates a new instance of Encoding
     */
    private ColorScaleType(String name) {
        this.name = name;
        ColorScaleType.types.add(this);
    }

    public static ArrayList<ColorScaleType> getTypes() {
        return ColorScaleType.types;
    }

    public static ColorScaleType retrieve(String name) {
        for (ColorScaleType type : types) {
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

        final ColorScaleType other = (ColorScaleType) obj;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return 29 + (this.name != null ? this.name.hashCode() : 0);
    }

    public static final long serialVersionUID = 1L;
    private static ArrayList<ColorScaleType> types;
    private String name;
}
