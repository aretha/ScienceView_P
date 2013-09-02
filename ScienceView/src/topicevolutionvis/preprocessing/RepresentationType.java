/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.preprocessing;

import java.util.ArrayList;

/**
 *
 * @author USER
 */
public class RepresentationType {

    static {
        RepresentationType.types = new ArrayList<>();
    }
    public static final RepresentationType VECTOR_SPACE_MODEL = new RepresentationType("Vector Space Model");
    public static final RepresentationType VECTOR_SPACE_REFERENCES = new RepresentationType("Vector Space Model Extended");
    public static final RepresentationType LDA = new RepresentationType("Latent Dirichlet Allocation (LDA)");

    private RepresentationType(String name) {
        this.name = name;
        RepresentationType.types.add(this);
    }

    public static ArrayList<RepresentationType> getTypes() {
        return RepresentationType.types;
    }

    public static RepresentationType retrieve(String name) {
        for (RepresentationType type : types) {
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

        final RepresentationType other = (RepresentationType) obj;
        return this.name.equals(other.name);
    }
    private static ArrayList<RepresentationType> types;
    private String name;
}
