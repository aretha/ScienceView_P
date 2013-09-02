/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.topic;

import cern.colt.matrix.DoubleMatrix1D;
import com.vividsolutions.jts.geom.Coordinate;
import gnu.trove.list.array.TIntArrayList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import topicevolutionvis.graph.TemporalGraph;
import topicevolutionvis.matrix.SparseMatrix;
import topicevolutionvis.matrix.SparseVector;
import topicevolutionvis.preprocessing.Ngram;
import topicevolutionvis.preprocessing.VectorSpaceRepresentation;
import topicevolutionvis.preprocessing.steemer.StemmerType;
import topicevolutionvis.projection.temporal.TemporalProjection;

/**
 *
 * @author Aretha
 */
public class PCATopic extends Topic implements Cloneable {

    private TopicData tdata;

    @Override
    public Object clone() throws CloneNotSupportedException {
        Topic newTopic = new PCATopic(new TIntArrayList(), this.tprojection, this.graph);
        for (int i = 0; i < this.vertex_id.size(); i++) {
            newTopic.vertex_id.add(this.vertex_id.get(i));
        }
        for (Coordinate c : this.fake_vertex) {
            newTopic.fake_vertex.add(new Coordinate(c));
        }
        newTopic.setId(this.getId());
        newTopic.setAlphaPolygon(this.getAlphaPolygon());
        newTopic.setDrawPolygonOption(this.getDrawPolygonOption());
        newTopic.setSelected(this.selected);
        newTopic.graph = this.graph;
        this.cloneInfo(newTopic);
        newTopic.setNextAddedVertex(this.getNextAddedVertex());
        return newTopic;
    }

    public PCATopic(TIntArrayList vertex, TemporalProjection tprojection, TemporalGraph graph) {
        super(vertex, tprojection, graph);
        this.tprojection = tprojection;
        this.tdata = tprojection.getTopicData();
        this.id = this.tdata.getNextAvailableId();
    }

    public PCATopic(TIntArrayList vertex, ArrayList<Coordinate> fake_vertex, TemporalProjection tprojection, TemporalGraph graph) {
        super(vertex, fake_vertex, tprojection, graph);
        this.tprojection = tprojection;
        this.tdata = tprojection.getTopicData();
        this.id = this.tdata.getNextAvailableId();
    }

    private SparseMatrix cutDimensions(SparseMatrix matrix, ArrayList<Ngram> cpNgrams, ArrayList<String> indexGrams) throws CloneNotSupportedException {
        int length = 100;
        SparseMatrix clone = (SparseMatrix) matrix.clone();
        double[][] newpoints = new double[matrix.getRowsCount()][];
        SparseVector vector, v;
        for (int i = 0; i < newpoints.length; i++) {
            if (matrix.getDimensions() < length) {
                newpoints[i] = new double[matrix.getDimensions()];
            } else {
                newpoints[i] = new double[length];
                clone.setDimensions(length);
            }
            newpoints[i] = new double[(matrix.getDimensions() < length) ? matrix.getDimensions() : length];
            vector = matrix.getRowWithIndex(i);
            double[] point = matrix.getRowWithIndex(i).toDenseVector();
            for (int j = 0; j < newpoints[i].length; j++) {
                newpoints[i][j] = point[j];
                v = new SparseVector(newpoints[i], vector.getId(), vector.getKlass());
                clone.setRow(i, v);
            }
        }

        indexGrams.clear();
        for (int i = 0; i < newpoints[0].length; i++) {
            indexGrams.add(cpNgrams.get(i).ngram);
        }
        return clone;
    }

