/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.projection;

import java.util.List;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeriesCollection;
import topicevolutionvis.database.DatabaseCorpus;
import topicevolutionvis.dimensionreduction.DimensionalityReductionType;
import topicevolutionvis.dimensionreduction.lda.LDAOutput;
import topicevolutionvis.matrix.SparseMatrix;
import topicevolutionvis.matrix.normalization.NormalizationType;
import topicevolutionvis.preprocessing.RepresentationType;
import topicevolutionvis.preprocessing.steemer.StemmerType;
import topicevolutionvis.preprocessing.transformation.MatrixTransformationType;
import topicevolutionvis.projection.distance.DissimilarityType;
import topicevolutionvis.projection.distance.kolmogorov.CompressorType;
import topicevolutionvis.projection.lsp.ControlPointsType;

/**
 *
 * @author Aretha
 */
public class ProjectionData implements Cloneable {

    private String collectionName = null;
    private DatabaseCorpus corpus = null;
    private String sourceFile = null;
    //cuts for terms
    private int lunhLowerCut = 10;
    private int lunhUpperCut = -1;
    //cuts for reerences
    private int referencesLowerCut = -1;
    private int referencesUpperCut = -1;
    //pre-processing
    private boolean removeStopwordsUsingTagging = false;
    private int numberGrams = 1;
    private StemmerType.Type stemmer = StemmerType.Type.PORTER;
    private boolean useStopword = true;
    private boolean useWeight = false;
    //normalization
    private NormalizationType.Type normalization = NormalizationType.Type.NONE;
    private MatrixTransformationType mattype = MatrixTransformationType.TF_IDF;
    private DissimilarityType distanceType = DissimilarityType.COSINE_BASED;
    //indicates the type of data used to create the projection
    private int numberDimensions = 0;
    private boolean include_references_bow = false;
    private boolean PEXFORMAT_saveTitle = true;
    private boolean PEXFORMAT_saveAuthors = true;
    private boolean PEXFORMAT_saveAbstract = true;
    private boolean PEXFORMAT_saveKeywords = true;
    private boolean PEXFORMAT_saveReferences = true;
    private boolean individualFilesToPExFormat = false;
    private int yearStepForSavingToPExFormat = 0;
    private SparseMatrix matrix;
    private int numberOfDocuments = 0;
    private int numberLines = 0;
    private int knnNumberNeighbors = 1;
    private boolean createDelaunay = true;
    private boolean createBibliographicCoupling = true;
    private String description = "";
    private String dmatFilename = "";
    private String docsTermsFilename = "";
    private String pexFilename = "";
    private String scalarFilename = "";
    private int targetDimension = 0; //target dimension to reduce
    private RepresentationType representationType = RepresentationType.VECTOR_SPACE_MODEL;
    private DimensionalityReductionType dimenType = DimensionalityReductionType.NONE;
    private ProjectorType projector = ProjectorType.NONE;
    private CompressorType comptype = CompressorType.BZIP2;
    //projection techqnique used
    private ProjectionType projTech = ProjectionType.NONE;
    //Temporal Projection Parameters
    private float fractionDelta = 8.0f;
    private int numberIterations = 50;
    private int numberControlPoint = 10;
    private int numberNeighborsConnection = 10;
    private ControlPointsType controlPointsChoice = ControlPointsType.KMEDOIDS;
    //DBSCAN and MONIC
    private boolean topicEvolutionGenerated = false;
    private double epsilon = 0.07d;
    private int minPoints = 4;
    private double theta = 0.5d;
    private double theta_split = 0.3d;
    //Stress
    private XYSeriesCollection stress_series = new XYSeriesCollection();
    private float static_stress = 0.0f;
    private float dynamic_stress = 0.0f;
    private long time = 0;
    //LDA
    private LDAOutput ldaOutput = null;
    private int numberOfTopics = 300;
    private double alpha = 0.16f;
    private double beta = 0.01f;
    private int numberOfLDAIterations = 1000;
    private double ldaModelLogLikelihood = 0;
    private double LLToken = 0;

    public DatabaseCorpus getDatabaseCorpus() {
        return this.corpus;
    }

