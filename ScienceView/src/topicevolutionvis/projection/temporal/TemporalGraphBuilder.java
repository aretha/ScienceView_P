/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.projection.temporal;

import com.vividsolutions.jts.geom.*;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
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
import topicevolutionvis.preprocessing.RepresentationType;
import topicevolutionvis.preprocessing.transformation.MatrixTransformationFactory;
import topicevolutionvis.projection.ProjectionData;
import topicevolutionvis.projection.ProjectionFactory;
import topicevolutionvis.projection.ProjectionType;
import topicevolutionvis.projection.distance.Dissimilarity;
import topicevolutionvis.projection.distance.DissimilarityFactory;
import topicevolutionvis.projection.distance.DistanceMatrix;
import topicevolutionvis.projection.lsp.LSPProjection2D;
import topicevolutionvis.projection.lsp.MeshGenerator;
import topicevolutionvis.projection.stress.LoetStress;
import topicevolutionvis.util.KNN;
import topicevolutionvis.util.PExConstants;
import topicevolutionvis.util.Pair;
import topicevolutionvis.util.Utils;
import topicevolutionvis.wizard.ProjectionView;

/**
 *
 * @author Aretha
 */
public class TemporalGraphBuilder {

    private final ProjectionView view;
    private final TemporalProjection tproj;
    private Dissimilarity diss = null;
    private IOException exception;
    private final TreeMap<Integer, TemporalGraph> graphs = new TreeMap<>();
    private final TIntObjectHashMap<TIntArrayList> fixedDocuments = new TIntObjectHashMap<>();
    private final TIntObjectHashMap<TIntArrayList> newDocuments = new TIntObjectHashMap<>();
    private final TIntObjectHashMap<TIntArrayList> updatedDocuments = new TIntObjectHashMap<>();
    private final TIntObjectHashMap<TIntArrayList> usedCPDocuments = new TIntObjectHashMap<>();
    private DatabaseCorpus corpus = null;
    private final boolean reduced_number_of_control_points = false;
    private int min_cp = 10; //número minimo de pontos de controle para cada projeção
    double area_fullprojection = 0;
//  private SimilarityConnectivy similarityCon;
    private final Scalar sdots, syear, scorecitations, stimescited, sscheme, sclass;

