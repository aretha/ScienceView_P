/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.datamining.clustering.monic.transitions;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.THashMap;
import topicevolutionvis.topic.Topic;

/**
 *
 * @author USER
 */
public class ContentChangeTransition extends InternalTransition {

    public static final int ONLY_ADDED_DOCUMENTS = 0;
    public static final int ONLY_REMOVED_DOCUMENTS = 1;
    public static final int ADDED_AND_REMOVED_DOCUMENTES = 2;
    

    public ContentChangeTransition(THashMap<Topic, TIntArrayList> added_documents, THashMap<Topic, TIntArrayList> removed_documents) {
        super(InternalTransition.CONTENT_CHANGE_TRANSITION);
        if (added_documents != null && removed_documents == null) {
            this.subtype = ONLY_ADDED_DOCUMENTS;
        } else if (added_documents == null && removed_documents != null) {
            this.subtype = ONLY_REMOVED_DOCUMENTS;
        } else {
            this.subtype = ADDED_AND_REMOVED_DOCUMENTES;
        }
        this.added_documents = added_documents;
        this.removed_documents = removed_documents;
    }

    public int getSubtype() {
        return this.subtype;
    }

    public THashMap<Topic, TIntArrayList> getIdsDocumentsAdded() {
        return this.added_documents;
    }

    public THashMap<Topic, TIntArrayList> getIdsDocumentsRemoved() {
        return this.removed_documents;
    }

    public int numberOfDocumentsAdded() {
        if (added_documents != null) {
            return this.added_documents.size();
        }
        return 0;
    }

    public int numberOfDocumentsRemoved() {
        if (removed_documents != null) {
            return this.removed_documents.size();
        }

        return 0;

    }
    private int subtype;
    private THashMap<Topic, TIntArrayList> added_documents = null;
    private THashMap<Topic, TIntArrayList> removed_documents = null;
}
