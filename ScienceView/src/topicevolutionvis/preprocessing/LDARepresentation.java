/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.preprocessing;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.regex.Pattern;
import topicevolutionvis.database.DatabaseCorpus;
import topicevolutionvis.dimensionreduction.lda.LDAOutput;
import topicevolutionvis.matrix.SparseMatrix;
import topicevolutionvis.matrix.SparseVector;
import topicevolutionvis.preprocessing.transformation.MatrixTransformationType;
import topicevolutionvis.projection.ProjectionData;

/**
 *
 * @author USER
 */
public class LDARepresentation extends Representation {

    private int number_of_topics = 200;
    private double alpha = 0.25d;
    private double beta = 0.01d;
    private ParallelTopicModel lda = null;

    public LDARepresentation(DatabaseCorpus corpus) {
        super(corpus);
    }

    @Override
    public SparseMatrix getMatrix(int[] ids, ProjectionData pdata) throws IOException {
        long start = System.currentTimeMillis();
        Pipe pipe = buildPipe();
        StringBuilder content;
        String token;
        InstanceList instances = new InstanceList(pipe);
        SparseVector row;
        Representation representation = RepresentationFactory.getInstance(RepresentationType.VECTOR_SPACE_MODEL, corpus);
        SparseMatrix vectorMatrix = representation.getMatrix(pdata.getLunhLowerCut(), pdata.getLunhUpperCut(), 1, pdata.getStemmer(), MatrixTransformationType.NONE, true, false, false);
        ArrayList<String> vocabulary = vectorMatrix.getAttributes();
        if (pdata != null) {
            this.number_of_topics = pdata.getNumberOfTopics();
            this.alpha = pdata.getAlpha();
            this.beta = pdata.getBeta();
        }
        for (int i = 0; i < vectorMatrix.getRowsCount(); i++) {
            content = new StringBuilder(50);
            row = vectorMatrix.getRowWithIndex(i);
            for (int j = 0; j < row.getIndex().length; j++) {
                int index = row.getIndex()[j];
                token = vocabulary.get(index);
                for (int r = 0; r < row.getValue(index); r++) {
                    content.append(token).append(" ");
                }
            }
            instances.addThruPipe(new Instance(content.toString(), null, row.getId(), null));
        }


        lda = new ParallelTopicModel(this.number_of_topics, number_of_topics * alpha, beta);
        lda.setNumIterations(pdata.getNumberOfLDAIterations());
        lda.setNumThreads(4);
        lda.addInstances(instances);
        lda.setOptimizeInterval(10);

        lda.setBurninPeriod(20);
        lda.estimate();

        lda.printTopWords(new File("lda/TopicsTopWords.txt"), 200, false);
        lda.printDocumentTopics(new File("lda/DocumentsTopics.txt"));

        long end = System.currentTimeMillis();
        System.err.println("Model Log Likelihood: " + lda.modelLogLikelihood());
        pdata.setLdaModelLogLikelihood(lda.modelLogLikelihood());
        SparseMatrix matrix = this.getMatrices(lda, pdata, ids);
        if (pdata != null) {
            pdata.setLDAMatrices(new LDAOutput(lda.getAlphabet(), matrix, lda.getSortedWords()));
        }
        System.err.println("Time to generate LDA (seg): " + (end - start) / 1000);
        return matrix;
    }

    public SparseMatrix getMatrices(ParallelTopicModel lda, ProjectionData pdata, int[] ids) throws IOException {
        double[] alphas = lda.alpha;
        ArrayList<TopicCandidate> selected_topics = new ArrayList<>(alphas.length);
        ArrayList<String> attributes = new ArrayList<>();
        for (int i = 0; i < alphas.length; i++) {
            if (alphas[i] >= 0.012) {
                selected_topics.add(new TopicCandidate(i, alphas[i]));
                attributes.add(Integer.toString(i));
            }

        }
        SparseMatrix matrix = new SparseMatrix(lda.getData().size());
        matrix.setDimensions(attributes.size());
        matrix.setAttributes(attributes);

        double[] probs;
        double[] selected_probs = new double[selected_topics.size()];
        for (int i = 0; i < lda.getData().size(); i++) {
            probs = lda.getTopicProbabilities(i);
            for (int j = 0; j < selected_topics.size(); j++) {
                selected_probs[j] = probs[selected_topics.get(j).id];
            }
            matrix.addRow(selected_probs, ids[i]);
        }
        return matrix;
    }

    public ArrayList<TreeSet<IDSorter>> getSortedWords() {
        if (lda != null) {
            return lda.getSortedWords();
        }
        return null;
    }

    public Alphabet getAlphabet() {
        if (lda != null) {
            return lda.getAlphabet();
        }
        return null;
    }

    private Pipe buildPipe() {
        ArrayList<Pipe> pipeList = new ArrayList<>();

        pipeList.add(new CharSequenceLowercase());

        //Tokenize raw strings
        pipeList.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));

        pipeList.add(new TokenSequenceRemoveStopwords(new File("libs/mallet-en.txt"), "UTF-8", false, false, false));

        // Rather than storing tokens as strings, convert
        //  them to integers by looking them up in an alphabet.
        pipeList.add(new TokenSequence2FeatureSequence());

        return new SerialPipes(pipeList);
    }

    public class TopicCandidate {

        public int id;
        public double value;

        public TopicCandidate(int id, double value) {
            this.id = id;
            this.value = value;
        }
    }
}
