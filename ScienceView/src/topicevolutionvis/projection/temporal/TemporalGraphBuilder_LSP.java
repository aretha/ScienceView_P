/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.projection.temporal;

import com.vividsolutions.jts.geom.*;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.awt.HeadlessException;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import topicevolutionvis.database.DatabaseCorpus;
import topicevolutionvis.datamining.network.BibliographicCouplingConnectivity;
import topicevolutionvis.datamining.network.CoAuthorshipConnectivy;
import topicevolutionvis.datamining.network.CoreCitationConnectivity;
import topicevolutionvis.dimensionreduction.DimensionalityReduction;
import topicevolutionvis.dimensionreduction.DimensionalityReductionFactory;
import topicevolutionvis.dimensionreduction.DimensionalityReductionType;
import topicevolutionvis.graph.*;
import topicevolutionvis.matrix.SparseMatrix;
import topicevolutionvis.matrix.normalization.NormalizationFactory;
import topicevolutionvis.preprocessing.RepresentationFactory;
import topicevolutionvis.preprocessing.transformation.MatrixTransformationFactory;
import topicevolutionvis.projection.ProjectionData;
import topicevolutionvis.projection.ProjectionFactory;
import topicevolutionvis.projection.ProjectionType;
import topicevolutionvis.projection.distance.DissimilarityFactory;
import topicevolutionvis.projection.lsp.LSPProjection2D;
import topicevolutionvis.projection.stress.LoetStress;
import topicevolutionvis.util.PExConstants;
import topicevolutionvis.util.Utils;
import topicevolutionvis.wizard.ProjectionView;

/**
 *
 * @author USER
 */
public class TemporalGraphBuilder_LSP {

    private ProjectionView view;
    private TemporalProjection tproj;
    private IOException exception;
    private TreeMap<Integer, TemporalGraph> graphs = new TreeMap<>();
    private TIntObjectHashMap<TIntArrayList> fixedDocuments = new TIntObjectHashMap<>();
    private TIntObjectHashMap<TIntArrayList> newDocuments = new TIntObjectHashMap<>();
    private TIntObjectHashMap<TIntArrayList> updatedDocuments = new TIntObjectHashMap<>();
    private DatabaseCorpus corpus = null;
    private int min_cp = 10; //número minimo de pontos de controle para cada projeção
    double area_fullprojection = 0;
//    private SimilarityConnectivy similarityCon;
    private Scalar sdots, syear, scorecitations, stimescited, sscheme, sclass;
    private boolean zoomed = false;

    /**
     * Creates a new instance of TemporalGraphBuilder
     *
     * @param view
     * @param tproj
     * @param corpus
     * @param graph
     */
    public TemporalGraphBuilder_LSP(ProjectionView view, TemporalProjection tproj, DatabaseCorpus corpus) {
        this.view = view;
        this.tproj = tproj;
        this.exception = null;
        this.corpus = corpus;

        //add scalars
        sdots = tproj.addVertexScalar(PExConstants.DOTS);
        sclass = tproj.addVertexScalar(PExConstants.CLASS);
        syear = tproj.addVertexScalar(PExConstants.YEAR);
        scorecitations = tproj.addVertexScalar(PExConstants.LOCAL_CITATION_COUNT);
        stimescited = tproj.addVertexScalar(PExConstants.GLOBAL_CITATION_COUNT);
        sscheme = tproj.addVertexScalar("temporal projection");
    }

