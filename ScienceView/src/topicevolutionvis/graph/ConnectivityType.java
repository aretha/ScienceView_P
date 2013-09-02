/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.graph;

import java.util.ArrayList;

/**
 *
 * @author Aretha
 */
public class ConnectivityType {

    static {
        ConnectivityType.types = new ArrayList<>();
    }
    public static final ConnectivityType NONE = new ConnectivityType("...");
    public static final ConnectivityType BIBLIOGRAPHIC_COUPLING = new ConnectivityType("Bibliographic Coupling");
    public static final ConnectivityType CORE_CITATIONS = new ConnectivityType("Core citations");
    public static final ConnectivityType CO_AUTHORSHIP = new ConnectivityType("Co-authorship");
    //public static final ConnectivityType SIMILARITY = new ConnectivityType("Similarity");

    private ConnectivityType(String name) {
        this.name = name;
        ConnectivityType.types.add(this);
    }

    public static ArrayList<ConnectivityType> getTypes() {
        return ConnectivityType.types;
    }

    public static ConnectivityType retrieve(String name) {
        for (ConnectivityType type : types) {
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

        final ConnectivityType other = (ConnectivityType) obj;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return 29 + (this.name != null ? this.name.hashCode() : 0);
    }
    public static final long serialVersionUID = 1L;
    private static ArrayList<ConnectivityType> types;
    private String name;
}