    /**
     * Creates a new instance of TemporalGraphBuilder
     *
     * @param view
     * @param tproj
     * @param corpus
     */
    public TemporalGraphBuilder(ProjectionView view, TemporalProjection tproj, DatabaseCorpus corpus) {
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
                    long time1 = System.currentTimeMillis();
                    tproj.setYears(corpus.getAscendingDates());
                    //creating the initial matrix

                    long time2 = System.currentTimeMillis();
                    SparseMatrix complete_matrix = RepresentationFactory.getInstance(pdata.getRepresentationType(), corpus).getMatrix(pdata);
//                    similarityCon = new SimilarityConnectivy(corpus, complete_matrix, DissimilarityFactory.getInstance(pdata.getDissimilarityType()));
                    diss = DissimilarityFactory.getInstance(pdata.getDissimilarityType());

                    long time3 = System.currentTimeMillis();
                    TemporalGraphBuilder.this.createGraph(pdata, corpus.getAscendingDates(), complete_matrix);
                    //   generateScheme();
                    LoetStress stress = new LoetStress();
                    pdata.setStressSeries(stress.calculate(complete_matrix, DissimilarityFactory.getInstance(pdata.getDissimilarityType()), graphs));
                    setViewStatus("Creating animation ...", 80);
                    //normalizing all graphs on the same range
                    normalizeGraphs(graphs);
                    createIntermediateGraphs();

                    long time4 = System.currentTimeMillis();
                    System.out.println("Pré-processamento time:" + (time3 - time2));
                    System.out.println("T-LSP time: " + (time4 - time3));
                    pdata.setTime(time4 - time3);
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

    private TIntArrayList selectMeaningfulControlPoints(Pair[][] mesh, TIntArrayList removed, TIntArrayList fixed, TIntArrayList update, SparseMatrix omatrix) {
        TIntArrayList used = new TIntArrayList();
        for (TIntIterator it = update.iterator(); it.hasNext();) {
            for (Pair neighbor : mesh[omatrix.getIndexWithId(it.next())]) {
                if (!used.contains(omatrix.getIds()[neighbor.index]) && fixed.contains(omatrix.getIds()[neighbor.index])) {
                    used.add(omatrix.getIds()[neighbor.index]);
                }
            }
        }
//        for (TIntIterator it = removed.iterator(); it.hasNext();) {
//            for (Pair neighbor : mesh[omatrix.getIndexWithId(it.next())]) {
//                if (!used.contains(omatrix.getIds()[neighbor.index]) && fixed.contains(omatrix.getIds()[neighbor.index])) {
//                    used.add(omatrix.getIds()[neighbor.index]);
//                }
//            }
//        }
        if (used.isEmpty()) {
            used.addAll(fixed);
        }
        System.err.println("Proportion of meaningful control points: " + used.size() + "/" + omatrix.getRowsCount() + "(" + (used.size() * 100) / omatrix.getRowsCount() + ")");

        return used;
    }

    private TIntArrayList toUpdate(Pair[][] mesh, TIntArrayList ids_remove, SparseMatrix omatrix, TreeMap<Integer, TIntArrayList> update) {
        TIntArrayList aux;
        Integer neighbor_id;
        for (TIntIterator it = ids_remove.iterator(); it.hasNext();) {
            int id = it.next();
            for (Pair neighbor : mesh[omatrix.getIndexWithId(id)]) {
                neighbor_id = omatrix.getIds()[neighbor.index];
                if (!ids_remove.contains(neighbor_id)) {
                    if (update.containsKey(neighbor_id)) {
                        update.get(neighbor_id).add(id);
                    } else {
                        aux = new TIntArrayList();
                        aux.add(id);
                        update.put(neighbor_id, aux);
                    }
                }
            }
        }
        //para serem atualizados os documentos devem ter mais de um documento em sua vizinhança que foi removido
        TIntArrayList updateSelected = new TIntArrayList();
        Iterator<Entry<Integer, TIntArrayList>> iterator = update.entrySet().iterator();
        Entry<Integer, TIntArrayList> entry;
        while (iterator.hasNext()) {
            entry = iterator.next();
            if (entry.getValue().size() > 0) {
                updateSelected.add(entry.getKey());
            }
        }
        return updateSelected;
    }

    private void createGraph(ProjectionData pdata, int[] years, SparseMatrix previous_full_matrix) throws IOException {
        try {
            int n_cp, n_conn;
            double increment = 80 / years.length;
            TreeMap<Integer, TIntArrayList> update;
            this.saveData(pdata, previous_full_matrix, corpus);

            //matrix transformation, normalization and dimensionality reduction
            if (pdata.getDimensionReductionType() != DimensionalityReductionType.NONE) {
                if (previous_full_matrix.getDimensions() > pdata.getTargetDimension()) {
                    setViewStatus("Reducing the dimensions to " + pdata.getTargetDimension() + " dimensions...", 10);
                    DimensionalityReduction dr = DimensionalityReductionFactory.getInstance(pdata.getDimensionReductionType(), pdata.getTargetDimension());
                    previous_full_matrix = dr.reduce(previous_full_matrix, this.tproj);
                }
            } else if (pdata.getRepresentationType() != RepresentationType.LDA) {
                previous_full_matrix = MatrixTransformationFactory.getInstance(pdata.getMatrixTransformationType()).tranform(previous_full_matrix, null);
                previous_full_matrix = NormalizationFactory.getInstance(pdata.getNormalization()).execute(previous_full_matrix);
            }

            if (!pdata.getDistanceMatrixFilename().isEmpty()) {
                DistanceMatrix distanceMatrix = new DistanceMatrix(previous_full_matrix, DissimilarityFactory.getInstance(pdata.getDissimilarityType()));
                distanceMatrix.save(pdata.getDistanceMatrixFilename().substring(0, pdata.getDistanceMatrixFilename().indexOf(".")).concat("-" + years[years.length - 1]).concat(".dmat"));
            }

            pdata.setNumberOfDocuments(previous_full_matrix.getRowsCount());
            pdata.setNumberDimensions(previous_full_matrix.getDimensions());

            //Criando a projeção do último ano
            LSPProjection2D projLSP = (LSPProjection2D) ProjectionFactory.getInstance(ProjectionType.LSP);
            projLSP.setParameters(pdata.getNumberControlPoints(), pdata.getNumberNeighborsConnection());
            double[][] projection = projLSP.project(previous_full_matrix, pdata, null);
            this.createGraph(tproj, projection, previous_full_matrix, corpus, years[years.length - 1]);

            setViewStatus("Projecting year " + years[years.length - 1] + "...", (int) increment);

            SparseMatrix current_full_matrix = null, current_reduced_matrix = null;
            int count = 1;
            int[] controlpoints;
            double[][] projection_cp;
            TIntArrayList ids_remove, ids_update, ids_fixed, meaningful_cp;
            System.err.println("---------------------");
            for (int i = years.length - 2; i > 0; i--) {
                n_cp = 10;
                n_conn = pdata.getNumberNeighborsConnection();

                update = new TreeMap<>();  //documentos que devem ser atualizados
                ids_fixed = new TIntArrayList(); //documentos que devem permanecer fixos
                ids_remove = Utils.toArrayList(corpus.getDocumentsIds(years[i + 1])); //documentos que devem ser removidos

                current_full_matrix = this.getMatrix(previous_full_matrix, years[0], years[i]);

                if (!pdata.getDistanceMatrixFilename().isEmpty()) {
                    DistanceMatrix distanceMatrix = new DistanceMatrix(current_full_matrix, DissimilarityFactory.getInstance(pdata.getDissimilarityType()));
                    distanceMatrix.save(pdata.getDistanceMatrixFilename().substring(0, pdata.getDistanceMatrixFilename().indexOf(".")).concat("-" + years[i]).concat(".dmat"));
                }
                if (current_full_matrix.getRowsCount() < 20) {
                    min_cp = 5;
                    n_cp = n_conn = 5;
                    if (current_full_matrix.getRowsCount() <= min_cp) {
                        min_cp = 3;
                        n_cp = n_conn = 3;
                    }
                }
                if (current_full_matrix.getRowsCount() <= 3) {
                    for (int j = 0; j < current_full_matrix.getIds().length; j++) {
                        ids_fixed.add(current_full_matrix.getIds()[j]);
                    }
                    this.newDocuments.put(years[i + 1], ids_remove);
                    this.fixedDocuments.put(years[i + 1], ids_fixed);
                    projection_cp = new double[ids_fixed.size()][2];
                    for (int j = 0; j < ids_fixed.size(); j++) {
                        int index_old = previous_full_matrix.getIndexWithId(ids_fixed.get(j));
                        projection_cp[j][0] = projection[index_old][0];
                        projection_cp[j][1] = projection[index_old][1];

                    }
                    projection = new double[ids_fixed.size()][2];
                    for (int j = 0; j < projection_cp.length; j++) {
                        projection[j][0] = projection_cp[j][0];
                        projection[j][1] = projection_cp[j][1];
                    }
                    createGraph(tproj, projection, current_full_matrix, corpus, years[i]);
                    previous_full_matrix = current_full_matrix;
                    System.out.println("---------------------");
                } else {
                    //documentos que devem ser alterados
                    Pair[][] mesh = projLSP.getUsedMesh();
                    if (this.reduced_number_of_control_points && i != years.length - 2) {
                        mesh = this.getMesh(previous_full_matrix, diss, n_conn);
                    }

                    ids_update = this.toUpdate(mesh, ids_remove, previous_full_matrix, update);

                    //documentos que devem ficar fixos
                    ids_fixed = Utils.toArrayList(previous_full_matrix.getIds());
                    ids_fixed.removeAll(ids_remove);
                    ids_fixed.removeAll(ids_update);

                    //se não existem pontos de controles suficientes, escolhar mais entre os a serem alterados
                    if (ids_fixed.size() < min_cp) {
                        this.chooseAdditionalControlPoints(update, ids_update, ids_fixed, previous_full_matrix, projection);
                    }

                    System.err.println("Year: " + years[i]);
                    meaningful_cp = this.selectMeaningfulControlPoints(mesh, ids_remove, ids_fixed, ids_update, previous_full_matrix);
                    current_reduced_matrix = this.getMatrix(current_full_matrix, years[0], years[i], ids_update, meaningful_cp);

                    //pegando a posição dos documentos que devem ficar fixos na projeção anterior
                    if (ids_fixed.size() > 0 && !this.reduced_number_of_control_points) {
                        n_cp = ids_fixed.size();
                        controlpoints = new int[n_cp];
                        projection_cp = new double[n_cp][2];

                        for (int j = 0; j < ids_fixed.size(); j++) {
                            int index_old = previous_full_matrix.getIndexWithId(ids_fixed.get(j));
                            controlpoints[j] = current_full_matrix.getIndexWithId(ids_fixed.get(j));
                            projection_cp[j][0] = projection[index_old][0];
                            projection_cp[j][1] = projection[index_old][1];
                        }
                        projLSP = (LSPProjection2D) ProjectionFactory.getInstance(ProjectionType.LSP);
                        projLSP.setControlPoints(controlpoints, projection_cp);
                    } else if (meaningful_cp.size() > 0 && this.reduced_number_of_control_points) {
                        n_cp = meaningful_cp.size();
                        controlpoints = new int[n_cp];
                        projection_cp = new double[n_cp][2];
                        for (int j = 0; j < meaningful_cp.size(); j++) {
                            int index_old = previous_full_matrix.getIndexWithId(meaningful_cp.get(j));
                            controlpoints[j] = current_reduced_matrix.getIndexWithId(meaningful_cp.get(j));
                            projection_cp[j][0] = projection[index_old][0];
                            projection_cp[j][1] = projection[index_old][1];
                        }
                        projLSP = (LSPProjection2D) ProjectionFactory.getInstance(ProjectionType.LSP);
                        projLSP.setControlPoints(controlpoints, projection_cp);
                    }

                    this.fixedDocuments.put(years[i + 1], ids_fixed);
                    this.updatedDocuments.put(years[i + 1], ids_update);
                    this.newDocuments.put(years[i + 1], ids_remove);
                    this.usedCPDocuments.put(years[i + 1], meaningful_cp);

                    System.err.println("Fixed: " + ids_fixed.size());
                    System.err.println("Update: " + ids_update.size());
                    System.err.println("Removed: " + ids_remove.size());

                    if (ids_fixed.size() + ids_update.size() < 1000) {
                        projLSP.setUseKnn(true);
                    }

                    projLSP.setParameters(n_cp, n_conn);
                    if (!this.reduced_number_of_control_points || ids_fixed.size() == meaningful_cp.size() || meaningful_cp.size() <= 10) {
                        projection = projLSP.project(current_full_matrix, pdata, null);
                        createGraph(tproj, projection, current_full_matrix, corpus, years[i]);
                    } else {
                        int n = 0;
                        double[][] projection1 = projLSP.project(current_reduced_matrix, pdata, null);
                        if (ids_fixed.size() > meaningful_cp.size()) {
                            int[] aux = new int[ids_fixed.size() - meaningful_cp.size()];
                            double[][] fixed_points_projection = new double[ids_fixed.size() - meaningful_cp.size()][2];
                            for (TIntIterator it = ids_fixed.iterator(); it.hasNext();) {
                                int id_fixed = it.next();
                                if (!meaningful_cp.contains(id_fixed)) {
                                    aux[n] = id_fixed;
                                    int index_old = previous_full_matrix.getIndexWithId(id_fixed);
                                    fixed_points_projection[n] = projection[index_old];
                                    n++;
                                }
                            }
                            int[] teste = current_reduced_matrix.getIds();
                            projection = this.mergeMatrices(projection1, current_reduced_matrix.getIds(), fixed_points_projection, aux);
                        }
                        createGraph(tproj, projection, current_full_matrix, corpus, years[i]);
                    }

                    previous_full_matrix = current_full_matrix;


                    System.err.println("---------------------");
                }

                count++;
                setViewStatus("Projecting year " + years[i] + "...", (int) increment * count);

            }

            //projetando o primeiro ano
            ids_remove = Utils.toArrayList(corpus.getDocumentsIds(years[1]));
            ids_fixed = Utils.toArrayList(previous_full_matrix.getIds());
            ids_fixed.removeAll(ids_remove);
            this.newDocuments.put(years[1], ids_remove);
            this.fixedDocuments.put(years[1], ids_fixed);
            current_full_matrix = this.getMatrix(previous_full_matrix, years[0], years[0]);
            projection_cp = new double[ids_fixed.size()][2];
            int index_old;
            for (int j = 0; j < ids_fixed.size(); j++) {
                index_old = previous_full_matrix.getIndexWithId(ids_fixed.get(j));
                projection_cp[j][0] = projection[index_old][0];
                projection_cp[j][1] = projection[index_old][1];
            }
            createGraph(tproj, projection_cp, current_full_matrix, corpus, years[0]);
            setViewStatus("Projecting year " + years[0] + "...", 80);
        } catch (Exception ex) {
            Logger.getLogger(TemporalGraphBuilder.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private double[][] mergeMatrices(double[][] projection1, int[] ids1, double[][] projection2, int[] ids2) {
        double[][] output_matrix = new double[ids1.length + ids2.length][2];
        int[] ids_total = new int[ids1.length + ids2.length];
        System.arraycopy(ids1, 0, ids_total, 0, ids1.length);
        System.arraycopy(ids2, 0, ids_total, ids1.length, ids2.length);
        Arrays.sort(ids_total);
        for (int i = 0; i < ids_total.length; i++) {
            int index = Arrays.binarySearch(ids1, ids_total[i]);
            if (index >= 0) { //está na primeira matriz
                output_matrix[i] = projection1[index];
            } else {
                output_matrix[i] = projection2[Arrays.binarySearch(ids2, ids_total[i])];
            }
        }
        return output_matrix;
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
        this.graphs.put(graph.getYear(), graph);

        double increment = 1.0 / tproj.getNumberOfYears();

        TIntObjectHashMap<Vertex> vertex = graph.getVertex();
        Vertex v;
        for (int i = 0; i < projection.length; i++) {
            v = new Vertex(matrix.getRowWithIndex(i).getId(), projection[i][0], projection[i][1]);
            v.setScalar(sdots, 0.0f);
            v.setPublishedYear(corpus.getYear(v.getId()));
            v.setScalar(sclass, corpus.getClass(v.getId()));
            v.setScalar(syear, Utils.indexOf(tproj.getYears(), v.getPublishedYear()) * increment);
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

        for (double[] projection1 : projection) {
            if (maxX < projection1[0]) {
                maxX = projection1[0];
            } else {
                if (minX > projection1[0]) {
                    minX = projection1[0];
                }
            }
            if (maxY < projection1[1]) {
                maxY = projection1[1];
            } else {
                if (minY > projection1[1]) {
                    minY = projection1[1];
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

    private void normalizeGraphs(TreeMap<Integer, TemporalGraph> graphs) {
        java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        for (TemporalGraph graph : graphs.values()) {
            graph.normalizeVertex(Vertex.getRayBase() * 3, d.getHeight() / 1.40f - 40);
        }
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
        Entry<Integer, TemporalGraph> entry;
        TemporalGraph graph1, graph2;
        double incrementProgressBar = 20 / this.graphs.size();
        int count = 0;

        for (int i = 0; i < tproj.getNumberOfYears(); i++) {
            year = tproj.getYearWithIndex(i);
            entry = graphs.lowerEntry(year);
            graph2 = graphs.get(year);
            if (entry != null) {
                graph1 = entry.getValue();

                count++;
                setViewStatus("Creating animation...", (int) incrementProgressBar * count + 80);
                this.createIntermediateGraphs(year, graph1, graph2, this.newDocuments.get(year), this.updatedDocuments.get(year), this.fixedDocuments.get(year), this.usedCPDocuments.get(year));
            } else {
                TemporalGraph graph;
                TIntObjectHashMap< Vertex> vertex;
                Vertex newVertex;
                double increment = 1.0 / tproj.getNumberOfYears();
                ArrayList<TemporalGraph> graphsAux = new ArrayList<>();
                for (int j = 0; j < TemporalProjection.getN(); j++) {
                    graph = new TemporalGraph(tproj, year, j);
//                    if (i == years.length-1 && j == TemporalProjection.getN() - 1) {
//                        graph = new TemporalGraph(tproj, years[i + 1], true);
//                    }
                    vertex = new TIntObjectHashMap<>();
                    TIntObjectIterator<Vertex> iterator = graph2.getVertex().iterator();
                    while (iterator.hasNext()) {
                        iterator.advance();
                        if (j != 0) {
                            Vertex oldVertex = iterator.value();
                            int id = oldVertex.getId();
                            newVertex = new Vertex(id, oldVertex.getX(), oldVertex.getY());
                            newVertex.setScalar(sdots, 0.0f);
                            newVertex.setScalar(sclass, corpus.getClass(id));
                            newVertex.setPublishedYear(oldVertex.getPublishedYear());
                            newVertex.setScalar(syear, Utils.indexOf(tproj.getYears(), newVertex.getPublishedYear()) * increment);
                            newVertex.setScalar(scorecitations, oldVertex.getScalar(scorecitations, false));
                            newVertex.setScalar(stimescited, oldVertex.getScalar(stimescited, false));
                            newVertex.setScalar(this.sscheme, 0.0f);
                            newVertex.setAlpha((1.0f / TemporalProjection.getN()) * j);
                            newVertex.setTemporalProjection(tproj);
                            vertex.put(id, newVertex);
                        }
                    }
                    graph.setVertex(vertex);
                    graphsAux.add(graph);
                }
                this.tproj.addGraphs(year, graphsAux);

            }
        }
    }

    private void createIntermediateGraphs(Integer year, TemporalGraph graph1, TemporalGraph graph2, TIntArrayList newDocuments, TIntArrayList updatedDocuments, TIntArrayList fixedDocuments, TIntArrayList usedControlPoints) {
        ArrayList<TemporalGraph> graphsAux = new ArrayList<>();
        TemporalGraph graph;
        TIntObjectHashMap< Vertex> vertex;
        Vertex newVertex = null, v1, v2;
        double increment = 1.0 / tproj.getNumberOfYears();
        for (int i = 0; i < TemporalProjection.getN(); i++) {
            graph = new TemporalGraph(tproj, year, i);
            vertex = new TIntObjectHashMap<>();
            for (TIntIterator it = fixedDocuments.iterator(); it.hasNext();) {
                int id = it.next();
                v1 = graph1.getVertexById(id);
                v2 = graph2.getVertexById(id);
                double x1 = v1.getX();
                double y1 = v1.getY();
                double x2 = v2.getX();
                double y2 = v2.getY();
                double x, y;
                if (y1 != y2) {
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
                if (usedControlPoints != null && usedControlPoints.contains(id)) {
                    newVertex.setScalar(sscheme, 0.75f);
                } else {
                    newVertex.setScalar(sscheme, 1.0f);
                }

                newVertex.setTemporalProjection(tproj);
                vertex.put(id, newVertex);
            }
            for (TIntIterator it1 = newDocuments.iterator(); it1.hasNext();) {
                int id = it1.next();
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
                for (TIntIterator it2 = updatedDocuments.iterator(); it2.hasNext();) {
                    int id = it2.next();
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

    private Pair[][] getMesh(SparseMatrix matrix, Dissimilarity diss, int numberNeighborsConnection) {
        Pair[][] mesh = null;
        try {
            KNN ann = new KNN(numberNeighborsConnection);
            mesh = (new MeshGenerator()).execute(ann.execute(matrix, diss), matrix, diss);
        } catch (IOException ex) {
            Logger.getLogger(TemporalGraphBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mesh;
    }

    private SparseMatrix getMatrix(SparseMatrix omatrix, int begin_year, int end_year, TIntArrayList update, TIntArrayList control_points) throws IOException {
        SparseMatrix nmatrix = new SparseMatrix(update.size() + control_points.size());
        for (TIntIterator it = corpus.getDocumentsIdsFromYearToYear(begin_year, end_year).iterator(); it.hasNext();) {
            int id = it.next();
            if (update.contains(id) || control_points.contains(id)) {
                nmatrix.addRow(omatrix.getRowWithId(id));
            }
        }
        return nmatrix;
    }

    private SparseMatrix getMatrix(SparseMatrix omatrix, int begin_year, int end_year) throws IOException {
        TIntArrayList ids = corpus.getDocumentsIdsFromYearToYear(begin_year, end_year);
        SparseMatrix nmatrix = new SparseMatrix(ids.size());
        for (TIntIterator it = ids.iterator(); it.hasNext();) {
            nmatrix.addRow(omatrix.getRowWithId(it.next()));
        }
        return nmatrix;
    }

    private void chooseAdditionalControlPointsOutsideRegion(Geometry region, TreeMap<Integer, TIntArrayList> update, TIntArrayList updateSelected, TIntArrayList fixed, SparseMatrix omatrix, double[][] projection, double[][] projection_cp) {
        TIntArrayList neighboors;
        ArrayList<ControlPointCandidate> candidates;
        GeometryFactory geometryFactory = new GeometryFactory();
        while (fixed.size() < min_cp + 1) {
            int vertex_index, neighboor_index;
            double x_vertex, y_vertex;
            double sum;
            candidates = new ArrayList<>();
            for (TIntIterator it1 = updateSelected.iterator(); it1.hasNext();) {
                int vertex = it1.next();
                vertex_index = Utils.indexOf(omatrix.getIds(), vertex);
                x_vertex = projection[vertex_index][0];
                y_vertex = projection[vertex_index][1];
                if (!region.contains(geometryFactory.createPoint(new Coordinate(x_vertex, y_vertex)))) {
                    sum = 0;
                    neighboors = update.get(vertex);
                    for (TIntIterator it = neighboors.iterator(); it.hasNext();) {
                        neighboor_index = Utils.indexOf(omatrix.getIds(), it.next());
                        sum += Math.sqrt(Math.pow(x_vertex - projection[neighboor_index][0], 2) + Math.pow(y_vertex - projection[neighboor_index][1], 2));
                    }
                    candidates.add(new ControlPointCandidate(vertex, sum));
                }
            }
            Collections.sort(candidates, new Comparator<ControlPointCandidate>() {
                @Override
                public int compare(ControlPointCandidate o1, ControlPointCandidate o2) {
                    return Double.compare(o2.value, o1.value);
                }
            });
            for (Iterator<ControlPointCandidate> it = candidates.iterator(); it.hasNext();) {
                if (fixed.size() < min_cp + 1) {
                    fixed.add(it.next().id);
                } else {
                    break;
                }
            }
            updateSelected.removeAll(fixed);
        }
        min_cp = fixed.size();
    }

    private void chooseAdditionalControlPoints(TreeMap<Integer, TIntArrayList> update, TIntArrayList updateSelected, TIntArrayList fixed, SparseMatrix omatrix, double[][] projection) {
        ArrayList<ControlPointCandidate> candidates;
        while (fixed.size() < min_cp) {
            int vertex_index, neighboor_index;
            double x_vertex, y_vertex, x_neighboor, y_neighboor;
            double sum;
            candidates = new ArrayList<>();
            for (TIntIterator it1 = updateSelected.iterator(); it1.hasNext();) {
                int vertex_id = it1.next();
                vertex_index = omatrix.getIndexWithId(vertex_id);
                x_vertex = projection[vertex_index][0];
                y_vertex = projection[vertex_index][1];
                sum = 0;
                for (TIntIterator it = update.get(vertex_id).iterator(); it.hasNext();) {
                    neighboor_index = omatrix.getIndexWithId(it.next());
                    x_neighboor = projection[neighboor_index][0];
                    y_neighboor = projection[neighboor_index][1];
                    sum += Math.sqrt(Math.pow(x_vertex - x_neighboor, 2) + Math.pow(y_vertex - y_neighboor, 2));

                }
                candidates.add(new ControlPointCandidate(vertex_id, sum));
            }
            Collections.sort(candidates, new Comparator<ControlPointCandidate>() {
                @Override
                public int compare(ControlPointCandidate o1, ControlPointCandidate o2) {
                    return Double.compare(o2.value, o1.value);
                }
            });
            for (Iterator<ControlPointCandidate> it = candidates.iterator(); it.hasNext();) {
                if (fixed.size() < min_cp) {
                    fixed.add(it.next().id);
                } else {
                    break;
                }
            }
            updateSelected.removeAll(fixed);
        }
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
    private void saveData(ProjectionData pdata, SparseMatrix matrix, DatabaseCorpus corpus) throws Exception {
        //saving the points matrix
        if (matrix != null && pdata.getDocsTermsFilename().trim().length() > 0) {
            matrix.save(pdata.getDocsTermsFilename());
        }

        if (corpus != null && pdata.getPExFilename().trim().length() > 0) {
            corpus.saveToPExFormat(pdata.getPExFilename(), pdata.isIndividualFilesToPExFormat(), pdata.getYearStepToPExFormat(), pdata);
        }
    }

    private void setViewStatus(String status, int value) {
        if (view != null) {
            view.setStatus(status, value);
        }
    }
}
