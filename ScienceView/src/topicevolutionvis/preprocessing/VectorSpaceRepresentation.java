/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.preprocessing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/* @author Rodrigo begin */

import java.io.BufferedWriter;
import java.io.FileWriter;

/* @author Rodrigo end */

import topicevolutionvis.database.DatabaseCorpus;
import topicevolutionvis.matrix.SparseMatrix;
import topicevolutionvis.matrix.SparseVector;
import topicevolutionvis.projection.ProjectionData;

/**
 *
 * @author Aretha
 */
public class VectorSpaceRepresentation extends Representation {
    
    public VectorSpaceRepresentation(DatabaseCorpus corpus) {
        super(corpus);
    }

    @Override
    public SparseMatrix getMatrix(int[] ids, ProjectionData pdata) throws IOException {
        SparseMatrix matrix = new SparseMatrix();
        int ngramssize = this.ngrams.size();
        double[] row;
        HashMap<String, Integer> docNgrams;
        ArrayList<Reference> references = null;
        int n_citations = 0;
        
        /* @author Rodrigo begin */
        
        HashMap<String, Integer> mapWords = new HashMap();
        HashMap<Integer, Integer> mapDates = new HashMap();
        ArrayList< ArrayList<Integer> > occurs = new ArrayList();
        ArrayList<Integer> aux;
        Integer idx, occur;
        int count = 0;
        
        /* Change */
        ArrayList<String> words = new ArrayList();
        for (Ngram n : this.ngrams)
            words.add(n.ngram);
        
        /* @author Rodrigo end */
        
        if (include_references) {
            references = corpus.getCorpusReferences(pdata.getReferencesLowerCut(), pdata.getReferencesUpperCut());
            n_citations = references.size();
        }
        
        /* @author Rodrigo begin */ 
        
        // maps the years the collection
        int[] dates = corpus.getAscendingDates();
        for (int i = 0; i < dates.length; i++) {
            mapDates.put(dates[i], i);
        }
        
        /* @author Rodrigo end */
        
        for (int i = 0; i < ids.length; i++) {
            if (include_references) {
                row = new double[ngramssize + n_citations];
            } else {
                row = new double[ngramssize];
            }

            Arrays.fill(row, 0.0d);
            // get the ngrams of the file
            docNgrams = getNgrams(ids[i]);
            
            /* @author Rodrigo begin */
            
            int year = corpus.getYear(ids[i]);
            for (String key : docNgrams.keySet()) {
                
                /* Change */
                if (!words.contains(key))
                    continue;
                
                if (!mapWords.containsKey(key)) {
                    // mapping to create the word
                    mapWords.put(key, count);
                    // creates a list for occurrences of the word in years
                    ArrayList<Integer> counts = new ArrayList(dates.length);
                    // initializes the counters to zero
                    for (int j = 0; j < dates.length; j++) {
                        counts.add(j, 0);
                    }
                    // inserts on the list
                    occurs.add(count, counts);
                    idx = count++;
                } else {
                    // mapping takes the word
                    idx = mapWords.get(key);
                }
                // updates, increasing the value of occurrence
                aux = occurs.get(idx);
                occur = aux.get(mapDates.get(year));
                occur += docNgrams.get(key);
                aux.set(mapDates.get(year), occur);
                //aux.set(mapDates.get(year), ++occur);
                occurs.set(idx, aux);
            }
            
            /* @author Rodrigo end */
            
            Ngram n;
            for (int j = 0; j < ngramssize; j++) {
                n = this.ngrams.get(j);                
                if (docNgrams.containsKey(n.ngram)) {
                    row[j] = docNgrams.get(n.ngram);
                }
            }

            if ((include_references) && references != null && !references.isEmpty()) {
                int index = 0;
                for (Reference ref : references) {
                	// TODO: large databases get stuck in here
                    if (corpus.doesThisDocumentCitesThisReference(ids[i], ref.indexDatabase)) {
                        row[ngramssize + index] = 1.0f;
                    }
                    index++;
                }
            }

            SparseVector sv = new SparseVector(row, ids[i], 0);
            sv.setTitle(corpus.getTitle(ids[i]));
            matrix.addRow(sv);
        }
        
        /* @author Rodrigo begin */
        
        // writes the file
        BufferedWriter buff = new BufferedWriter(new FileWriter("occurs.txt"));
        
        // write the header
        buff.write(corpus.getCollectionName() + "\n");
        buff.write(mapWords.size() + "\n");
        int i;
        for (i = 0; i < dates.length - 1; i++)
            buff.write(dates[i] + " ");
        buff.write(dates[i] + "\n");
        
        for (String key : mapWords.keySet()) {
            buff.write(key + "| ");
            aux = occurs.get(mapWords.get(key));
            int j;
            for (j = 0; j < aux.size() - 1; j++) {
                buff.write(aux.get(j) + "| ");
            }
            buff.write(aux.get(j) + "\n");
        }
        buff.close();
        
        /* @author Rodrigo end */
        
        ArrayList<String> attr = new ArrayList<>();
        for (Ngram n : this.ngrams) {
            attr.add(n.ngram);
        }
        if (include_references) {
            for (Reference ref : references) {
                attr.add("ref-" + ref.indexDatabase);
            }
        }
        matrix.setAttributes(attr);


        return matrix;
    }
}
