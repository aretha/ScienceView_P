/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.dimensionreduction.lda;

import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import topicevolutionvis.matrix.SparseMatrix;

/**
 *
 * @author USER
 */
public class LDAOutput {

    private Alphabet alphabet;
    private ArrayList<TreeSet<IDSorter>> topic_words_probabilites = null;
    private SparseMatrix topic_documents_probabilites;
    private double[] sums_words;

    public LDAOutput(Alphabet alphabet, SparseMatrix topic_documents_probabilites, ArrayList<TreeSet<IDSorter>> topic_words_probabilites) {
        this.alphabet = alphabet;
        this.topic_documents_probabilites = topic_documents_probabilites;
        this.topic_words_probabilites = new ArrayList<>(topic_documents_probabilites.getDimensions());
        ArrayList<TreeSet<IDSorter>> aux = topic_words_probabilites;
        for (int i = 0; i < topic_documents_probabilites.getDimensions(); i++) {
            this.topic_words_probabilites.add(aux.get(Integer.valueOf(topic_documents_probabilites.getAttributeWithIndex(i))));
        }

        Iterator<IDSorter> it;
        //getting the sum to normalize probabilites
        this.sums_words = new double[topic_documents_probabilites.getDimensions()];
        int index = 0;
        for (int i = 0; i < sums_words.length; i++) {
            sums_words[index] = 0;
            it = this.topic_words_probabilites.get(i).iterator();
            while (it.hasNext()) {
                sums_words[index] += it.next().getWeight();
            }
            index++;
        }
    }

    public TreeSet<IDSorter> getWordsProbabilitesForTopic(int index_topic) {
        return this.topic_words_probabilites.get(index_topic);
    }

    public double getSumWordsForTopic(int index_topic) {
        return this.sums_words[index_topic];
    }

    public String getWordFromAlphabet(int id) {
        return (String) alphabet.lookupObject(id);
    }

    public int getNumTopics() {
        return topic_words_probabilites.size();
    }

    public Alphabet getAlphabet() {
        return this.alphabet;
    }

    public double getProbabilityDocumentVsTopics(int document_index, int topic_int) {
        return this.topic_documents_probabilites.getValueWithId(document_index, topic_int);
    }

    public SparseMatrix getTopicDocumentsProbabilites() {
        return this.topic_documents_probabilites;
    }
}