    public void setDatabaseCorpus(DatabaseCorpus corpus) {
        this.corpus = corpus;
    }

    public String getCollectionName() {
        return this.collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public CompressorType getCompressorType() {
        return comptype;
    }

    public void setCompressorType(CompressorType comptype) {
        this.comptype = comptype;
    }

    public int getNumberGrams() {
        return numberGrams;
    }

    public void setNumberGrams(int numberGrams) {
        this.numberGrams = numberGrams;
    }

    public int getLunhLowerCut() {
        return lunhLowerCut;
    }

    public void setLunhLowerCut(int lunhLowerCut) {
        this.lunhLowerCut = lunhLowerCut;
    }

    public int getLunhUpperCut() {
        return lunhUpperCut;
    }

    public void setLunhUpperCut(int lunhUpperCut) {
        this.lunhUpperCut = lunhUpperCut;
    }

    public int getReferencesLowerCut() {
        return this.referencesLowerCut;
    }

    public void setReferencesLowerCut(int referencesLowerCut) {
        this.referencesLowerCut = referencesLowerCut;
    }

    public int getReferencesUpperCut() {
        return this.referencesUpperCut;
    }

    public void setReferencesUpperCut(int referencesUpperCut) {
        this.referencesUpperCut = referencesUpperCut;
    }

    public StemmerType.Type getStemmer() {
        return stemmer;
    }

    public void setStemmer(StemmerType.Type stemmer) {
        this.stemmer = stemmer;
    }

    public boolean isUseStopword() {
        return useStopword;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public DissimilarityType getDissimilarityType() {
        return distanceType;
    }

    public void setDissimilarityType(DissimilarityType distanceType) {
        this.distanceType = distanceType;
    }

    public void setNumberOfDocuments(int numberObjects) {
        this.numberOfDocuments = numberObjects;
    }

    public int getNumberOfDocuments() {
        return numberOfDocuments;
    }

    public void setUseWeight(boolean useWeight) {
        this.useWeight = useWeight;
    }

    public void setUseStopword(boolean useStopword) {
        this.useStopword = useStopword;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public double getEpsilon() {
        return this.epsilon;
    }

    public void setMinPoint(int minPoints) {
        this.minPoints = minPoints;
    }

    public int getMinPoints() {
        return this.minPoints;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }

    public double getTheta() {
        return this.theta;
    }

    public void setThetaSplit(double theta_split) {
        this.theta_split = theta_split;
    }

    public double getThetaSplit() {
        return this.theta_split;
    }

    public int getNumberOfTopics() {
        return this.numberOfTopics;
    }

    public void setNumberOfTopics(int numberOfTopics) {
        this.numberOfTopics = numberOfTopics;
    }

    public boolean isTopicEvolutionGenerated() {
        return topicEvolutionGenerated;
    }

    public void setTopicEvolutionGenerated(boolean topicEvolutionGenerated) {
        this.topicEvolutionGenerated = topicEvolutionGenerated;
    }
    
    public double getAlpha() {
        return this.alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getBeta() {
        return this.beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public int getNumberOfLDAIterations() {
        return this.numberOfLDAIterations;
    }

    public void setNumberOfLDAIterations(int numberOfLDAIterations) {
        this.numberOfLDAIterations = numberOfLDAIterations;
    }

    public int getNumberDimensions() {
        return numberDimensions;
    }

    public void setNumberDimensions(int numberDimensions) {
        this.numberDimensions = numberDimensions;
    }

    public int getNumberControlPoints() {
        return numberControlPoint;
    }

    public void setNumberControlPoints(int numberControlPoint) {
        this.numberControlPoint = numberControlPoint;
    }

    public MatrixTransformationType getMatrixTransformationType() {
        return mattype;
    }

    public void setMatrixTransformationType(MatrixTransformationType mattype) {
        this.mattype = mattype;
    }

    public SparseMatrix getMatrix() {
        return matrix;
    }

    public void setMatrix(SparseMatrix matrix) {
        this.matrix = matrix;
    }

    public ProjectorType getProjectorType() {
        return this.projector;
    }

    public void setProjectorType(ProjectorType projector) {
        this.projector = projector;
    }

    public ProjectionType getProjectionType() {
        return this.projTech;
    }

    public void setProjectionType(ProjectionType projTech) {
        this.projTech = projTech;
    }

    public int getNumberIterations() {
        return numberIterations;
    }

    public void setNumberIterations(int numberIterations) {
        this.numberIterations = numberIterations;
    }

    public int getNumberNeighborsConnection() {
        return numberNeighborsConnection;
    }

    public void setNumberNeighborsConnection(int numberNeighborsConnection) {
        this.numberNeighborsConnection = numberNeighborsConnection;
    }

    public ControlPointsType getControlPointsChoice() {
        return controlPointsChoice;
    }

    public void setControlPointsChoice(ControlPointsType controlPointsChoice) {
        this.controlPointsChoice = controlPointsChoice;
    }

    public void setLDAMatrices(LDAOutput ldaMatrices) {
        this.ldaOutput = ldaMatrices;
    }

    public LDAOutput getLDAMatrices() {
        return this.ldaOutput;
    }

    public boolean hasLDAOutput() {
        if (this.ldaOutput != null) {
            return true;
        }
        return false;
    }

    public float getFractionDelta() {
        return fractionDelta;
    }

    public void setFractionDelta(float fractionDelta) {
        this.fractionDelta = fractionDelta;
    }

    public NormalizationType.Type getNormalization() {
        return normalization;
    }

    public void setNormalization(NormalizationType.Type normalization) {
        this.normalization = normalization;
    }

    public boolean getIncludeReferencesInBOW() {
        return this.include_references_bow;
    }

    public void setIncludeReferencesInBOW(boolean include_references_bow) {
        this.include_references_bow = include_references_bow;
    }

    public void setSaveTitleToPExFormat(boolean value) {
        this.PEXFORMAT_saveTitle = value;
    }

    public boolean getSaveTitleToPExFormat() {
        return this.PEXFORMAT_saveTitle;
    }

    public void setSaveAuthorsToPExFormat(boolean value) {
        this.PEXFORMAT_saveAuthors = value;
    }

    public boolean getSaveAuthorsToPExFormat() {
        return this.PEXFORMAT_saveAuthors;
    }

    public void setSaveAbstractToPExFormat(boolean value) {
        this.PEXFORMAT_saveAbstract = value;
    }

    public boolean getSaveAbstractToPExFormat() {
        return this.PEXFORMAT_saveAbstract;
    }

    public void setSaveKeywordsToPExFormat(boolean value) {
        this.PEXFORMAT_saveKeywords = value;
    }

    public boolean getSaveKeywordsToPExFormat() {
        return this.PEXFORMAT_saveKeywords;
    }

    public void setSaveReferencesToPExFormat(boolean value) {
        this.PEXFORMAT_saveReferences = value;
    }

    public boolean getSaveReferencesToPExFormat() {
        return this.PEXFORMAT_saveReferences;
    }

    public int getKnnNumberNeighbors() {
        return knnNumberNeighbors;
    }

    public void setKnnNumberNeighbors(int knnNumberNeighbors) {
        this.knnNumberNeighbors = knnNumberNeighbors;
    }

    public boolean isCreateDelaunay() {
        return createDelaunay;
    }

    public boolean isCreateBibliographicCoupling() {
        return this.createBibliographicCoupling;
    }

    public int getNumberLines() {
        return numberLines;
    }

    public void setNumberLines(int numberLines) {
        this.numberLines = numberLines;
    }

    public void setCreateDelaunay(boolean createDelaunay) {
        this.createDelaunay = createDelaunay;
    }

    public String getDocsTermsFilename() {
        return docsTermsFilename;
    }

    public void setDocsTermsFilename(String docsTermsFilename) {
        this.docsTermsFilename = docsTermsFilename;
    }

    public void setIndividualFilesToPExFormat(boolean value) {
        this.individualFilesToPExFormat = value;
    }

    public boolean isIndividualFilesToPExFormat() {
        return this.individualFilesToPExFormat;
    }

    public void setYearStepToPExFormat(int value) {
        this.yearStepForSavingToPExFormat = value;
    }

    public int getYearStepToPExFormat() {
        return this.yearStepForSavingToPExFormat;
    }

    public void setPExFilename(String pexFilename) {
        this.pexFilename = pexFilename;
    }

    public String getPExFilename() {
        return this.pexFilename;
    }

    public void setScalarFilename(String scalarFilename) {
        this.scalarFilename = scalarFilename;
    }

    public String getScalarFilename() {
        return this.scalarFilename;
    }

    public String getDistanceMatrixFilename() {
        return dmatFilename;
    }

    public void setDistanceMatrixFilename(String dmatFilename) {
        this.dmatFilename = dmatFilename;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DimensionalityReductionType getDimensionReductionType() {
        return dimenType;
    }

    public void setDimensionReductionType(DimensionalityReductionType dimenType) {
        this.dimenType = dimenType;
    }

    public void setRepresentationType(RepresentationType representationType) {
        this.representationType = representationType;
    }

    public RepresentationType getRepresentationType() {
        return this.representationType;
    }

    public int getTargetDimension() {
        return targetDimension;
    }

    public void setTargetDimension(int targetDimension) {
        this.targetDimension = targetDimension;
    }

    public XYSeriesCollection getStressSeries() {
        return this.stress_series;
    }

    public void setStressSeries(XYSeriesCollection stress_series) {
        this.stress_series = stress_series;


        this.static_stress = 0;
        for (XYDataItem item : (List<XYDataItem>) stress_series.getSeries(0).getItems()) {
            static_stress += item.getYValue();
        }

        this.dynamic_stress = 0;
        for (XYDataItem item : (List<XYDataItem>) stress_series.getSeries(1).getItems()) {
            dynamic_stress += item.getYValue();
        }
    }

    public float getStaticStress() {
        return this.static_stress;
    }

    public float getDynamicStress() {
        return this.dynamic_stress;
    }

    public float getTotalStress() {
        return this.static_stress + this.dynamic_stress;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return this.time;
    }

    public boolean usetStopwordRemovalByTagging() {
        return this.removeStopwordsUsingTagging;
    }

    public void setStopwordsRemovalByTaggin(boolean value) {
        this.removeStopwordsUsingTagging = value;
    }

    public void setLdaModelLogLikelihood(double value) {
        this.ldaModelLogLikelihood = value;
    }

    public double getldaModelLogLikelihood() {
        return this.ldaModelLogLikelihood;
    }

    public void setLLToken(double value) {
        this.LLToken = value;
    }

    public double getLLToken() {
        return this.LLToken;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ProjectionData newPdata = new ProjectionData();
        newPdata.comptype = this.comptype;
        newPdata.controlPointsChoice = this.controlPointsChoice;
        newPdata.createDelaunay = this.createDelaunay;
        newPdata.description = this.description;
        newPdata.dimenType = this.dimenType;
        newPdata.distanceType = this.distanceType;
        newPdata.dmatFilename = this.dmatFilename;
        newPdata.docsTermsFilename = this.docsTermsFilename;
        newPdata.fractionDelta = this.fractionDelta;
        newPdata.knnNumberNeighbors = this.knnNumberNeighbors;
        newPdata.lunhLowerCut = this.lunhLowerCut;
        newPdata.lunhUpperCut = this.lunhUpperCut;
        newPdata.matrix = this.matrix;
        newPdata.mattype = this.mattype;
        newPdata.normalization = this.normalization;
        newPdata.numberControlPoint = this.numberControlPoint;
        newPdata.numberDimensions = this.numberDimensions;
        newPdata.numberGrams = this.numberGrams;
        newPdata.numberIterations = this.numberIterations;
        newPdata.numberLines = this.numberLines;
        newPdata.numberNeighborsConnection = this.numberNeighborsConnection;
        newPdata.numberOfDocuments = this.numberOfDocuments;
        newPdata.projTech = this.projTech;
        newPdata.projector = this.projector;
        newPdata.sourceFile = this.sourceFile;
        newPdata.stemmer = this.stemmer;
        newPdata.targetDimension = this.targetDimension;
        newPdata.useStopword = this.useStopword;
        newPdata.useWeight = this.useWeight;
        newPdata.collectionName = this.collectionName;
        return newPdata;
    }
}
