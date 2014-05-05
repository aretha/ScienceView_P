/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.preprocessing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
        if (include_references) {
            references = corpus.getCorpusReferences(pdata.getReferencesLowerCut(), pdata.getReferencesUpperCut());
            n_citations = references.size();
        }

        for (int i = 0; i < ids.length; i++) {
            if (include_references) {
                row = new double[ngramssize + n_citations];
            } else {
                row = new double[ngramssize];
            }

            Arrays.fill(row, 0.0d);
            //get the ngrams of the file
            docNgrams = getNgrams(ids[i]);
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