    /**
     *
     * @param pdata
     */
    public void start(final ProjectionData pdata) {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    long start = System.currentTimeMillis();
                    tproj.setYears(corpus.getAscendingDates());
                    //creating the initial matrix

                    SparseMatrix complete_matrix = RepresentationFactory.getInstance(pdata.getRepresentationType(), corpus).getMatrix(pdata);
//                    similarityCon = new SimilarityConnectivy(corpus, complete_matrix, DissimilarityFactory.getInstance(pdata.getDissimilarityType()));

                    TemporalGraphBuilder_LSP.this.createGraph(pdata, corpus.getAscendingDates(), complete_matrix);
                    //   generateScheme();
                    LoetStress stress = new LoetStress();
                    pdata.setStressSeries(stress.calculate(complete_matrix, DissimilarityFactory.getInstance(pdata.getDissimilarityType()), graphs));

                    setViewStatus("Creating animation ...", 80);

                    createIntermediateGraphs();
                    long end = System.currentTimeMillis();
                    pdata.setTime(end - start);
                    setViewStatus("Creating animation ...", 100);


                } catch (IOException ex) {
                    exception = ex;
                } finally {
                    if (view != null) {
                        view.finished(exception);
                    }
                }
            }
        };

        t.start();
    }

    private void createGraph(ProjectionData pdata, int[] years, SparseMatrix complete_matrix) throws IOException {
        try {
            double increment = 80 / years.length;
            int n_cp, n_conn = 10;

            //matrix transformation, normalization and dimensionality reduction
            if (pdata.getDimensionReductionType() != DimensionalityReductionType.NONE) {
                if (complete_matrix.getDimensions() > pdata.getTargetDimension()) {
                    setViewStatus("Reducing the dimensions to " + pdata.getTargetDimension() + " dimensions...", 10);
                    DimensionalityReduction dr = DimensionalityReductionFactory.getInstance(pdata.getDimensionReductionType(), pdata.getTargetDimension());
                    complete_matrix = dr.reduce(complete_matrix, this.tproj);
                }
            } else {
                complete_matrix = MatrixTransformationFactory.getInstance(pdata.getMatrixTransformationType()).tranform(complete_matrix, null);
                complete_matrix = NormalizationFactory.getInstance(pdata.getNormalization()).execute(complete_matrix);
            }

            pdata.setNumberOfDocuments(complete_matrix.getRowsCount());
            pdata.setNumberDimensions(complete_matrix.getDimensions());

//            SingleLinkAll singleLinkAll = new SingleLinkAll(corpus, complete_matrix, pdata.getDissimilarityType());
//            singleLinkAll.run();

            // this.saveData(pdata, complete_matrix, corpus);
            java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            SparseMatrix omatrix = complete_matrix;

            LSPProjection2D projLSP = (LSPProjection2D) ProjectionFactory.getInstance(ProjectionType.LSP);
            projLSP.setParameters(pdata.getNumberControlPoints(), pdata.getNumberNeighborsConnection());
            double[][] projection = projLSP.project(omatrix, pdata, null);
            projection = this.normalizeProjection(projection, Vertex.getRayBase() * 5, (d.getHeight()) / 1.40f - 5);
            this.createGraph(tproj, projection, omatrix, corpus, years[years.length - 1]);


            setViewStatus("Projecting year " + years[years.length - 1] + "...", (int) increment);

            SparseMatrix matrix = null;
            int count = 1;

            for (int i = years.length - 2; i > 2; i--) {

                matrix = this.getMatrix(complete_matrix, years[0], years[i]);

                n_cp = (int) matrix.getRowsCount() / 10;
                if (matrix.getRowsCount() < 20) {
                    min_cp = 5;
                    n_cp = n_conn = 5;
                    if (matrix.getRowsCount() <= min_cp) {
                        min_cp = 3;
                        n_cp = n_conn = 3;
                    }
                }
                projLSP = (LSPProjection2D) ProjectionFactory.getInstance(ProjectionType.LSP);
                projLSP.setParameters(n_cp, n_conn);
                projection = projLSP.project(matrix, pdata, null);

                projection = this.normalizeProjection(projection, Vertex.getRayBase() * 5, (d.getHeight()) / 1.40f - 5);
                this.createGraph(tproj, projection, matrix, corpus, years[i]);

                count++;
                setViewStatus("Projecting year " + years[i] + "...", (int) increment * count);

            }
            this.createGraph(tproj, projection, matrix, corpus, years[0]);
            this.createGraph(tproj, projection, matrix, corpus, years[1]);
            this.createGraph(tproj, projection, matrix, corpus, years[2]);

            setViewStatus("Projecting year " + years[0] + "...", 80);
        } catch (IOException | HeadlessException ex) {
            Logger.getLogger(TemporalGraphBuilder.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

//    private void generateScheme() {
//        TreeMap<Integer, TemporalGraph> graphs = tproj.getGraphs();
//        graphs.remove(graphs.firstKey());
//        for (Entry<Integer, TemporalGraph> entry : graphs.entrySet()) {
//
//            ArrayList<Integer> updated = this.updatedDocuments.get(entry.getKey());
//            ArrayList<Integer> removed = this.newDocuments.get(entry.getKey());
//            TemporalGraph graph = entry.getValue();
//            for (Vertex v : graph.getVertex()) {
//                v.setScalar(this.sscheme, 1.0f);
//                if (updated != null && updated.contains(v.getId())) {
//                    v.setScalar(this.sscheme, 0.5f);
//                }
//                if (removed != null && removed.contains(v.getId())) {
//                    v.setScalar(this.sscheme, 0.0f);
//                }
//            }
//        }
//
//    }
    private void createGraph(TemporalProjection tproj, double[][] projection, SparseMatrix matrix, DatabaseCorpus corpus, int year) {
        TemporalGraph graph = new TemporalGraph(tproj, year, 0);


        double increment = 1.0 / tproj.getNumberOfYears();

        TIntObjectHashMap<Vertex> vertex = graph.getVertex();
        Vertex v;
        for (int i = 0; i < projection.length; i++) {
            v = new Vertex(matrix.getRowWithIndex(i).getId(), projection[i][0], projection[i][1]);
            v.setScalar(sdots, 0.0f);
            v.setPublishedYear(corpus.getYear(v.getId()));
            v.setScalar(sclass, corpus.getClass(v.getId()));
            v.setScalar(syear, Utils.indexOf(tproj.getYears(), v.getPublishedYear().intValue()) * increment);
            v.setScalar(scorecitations, corpus.getNumberOfCitationsAtYear(v.getId(), year));
            v.setScalar(stimescited, corpus.getGlobalCitationCount(v.getId()));
            v.setTemporalProjection(tproj);
            tproj.setTitleDocument(v.getId(), corpus.getTitle(v.getId()));
            vertex.put(v.getId(), v);
        }
        graph.setVertex(vertex);

        tproj.addConnectivity(year, new Connectivity(ConnectivityType.NONE, false, false));
        tproj.addConnectivity(year, (new BibliographicCouplingConnectivity(corpus)).getBibliographicCoupling(graph.getVertex()));
        tproj.addConnectivity(year, (new CoreCitationConnectivity(corpus)).getCitationCore(graph.getVertex()));
        tproj.addConnectivity(year, (new CoAuthorshipConnectivy(corpus)).getCoAuthorshipConnectivy(graph.getVertex()));
        this.graphs.put(graph.getYear(), graph);
    }

    public double getAreaOfProjection(double[][] projection) {
        Coordinate[] coords = new Coordinate[projection.length];
        for (int i = 0; i < coords.length; i++) {
            coords[i] = new Coordinate(projection[i][0], projection[i][1]);
        }
        return (new GeometryFactory()).createMultiPoint(coords).convexHull().getArea();
    }

    public double[][] normalizeProjection(double[][] projection, double begin, double end) {
        double maxX = projection[0][0];
        double minX = projection[0][0];
        double maxY = projection[0][1];
        double minY = projection[0][1];

        //Encontra o maior e menor valores para X e Y
        for (int i = 0; i < projection.length; i++) {
            if (maxX < projection[i][0]) {
                maxX = projection[i][0];
            } else {
                if (minX > projection[i][0]) {
                    minX = projection[i][0];
                }
            }

            if (maxY < projection[i][1]) {
                maxY = projection[i][1];
            } else {
                if (minY > projection[i][1]) {
                    minY = projection[i][1];
                }
            }
        }


        ///////Fazer a largura ficar proporcional a altura
        double endX = ((maxX - minX) * end);
        if (maxY != minY) {
            endX = ((maxX - minX) * end) / (maxY - minY);
        }
        //////////////////////////////////////////////////

        double[][] normalized_projection = new double[projection.length][2];
        for (int i = 0; i < projection.length; i++) {
        }

        //Normalizo
        for (int i = 0; i < projection.length; i++) {
            if (maxX != minX) {
                normalized_projection[i][0] = (((projection[i][0] - minX) / (maxX - minX)) * (endX - begin)) + begin;
            } else {
                normalized_projection[i][0] = begin;
            }

            if (maxY != minY) {
                normalized_projection[i][1] = ((((projection[i][1] - minY) / (maxY - minY)) * (end - begin)) + begin);
            } else {
                normalized_projection[i][1] = begin;
            }

        }
        return normalized_projection;
    }

//    private void generateEdgesTxt(int year, Connectivity con) {
//        try {
//            try (BufferedWriter writer = new BufferedWriter(new FileWriter(con.getName().concat("_").concat(Integer.toString(year)).concat(".txt")))) {
//                System.out.println(con.getName().concat("_").concat(Integer.toString(year)).concat(".txt"));
//                int index = 0;
//                float min = Float.POSITIVE_INFINITY, max = Float.NEGATIVE_INFINITY;
//                for (Edge edge : con.getEdges()) {
//                    Vertex source = edge.getSource();
//                    Vertex target = edge.getTarget();
//
//                    //min value
//                    if (source.getX() < min) {
//                        min = source.getX();
//                    }
//                    if (source.getY() < min) {
//                        min = source.getY();
//                    }
//                    if (target.getX() < min) {
//                        min = target.getX();
//                    }
//                    if (target.getY() < min) {
//                        min = target.getY();
//                    }
//
//                    //max value
//                    if (source.getX() > max) {
//                        max = source.getX();
//                    }
//                    if (source.getY() > max) {
//                        max = source.getY();
//                    }
//                    if (target.getX() > max) {
//                        max = target.getX();
//                    }
//                    if (target.getY() > max) {
//                        max = target.getY();
//                    }
//                }
//                for (Edge edge : con.getEdges()) {
//                    Vertex source = edge.getSource();
//                    Vertex target = edge.getTarget();
//                    writer.write(Integer.toString(index) + ": " + (source.getX() - min) / (max - min) + " " + (source.getY() - min) / (max - min) + " " + (target.getX() - min) / (max - min) + " " + (target.getY() - min) / (max - min));
//                    writer.newLine();
//                    writer.flush();
//                    index++;
//                }
//            }
//
//        } catch (IOException ex) {
//            Logger.getLogger(TemporalGraphBuilder2LSP.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    private void createIntermediateGraphs() {
        int year;

        for (int i = 0; i < tproj.getNumberOfYears(); i++) {
            year = tproj.getYearWithIndex(i);
            ArrayList<TemporalGraph> graphsAux = new ArrayList<>();
            for (int j = 0; j < TemporalProjection.getN(); j++) {
                graphsAux.add(this.graphs.get(year));
            }
            this.tproj.addGraphs(year, graphsAux);


        }
    }

    private void createIntermediateGraphs(Integer year, TemporalGraph graph1, TemporalGraph graph2, TIntArrayList newDocuments, TIntArrayList updatedDocuments, TIntArrayList fixedDocuments) {
        ArrayList<TemporalGraph> graphsAux = new ArrayList<>();
        TemporalGraph graph;
        TIntObjectHashMap< Vertex> vertex;
        Vertex newVertex = null, v1, v2;
        double increment = 1.0 / tproj.getNumberOfYears();
        for (int i = 0; i < TemporalProjection.getN(); i++) {
            graph = new TemporalGraph(tproj, year, i);
            vertex = new TIntObjectHashMap<>();

            TIntIterator it = fixedDocuments.iterator();
            while (it.hasNext()) {
                int id = it.next();
                v1 = graph1.getVertexById(id);
                v2 = graph2.getVertexById(id);
                if (v1.getY() != v2.getY()) {
                    double x1 = v1.getX();
                    double y1 = v1.getY();
                    double x2 = v2.getX();
                    double y2 = v2.getY();
                    double x, y;
                    LinearInterpolator interpolator = new LinearInterpolator();
                    double[] x_input = new double[2], y_input = new double[2];
                    if (x2 > x1) {
                        x_input[0] = x1;
                        x_input[1] = x2;
                        y_input[0] = y1;
                        y_input[1] = y2;
                        x = x1 + ((Math.abs(x2 - x1) / TemporalProjection.getN()) * i);
                        newVertex = new Vertex(id, x, interpolator.interpolate(x_input, y_input).value(x));
                    } else if (x1 > x2) {
                        x_input[0] = x2;
                        x_input[1] = x1;
                        y_input[0] = y2;
                        y_input[1] = y1;
                        x = x1 - ((Math.abs(x2 - x1) / TemporalProjection.getN()) * i);
                        newVertex = new Vertex(id, x, interpolator.interpolate(x_input, y_input).value(x));
                    } else if (x1 == x2) {
                        if (y2 > y1) {
                            x_input[0] = y1;
                            x_input[1] = y2;
                            y_input[0] = x1;
                            y_input[1] = x2;
                            y = y1 + ((Math.abs(y2 - y1) / TemporalProjection.getN()) * i);
                            newVertex = new Vertex(id, interpolator.interpolate(x_input, y_input).value(y), y);
                        } else {
                            x_input[0] = y2;
                            x_input[1] = y1;
                            y_input[0] = x2;
                            y_input[1] = x1;
                            y = y1 - ((Math.abs(y2 - y1) / TemporalProjection.getN()) * i);
                            newVertex = new Vertex(id, interpolator.interpolate(x_input, y_input).value(y), y);
                        }
                    }

                } else {
                    newVertex = new Vertex(id, v1.getX(), v1.getY());
                }

                newVertex.setScalar(sdots, 0.0f);
                newVertex.setPublishedYear(v2.getPublishedYear());
                newVertex.setScalar(sclass, corpus.getClass(id));
                newVertex.setScalar(syear, Utils.indexOf(tproj.getYears(), newVertex.getPublishedYear()) * increment);
                newVertex.setScalar(scorecitations, v2.getScalar(scorecitations, false));
                newVertex.setScalar(stimescited, v2.getScalar(stimescited, false));
                newVertex.setScalar(sscheme, 1.0f);
                newVertex.setTemporalProjection(tproj);
                vertex.put(id, newVertex);
            }
            it = newDocuments.iterator();
            while (it.hasNext()) {
                int id = it.next();
                v1 = graph2.getVertexById(id);
                newVertex = new Vertex(id, v1.getX(), v1.getY());
                newVertex.setScalar(sdots, 0.0f);
                newVertex.setScalar(sclass, corpus.getClass(id));
                newVertex.setPublishedYear(v1.getPublishedYear());
                newVertex.setScalar(syear, Utils.indexOf(tproj.getYears(), newVertex.getPublishedYear()) * increment);
                newVertex.setScalar(scorecitations, v1.getScalar(scorecitations, false));
                newVertex.setScalar(stimescited, v1.getScalar(stimescited, false));
                newVertex.setScalar(this.sscheme, 0.0f);
                newVertex.setTemporalProjection(tproj);
                newVertex.setAlpha((1.0f / TemporalProjection.getN()) * i);
                vertex.put(id, newVertex);
            }
            if (updatedDocuments != null) {
                it = updatedDocuments.iterator();
                while (it.hasNext()) {
                    int id = it.next();
                    v1 = graph1.getVertexById(id);
                    v2 = graph2.getVertexById(id);

                    double x1 = v1.getX();
                    double y1 = v1.getY();
                    double x2 = v2.getX();
                    double y2 = v2.getY();
                    double x, y;
                    LinearInterpolator interpolator = new LinearInterpolator();
                    double[] x_input = new double[2], y_input = new double[2];
                    if (x2 > x1) {
                        x_input[0] = x1;
                        x_input[1] = x2;
                        y_input[0] = y1;
                        y_input[1] = y2;
                        x = x1 + ((Math.abs(x2 - x1) / TemporalProjection.getN()) * i);
                        newVertex = new Vertex(id, x, interpolator.interpolate(x_input, y_input).value(x));
                    } else if (x1 > x2) {
                        x_input[0] = x2;
                        x_input[1] = x1;
                        y_input[0] = y2;
                        y_input[1] = y1;
                        x = x1 - ((Math.abs(x2 - x1) / TemporalProjection.getN()) * i);
                        newVertex = new Vertex(id, x, interpolator.interpolate(x_input, y_input).value(x));
                    } else if (x1 == x2) {
                        if (y2 > y1) {
                            x_input[0] = y1;
                            x_input[1] = y2;
                            y_input[0] = x1;
                            y_input[1] = x2;
                            y = y1 + ((Math.abs(y2 - y1) / TemporalProjection.getN()) * i);
                            newVertex = new Vertex(id, interpolator.interpolate(x_input, y_input).value(y), y);
                        } else if (y2 < y1) {
                            x_input[0] = y2;
                            x_input[1] = y1;
                            y_input[0] = x2;
                            y_input[1] = x1;
                            y = y1 - ((Math.abs(y2 - y1) / TemporalProjection.getN()) * i);
                            newVertex = new Vertex(id, interpolator.interpolate(x_input, y_input).value(y), y);
                        } else {
                            newVertex = new Vertex(id, x1, y1);
                        }
                    }
                    newVertex.setScalar(sdots, 0.0f);
                    newVertex.setPublishedYear(v1.getPublishedYear());
                    newVertex.setScalar(sclass, v1.getScalar(sclass, false));
                    newVertex.setScalar(syear, Utils.indexOf(tproj.getYears(), newVertex.getPublishedYear()) * increment);
                    newVertex.setScalar(scorecitations, v2.getScalar(scorecitations, false));
                    newVertex.setScalar(stimescited, v2.getScalar(stimescited, false));
                    newVertex.setScalar(this.sscheme, 0.5f);
                    newVertex.setTemporalProjection(tproj);
                    vertex.put(id, newVertex);
                }
            }
            graph.setVertex(vertex);


            graphsAux.add(graph);
        }
        this.tproj.addGraphs(year, graphsAux);
    }
//

    private SparseMatrix getMatrix(SparseMatrix omatrix, int begin_year, int end_year) throws IOException {
        TIntArrayList ids = corpus.getDocumentsIdsFromYearToYear(begin_year, end_year);
        SparseMatrix nmatrix = new SparseMatrix(ids.size());
        for (TIntIterator it = ids.iterator(); it.hasNext();) {
            nmatrix.addRow(omatrix.getRowWithId(it.next()));
        }
        return nmatrix;
    }

//
//    private void saveTrajectory(TreeMap<Integer, TemporalGraph> graphs, ProjectionData pdata) {
//        BufferedWriter out = null;
//        StringBuilder aux = null;
//        try {
//            out = new BufferedWriter(new FileWriter(pdata.getCollectionName().concat("_traj.txt")));
//            //writing the collection name
//            out.write(pdata.getCollectionName());
//            out.newLine();
//            //writing the years
//            out.write(graphs.keySet().toString().replace("[", "").replace("]", "").replace(",", ";"));
//            out.newLine();
//
//            int id;
//            int ids[] = corpus.getDocumentsIds();
//            Vertex v;
//            String line;
//            for (int i = 0; i < ids.length; i++) {
//                id = ids[i];
//                aux = new StringBuilder(Integer.toString(id)).append(": ");
//                for (Integer year : graphs.keySet()) {
//                    v = graphs.get(year).getVertexByUrl(id);
//                    if (v != null) {
//                        aux.append(v.getX()).append(", ").append(v.getY()).append(";");
//                    } else {
//                        aux.append("-;");
//                    }
//                }
//                line = aux.toString();
//                out.write(line.substring(0, line.length() - 1));
//                out.newLine();
//                out.flush();
//            }
//
//            out.close();
//        } catch (IOException ex) {
//            Logger.getLogger(TemporalGraphBuilder.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    private void setViewStatus(String status, int value) {
        if (view != null) {
            view.setStatus(status, value);
        }
    }
}
