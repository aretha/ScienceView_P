/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.topic;

/**
 *
 * @author Aretha
 */
public class TopicData {

    public enum TopicType {

        COVARIANCE, PCA, LDA
    }

    public enum TopicVisualization {

        SIMPLE, TAGCLOUD
    }
    
    private int nextAvailableId = 1;
    //parameters for extraction of topics by covariance
    private float covariancePercentageTopics = 0.75f;
    private float covariancePercentageTerms = 0.5f;
    //parameters for extraction of topics by covariance
    private float pcaMinInformationTerms = 0.5f;
    private float pcaInformationTopics = 0.5f;
    private float ldaMinInformationTerms = 0.25f;
    private float ldaInformationTopics = 0.5f;
    private int ldaNumberOfTopics = 100;
    private int ldaNumberOfIterations = 1000;
    private double ldaAlpha = 0.1d;
    private double ldaBeta = 0.01d;
    private TopicType topicType = TopicType.COVARIANCE; //The type of topic to create
    private TopicVisualization topicVisualization = TopicVisualization.SIMPLE; //type of topic visualization
    private int maxNumberOfTermsTagCloud = 10;

    public TopicData() {
    }

    public TopicType getTopicType() {
        return topicType;
    }

    public void setTopicType(TopicType topicType) {
        this.topicType = topicType;
    }

    public TopicVisualization getTypeOfTopicVisualization() {
        return topicVisualization;
    }

    public void setTypeOfTopicVisualization(TopicVisualization topicVisualization) {
        this.topicVisualization = topicVisualization;
    }

    public int getNextAvailableId() {
        int id = this.nextAvailableId;
        this.nextAvailableId++;
        return id;
    }

    public void setNextAvailableId(int nextAvailableId) {
        this.nextAvailableId = nextAvailableId;
    }

    public float getCovariancePercentageTerms() {
        return covariancePercentageTerms;
    }

    public void setCovariancePercentageTerms(float value) {
        this.covariancePercentageTerms = value;
    }

    public float getCovariancePercentageTopics() {
        return covariancePercentageTopics;
    }

    public void setCovariancePercentageTopics(float value) {
        this.covariancePercentageTopics = value;
    }

    public float getPcaMinInformationTerms() {
        return this.pcaMinInformationTerms;
    }

    public void setPcaMinInformationTerms(float value) {
        this.pcaMinInformationTerms = value;
    }

    public float getLDAMinInformationTerms() {
        return this.ldaMinInformationTerms;
    }

    public void setLDAMinInformationTerms(float value) {
        this.ldaMinInformationTerms = value;
    }

    public float getPcaInformationTopics() {
        return this.pcaInformationTopics;
    }

    public void setPcaInformationTopics(float value) {
        this.pcaInformationTopics = value;
    }

    public float getLDAInformationTopics() {
        return this.ldaInformationTopics;
    }

    public void setLDAInformationTopics(float value) {
        this.ldaInformationTopics = value;
    }

    public int getLdaNumberOfTopics() {
        return ldaNumberOfTopics;
    }

    public void setLdaNumberOfTopics(int ldaNumberOfTopics) {
        this.ldaNumberOfTopics = ldaNumberOfTopics;
    }

    public int getLdaNumberOfIterations() {
        return ldaNumberOfIterations;
    }

    public void setLdaNumberOfIterations(int ldaNumberOfIterations) {
        this.ldaNumberOfIterations = ldaNumberOfIterations;
    }

    public double getLdaAlpha() {
        return ldaAlpha;
    }

    public void setLdaAlpha(double ldaAlpha) {
        this.ldaAlpha = ldaAlpha;
    }

    public double getLdaBeta() {
        return ldaBeta;
    }

    public void setLdaBeta(double ldaBeta) {
        this.ldaBeta = ldaBeta;
    }

    public void setMaxNumberOfTermsForTagCloud(int value) {
        this.maxNumberOfTermsTagCloud = value;
    }

    public int getMaxNumberOfTermsForTagCloud() {
        return this.maxNumberOfTermsTagCloud;
    }
}