    @Override
    public void createTopic() {
        try {
            int lowercut = 2;
            int ngrams = 1;
            if (vertex_id.size() > 50 && vertex_id.size() < 100) {
                lowercut = 10;
            } else if (vertex_id.size() > 100 && vertex_id.size() < 300) {
                lowercut = 15;
            } else if (vertex_id.size() > 300) {
                lowercut = 20;
            }
            VectorSpaceRepresentation pp = new VectorSpaceRepresentation(corpus);
            SparseMatrix matrix = pp.getMatrixSelected(lowercut, -1, ngrams, StemmerType.Type.NONE, true, true, this.getVertexList());
            ArrayList<Ngram> cpNgrams = pp.getNgrams();
            if (vertex_id.size() > 1) {
                ArrayList<String> attributes = new ArrayList<>();
                SparseMatrix matrix_bow = this.cutDimensions(matrix, cpNgrams, attributes);
                double[][] points = matrix_bow.toDenseMatrix();
                PCAFlanagan pca = new PCAFlanagan();
                pca.enterScoresAsRowPerPerson(points);
                pca.pca();

                //decididno quais fatores extrair
                double[] eigenvalues = pca.proportionPercentage();
                double[][] loading_factors = pca.loadingFactorsAsRows();
                double min_topics = tdata.getPcaInformationTopics();
                double min_terms = tdata.getPcaMinInformationTerms();


                ArrayList<TermCandidate> candidates = new ArrayList<>();
                int index_term;
                int n_topic = 0;
                //demais tópicos
                double sum_topics = 0.0d;
                while (sum_topics / 100 < min_topics) {

                    index_term = 0;
                    candidates.clear();
                    for (int j = 0; j < loading_factors[n_topic].length; j++) {
                        if (loading_factors[n_topic][j] > min_terms) {
                            candidates.add(new TermCandidate(attributes.get(index_term), loading_factors[n_topic][j]));
                        }
                        index_term++;
                    }
                    if (candidates.size() > 2) {

                        Collections.sort(candidates, new Comparator<TermCandidate>() {
                            @Override
                            public int compare(TermCandidate o2, TermCandidate o1) {
                                if (o1.value < o2.value) {
                                    return -1;
                                } else if (o1.value > o2.value) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            }
                        });
                        this.newTopic(eigenvalues[n_topic]);
                        for (TermCandidate candidate : candidates) {
                            this.addTopicTag(candidate.id, candidate.value);
                        }


                        sum_topics += eigenvalues[n_topic];
                        n_topic++;
                    } else {
                        min_terms = min_terms * 0.75f;
                    }
                }
            }
        } catch (IOException | CloneNotSupportedException ex) {
            Logger.getLogger(PCATopic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    public void createTopic2() {
//        try {
//            int lowercut = 2;
//            int uppercut = -1;
//            int ngrams = 1;
//            if (vertex.size() > 50 && vertex.size() < 100) {
//                lowercut = 10;
//            } else if (vertex.size() > 100 && vertex.size() < 300) {
//                lowercut = 15;
//            } else if (vertex.size() > 300) {
//                lowercut = 20;
//            }
//            VectorSpaceRepresentation pp = new VectorSpaceRepresentation(corpus);
//            SparseMatrix matrix = pp.getMatrixSelected(lowercut, uppercut, ngrams, StemmerType.Type.NONE, true, true, vertex);
//            ArrayList<Ngram> cpNgrams = pp.getNgrams();
//            if (vertex.size() > 0) {
//                ArrayList<String> attributes = new ArrayList<>();
//                SparseMatrix matrix_bow = this.cutDimensions(matrix, cpNgrams, attributes);
//                float[][] points = matrix_bow.toDenseMatrix();
//                double[][] covmatrix_aux = this.createCovarianceMatrix(points);
//                DoubleMatrix2D covmatrix = new DenseDoubleMatrix2D(covmatrix_aux);
//                EigenvalueDecomposition pca = new EigenvalueDecomposition(covmatrix);
//                DoubleMatrix2D eigenvector_matrix = pca.getV();
//                DoubleMatrix1D eigenvalues_matrix = pca.getRealEigenvalues();
//
//                double eigenvalues_sum = 0.0d;
//
//                ArrayList<EigenvectorCandidate> eigenvectorCandidates = new ArrayList<>();
//                for (int n = 0; n < eigenvector_matrix.columns(); n++) {
//                    eigenvectorCandidates.add(new EigenvectorCandidate(eigenvector_matrix.viewColumn(n), Math.abs(eigenvalues_matrix.get(n))));
//                    eigenvalues_sum += Math.abs(eigenvalues_matrix.get(n));
//
//                }
//                Collections.sort(eigenvectorCandidates, new Comparator<EigenvectorCandidate>() {
//
//                    @Override
//                    public int compare(EigenvectorCandidate o1, EigenvectorCandidate o2) {
//                        return Double.compare(o2.value, o1.value);
//                    }
//                });
//
//                int n = 0;
//                double sum_topics = 0.0d;
//                EigenvectorCandidate eigenvector;
//                while (true) {
//                    eigenvector = eigenvectorCandidates.get(n);
//                    if (sum_topics < this.tdata.getPcaInformationTopics()) {
//                        sum_topics += eigenvector.value / eigenvalues_sum;
//                        ArrayList<TermCandidate> candidates = new ArrayList<>();
//
//                        double totalInfTerms = 0.0d;
//                        double sum = 0.0d;
//                        for (int i = 0; i < eigenvector.eigenvector.size(); i++) {
//                            candidates.add(new TermCandidate(attributes.get(i), Math.abs(eigenvector.eigenvector.getQuick(i))));
//                            sum += Math.abs(eigenvector.eigenvector.getQuick(i));
//                        }
//
//                        Collections.sort(candidates, new Comparator<TermCandidate>() {
//
//                            @Override
//                            public int compare(TermCandidate o1, TermCandidate o2) {
//                                return Double.compare(o2.value, o1.value);
//                            }
//                        });
//                        for (int i = 0; i < candidates.size(); i++) {
//                            totalInfTerms += candidates.get(i).value / sum;
//                            if (totalInfTerms < tdata.getPcaMinInformationTerms()) {
//                                this.addTopicTag(n, candidates.get(i).id, candidates.get(i).value);
//                            } else {
//                                break;
//                            }
//                        }
//                        this.topicsWeights.add(Math.abs(eigenvectorCandidates.get(n).value));
//                        n++;
//                    } else {
//                        break;
//                    }
//
//                }
//            }
//        } catch (IOException | CloneNotSupportedException ex) {
//            Logger.getLogger(PCATopic.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    private class EigenvectorCandidate {

        public Double value;
        public DoubleMatrix1D eigenvector;

        public EigenvectorCandidate(DoubleMatrix1D eigenvector, Double value) {
            this.eigenvector = eigenvector;
            this.value = value;
        }
    }
}
