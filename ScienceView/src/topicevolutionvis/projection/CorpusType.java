/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.projection;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Aretha
 */
public class CorpusType implements Serializable {

    private String name;
    private static ArrayList<CorpusType> types;

    static {
        CorpusType.types = new ArrayList<>();
    }
    public static final CorpusType BIBTEX = new CorpusType("BibTeX");
    public static final CorpusType ISI = new CorpusType("ISI");
    public static final CorpusType ENDNOTE = new CorpusType("Endnote Export Format");
    public static final CorpusType NONE = new CorpusType("None");

    private CorpusType(String name) {
        this.name = name;
        CorpusType.types.add(this);
    }

    @Override
    public String toString() {
        return this.name;
    }

     public static ArrayList<CorpusType> getTypes() {
        return CorpusType.types;
    }
}
