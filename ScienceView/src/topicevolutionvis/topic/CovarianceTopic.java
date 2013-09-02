package topicevolutionvis.topic;

import com.vividsolutions.jts.geom.Coordinate;
import gnu.trove.list.array.TIntArrayList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import topicevolutionvis.graph.TemporalGraph;
import topicevolutionvis.matrix.SparseMatrix;
import topicevolutionvis.preprocessing.Ngram;
import topicevolutionvis.preprocessing.VectorSpaceRepresentation;
import topicevolutionvis.preprocessing.steemer.StemmerType;
import topicevolutionvis.projection.temporal.TemporalProjection;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class CovarianceTopic extends Topic {

    /**
     * Creates a new instance of CovarianceTopic
     *
     * @param graph
     * @param vertex
     * @param corpus
     * @param tdata
     */
    public CovarianceTopic(TIntArrayList vertex, TemporalProjection tprojection, TemporalGraph graph) {
        super(vertex, tprojection, graph);
        this.tdata = tprojection.getTopicData();
        this.id = this.tdata.getNextAvailableId();
        this.tprojection = tprojection;
        this.relation = vertex.size() / corpus.getNumberOfDocuments();

    }

    public CovarianceTopic(TIntArrayList vertex, ArrayList<Coordinate> fake_vertex, TemporalProjection tprojection, TemporalGraph graph) {
        super(vertex, fake_vertex, tprojection, graph);
        this.tprojection = tprojection;
        this.corpus = tprojection.getDatabaseCorpus();
        this.tdata = tprojection.getTopicData();
        this.id = this.tdata.getNextAvailableId();
        this.relation = vertex.size() / corpus.getNumberOfDocuments();
    }

    @Override
    public void createTopic() {
        try {
            int lowercut = 2, uppercut = -1, ngrams = 1;

            if (vertex_id.size() > 50 && vertex_id.size() < 100) {
                lowercut = 10;
            } else if (vertex_id.size() > 100 && vertex_id.size() < 300) {
                lowercut = 15;
            } else if (vertex_id.size() > 300) {
                lowercut = 20;
            }

            VectorSpaceRepresentation pp = new VectorSpaceRepresentation(corpus);
            SparseMatrix matrix = pp.getMatrixSelected(lowercut, uppercut, ngrams,
                    StemmerType.Type.NONE, true, true, this.getVertexList());
            ArrayList<Ngram> cpNgrams = pp.getNgrams();

            //Reducing the points and creating an index
            if (matrix.getRowsCount() > 0 && matrix.getDimensions() > 0) {
                ArrayList<String> attributes = new ArrayList<>();
                double[][] points = this.cutDimensions(matrix, cpNgrams, attributes);

                this.createTopic(points, attributes);
            }
        } catch (IOException ex) {
            Logger.getLogger(CovarianceTopic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createTopic(double[][] points, ArrayList<String> attributes) throws IOException {
        //Extracting the mean of the columns
        double[] mean = new double[points[0].length];
        Arrays.fill(mean, 0.0f);

        for (int i = 0; i < points.length; i++) {
            //calculating
            for (int j = 0; j < points[i].length; j++) {
                mean[j] += points[i][j];
            }
        }

        for (int i = 0; i < mean.length; i++) {
            mean[i] /= points.length;
        }

        //extracting the mean
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                points[i][j] -= mean[j];
            }
        }

        indexTopic = 0;
        TIntArrayList indexes = new TIntArrayList();
        boolean cont = this.createStringBoxes(points, attributes, indexes);


        if (cont == true) {
            for (int i = 0; i < 10; i++) {
                cont = this.createStringBoxes(points, attributes, indexes);
                if (!cont) {
                    break;
                }
            }

            //returning the mean
            for (int i = 0; i < points.length; i++) {
                for (int j = 0; j < points[i].length; j++) {
                    points[i][j] += mean[j];
                }
            }

//            this.colorVertex(points, vertex, indexes);
        }
    }

    private boolean createStringBoxes(double[][] points, ArrayList<String> attributes, TIntArrayList indexes) {

        //Get the two attributes with largest covariance
        double gcov1 = Double.MIN_VALUE;
        int icov = 0;
        int jcov = 0;
        for (int i = 0; i < points[0].length - 1; i++) {
            for (int j = points[0].length - 1; j > i; j--) {
                if (!indexes.contains(i) && !indexes.contains(j)) {
                    double aux = this.covariance(points, i, j);
                    if (gcov1 < aux) {
                        gcov1 = aux;
                        icov = i;
                        jcov = j;
                    }
                }
            }
        }

        indexes.add(icov);
        indexes.add(jcov);

        if (attributes.size() > 0) {
            this.newTopic(gcov1);

            this.addTopicTag(attributes.get(icov), 1.0);
            this.addTopicTag(attributes.get(jcov), 1.0);

            if (indexTopic == 0) {
                maxcov = gcov1;
            }

            for (int i = 0; i < points[0].length - 1; i++) {
                if (!indexes.contains(i)) {
                    double aux = (this.covariance(points, icov, i) + this.covariance(points, jcov, i)) / 2;
                    if (aux / gcov1 > tdata.getCovariancePercentageTerms()) {
                        indexes.add(i);
                        if (indexTopic == 0 || gcov1 > maxcov * tdata.getCovariancePercentageTopics()) {
                            this.addTopicTag(attributes.get(i), aux / gcov1);
                        }
                    }
                }
            }

            if (indexTopic == 0 || gcov1 > maxcov * tdata.getCovariancePercentageTopics()) {
                indexTopic++;
                return true;
            }
        }
        return false;
    }

    private double[][] cutDimensions(SparseMatrix matrix, ArrayList<Ngram> cpNgrams,
            ArrayList<String> indexGrams) {
        //keep on the new points matrix no more than 200 dimensions
        double[][] newpoints = new double[matrix.getRowsCount()][];

        for (int i = 0; i < newpoints.length; i++) {
            newpoints[i] = new double[(matrix.getDimensions() < 200) ? matrix.getDimensions() : 200];
            double[] point = matrix.getRowWithIndex(i).toDenseVector();
            System.arraycopy(point, 0, newpoints[i], 0, newpoints[i].length);
        }

        indexGrams.clear();
        for (int i = 0; i < newpoints[0].length; i++) {
            indexGrams.add(cpNgrams.get(i).ngram);
        }

        return newpoints;
    }

    //calculate the covariance between columns a and b
    private double covariance(double[][] points, int a, int b) {
        double covariance = 0.0f;
        for (int i = 0; i < points.length; i++) {
            covariance += points[i][a] * points[i][b];
        }

        covariance /= points.length;
        return covariance;
    }
    private int indexTopic;
    private double maxcov;
    private TopicData tdata;

    @Override
    public Object clone() throws CloneNotSupportedException {
        Topic newTopic = new CovarianceTopic(new TIntArrayList(), this.tprojection, this.graph);
        for (int i = 0; i < this.vertex_id.size(); i++) {
            newTopic.vertex_id.add(vertex_id.get(i));
        }
        for (Coordinate c : this.fake_vertex) {
            newTopic.fake_vertex.add(new Coordinate(c));
        }
        if (this.usedConvex()) {
            newTopic.setDrawPolygonOption(Topic.USE_CONVEX);
        } else {
            newTopic.setDrawPolygonOption(this.getDrawPolygonOption());
        }
        newTopic.graph = this.graph;
        newTopic.setId(this.getId());
        newTopic.setAlphaPolygon(this.getAlphaPolygon());
        newTopic.setSelected(this.selected);
        this.cloneInfo(newTopic);
        newTopic.setNextAddedVertex(this.getNextAddedVertex());
        return newTopic;
    }
}
