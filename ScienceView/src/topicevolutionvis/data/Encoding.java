package topicevolutionvis.data;

import java.util.ArrayList;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public final class Encoding {

    static {
        Encoding.encodings = new ArrayList<>();
    }

    public static final Encoding ASCII = new Encoding("ISO-8859-1");
    public static final Encoding UTF16LE = new Encoding("UTF-16LE");

    /**
     * Creates a new instance of Encoding
     */
    private Encoding(String name) {
        this.name = name;
        Encoding.encodings.add(this);
    }

    public static ArrayList<Encoding> getEncodings() {
        return Encoding.encodings;
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

        final Encoding other = (Encoding) obj;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return 29 + (this.name != null ? this.name.hashCode() : 0);
    }

    private static ArrayList<Encoding> encodings;
    private String name;
}
