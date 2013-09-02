/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.view;

/**
 *
 * @author Aretha
 */
public class DocumentInfo {

    public String documentName;
    public int id;

    public DocumentInfo(String documentName, int id) {
        this.documentName = documentName;
        this.id = id;
    }

    @Override
    public String toString() {
        return this.documentName;
    }
}
