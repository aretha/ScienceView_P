/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.preprocessing;

import java.io.IOException;
import topicevolutionvis.database.DatabaseCorpus;

/**
 *
 * @author USER
 */
public class RepresentationFactory {

    public static Representation getInstance(RepresentationType type, DatabaseCorpus corpus) throws IOException {
        Representation representation = null;
        if (type.equals(RepresentationType.VECTOR_SPACE_MODEL) || type.equals(RepresentationType.VECTOR_SPACE_REFERENCES)) {
            representation = new VectorSpaceRepresentation(corpus);
        } else if (type.equals(RepresentationType.LDA)) {
            representation = new LDARepresentation(corpus);
        }
        return representation;
    }
}
