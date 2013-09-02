/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ReportView.java
 *
 * Created on Oct 21, 2010, 4:23:02 PM
 */
package topicevolutionvis.view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import topicevolutionvis.preprocessing.RepresentationType;
import topicevolutionvis.preprocessing.Stopwords;
import topicevolutionvis.projection.ProjectionData;
import topicevolutionvis.topic.TopicData;
import topicevolutionvis.topic.TopicData.TopicType;
import topicevolutionvis.util.SystemPropertiesManager;
import topicevolutionvis.utils.filefilter.TXTFileFilter;

/**
 *
 * @author barbosaa
 */
public class ReportView extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;
    private ProjectionData pdata = null;
    private TopicData tdata = null;

    /**
     * Creates new form ReportView
     */
    public ReportView() {
        initComponents();
    }

    public void reset(ProjectionData pdata, TopicData tdata) {
        this.pdata = pdata;
        this.tdata = tdata;

        //collection name
        this.sourceTextField.setText(pdata.getDatabaseCorpus().getCollectionFilename());

        //data
        this.numberDimensionsTextField.setText(Integer.toString(pdata.getNumberDimensions()));
        this.numberObjectsTextField.setText(Integer.toString(pdata.getNumberOfDocuments()));
        this.numberReferencesTextField.setText(Integer.toString(pdata.getDatabaseCorpus().getNumberOfUniqueReferences()));

        //pre-processing
        this.numberGramsTextField.setText(Integer.toString(pdata.getNumberGrams()));
        try {
            this.stopwordsListTextField.setText(Stopwords.getInstance().getFilename());
        } catch (IOException ex) {
            Logger.getLogger(ReportView.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.removeStopwordsByTaggingCheckBox.setSelected(pdata.usetStopwordRemovalByTagging());
        this.removeStopwordsByTaggingCheckBox.setEnabled(false);


        //projection
        this.projTechniqueTextField.setText(pdata.getProjectionType().toString());
        this.distanceTypeTextField.setText(pdata.getDissimilarityType().toString());
        this.initialNumberOfControlPointsTextField.setText(Integer.toString(pdata.getNumberControlPoints()));
        this.connectionsTextField.setText(Integer.toString(pdata.getNumberNeighborsConnection()));

        //topic
        StringBuilder aux = new StringBuilder(50);
        if (tdata.getTopicType() == TopicType.COVARIANCE) {
            aux = aux.append("Covariance Topic Extraction (alpha=").append(tdata.getCovariancePercentageTopics()).append(", beta=").append(tdata.getCovariancePercentageTopics()).append(")");
        } else if (tdata.getTopicType() == TopicType.PCA) {
            aux = aux.append("PCA Topic Extraction (minTopics=").append(tdata.getPcaInformationTopics()).append(", minTerms=").append(tdata.getPcaMinInformationTerms()).append(")");
        } else { // LDA
            aux = aux.append("LDA Topic Extraction (minTopics=").append(tdata.getLDAInformationTopics()).append(", minTerms=").append(tdata.getLDAMinInformationTerms()).append(");");
            aux = aux.append("LDA Topic Model (#topics=").append(tdata.getLdaNumberOfTopics()).append(", #iterations=").append(tdata.getLdaNumberOfIterations()).append(", alpha=").append(tdata.getLdaAlpha()).append(", beta=").append(tdata.getLdaBeta()).append(")");
        }
        if (pdata.isTopicEvolutionGenerated()) {
            DecimalFormat df = new DecimalFormat("#.###");
            this.dbscanTextField.setText("epsilon=" + df.format(this.pdata.getEpsilon()) + "; minPts=" + this.pdata.getMinPoints());
            this.monicTextField.setText("theta=" + this.pdata.getTheta() + "; theta_split=" + this.pdata.getThetaSplit());
        } else {
            this.dbscanTextField.setText(null);
            this.monicTextField.setText(null);
        }
        this.topicExtratcionTextArea.setText(aux.toString());

        //projection evaluation
        this.static_stressTextField.setText(Float.toString(pdata.getStaticStress()));
        this.dynamic_stressTextField.setText(Float.toString(pdata.getDynamicStress()));
        this.total_stressTextField.setText(Float.toString(pdata.getTotalStress()));
        this.timeTextField.setText(Long.toString(pdata.getTime()));

        //Representation
        this.representationTypeTextField.setText(pdata.getRepresentationType().toString());
        StringBuilder param = new StringBuilder("");
        if (RepresentationType.VECTOR_SPACE_MODEL.equals(pdata.getRepresentationType())) {
            param.append("Luhn's Lower Cut: ").append(Integer.toString(pdata.getLunhLowerCut())).append("\n")
                    .append("Luhn's Upper Cut: ").append(Integer.toString(pdata.getLunhUpperCut()));
        } else if (RepresentationType.VECTOR_SPACE_REFERENCES.equals(pdata.getRepresentationType())) {
            param.append("Luhn's Lower Cut for Terms: ").append(Integer.toString(pdata.getLunhLowerCut())).append("\n")
                    .append("Luhn's Upper Cut for Terms: ").append(Integer.toString(pdata.getLunhUpperCut())).append("\n")
                    .append("Luhn's Lower Cut for References: ").append(Integer.toString(pdata.getReferencesLowerCut())).append("\n")
                    .append("Luhn's Upper Cut for References: ").append(Integer.toString(pdata.getReferencesUpperCut()));
        } else {
            param.append("Number of Topics: ").append(Integer.toString(pdata.getNumberOfTopics())).append("\n")
                    .append("Number of Iterations: ").append(Integer.toString(pdata.getNumberOfLDAIterations())).append("\n")
                    .append("Alpha: ").append(Double.toString(pdata.getAlpha())).append("\n")
                    .append("Beta: ").append(Double.toString(pdata.getBeta()));
        }
        this.representationParametersTextArea.setText(param.toString());

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        dataPanel = new javax.swing.JPanel();
        numberObjectsLabel = new javax.swing.JLabel();
        numberObjectsTextField = new javax.swing.JTextField();
        numberDimensionsLabel = new javax.swing.JLabel();
        numberDimensionsTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        numberReferencesTextField = new javax.swing.JTextField();
        preprocessingPanel = new javax.swing.JPanel();
        numberGramsLabel = new javax.swing.JLabel();
        numberGramsTextField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        stopwordsListTextField = new javax.swing.JTextField();
        removeStopwordsByTaggingCheckBox = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        static_stressTextField = new javax.swing.JTextField();
        dynamic_stressTextField = new javax.swing.JTextField();
        total_stressTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        timeTextField = new javax.swing.JTextField();
        projectionPanel = new javax.swing.JPanel();
        projTechniqueLabel = new javax.swing.JLabel();
        projTechniqueTextField = new javax.swing.JTextField();
        distanceTypeLabel = new javax.swing.JLabel();
        distanceTypeTextField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        initialNumberOfControlPointsTextField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        connectionsTextField = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        representationTypeTextField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        representationParametersTextArea = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        sourceLabel = new javax.swing.JLabel();
        sourceTextField = new javax.swing.JTextField();
        saveParametersButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        dbscanTextField = new javax.swing.JTextField();
        monicTextField = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        topicExtratcionTextArea = new javax.swing.JTextArea();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Collection Properties and Projection Parameters"));
        setLayout(new java.awt.GridBagLayout());

        dataPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Data"));
        dataPanel.setLayout(new java.awt.GridBagLayout());

        numberObjectsLabel.setText("Number of Documents:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        dataPanel.add(numberObjectsLabel, gridBagConstraints);

        numberObjectsTextField.setEditable(false);
        numberObjectsTextField.setColumns(5);
        numberObjectsTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        numberObjectsTextField.setMinimumSize(new java.awt.Dimension(46, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        dataPanel.add(numberObjectsTextField, gridBagConstraints);

        numberDimensionsLabel.setText("Number of Dimensions:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        dataPanel.add(numberDimensionsLabel, gridBagConstraints);

        numberDimensionsTextField.setEditable(false);
        numberDimensionsTextField.setColumns(5);
        numberDimensionsTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        numberDimensionsTextField.setMinimumSize(new java.awt.Dimension(46, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        dataPanel.add(numberDimensionsTextField, gridBagConstraints);

        jLabel1.setText("Number of unique references:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        dataPanel.add(jLabel1, gridBagConstraints);

        numberReferencesTextField.setEditable(false);
        numberReferencesTextField.setColumns(5);
        numberReferencesTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        dataPanel.add(numberReferencesTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(dataPanel, gridBagConstraints);

        preprocessingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Pre-processing"));
        preprocessingPanel.setLayout(new java.awt.GridBagLayout());

        numberGramsLabel.setText("Number of Grams:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        preprocessingPanel.add(numberGramsLabel, gridBagConstraints);

        numberGramsTextField.setEditable(false);
        numberGramsTextField.setColumns(10);
        numberGramsTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        numberGramsTextField.setMinimumSize(new java.awt.Dimension(46, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        preprocessingPanel.add(numberGramsTextField, gridBagConstraints);

        jLabel10.setText("Stopwords List:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        preprocessingPanel.add(jLabel10, gridBagConstraints);

        stopwordsListTextField.setEditable(false);
        stopwordsListTextField.setColumns(30);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        preprocessingPanel.add(stopwordsListTextField, gridBagConstraints);

        removeStopwordsByTaggingCheckBox.setText("Remove From Terms verbs, conjunctions ...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        preprocessingPanel.add(removeStopwordsByTaggingCheckBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(preprocessingPanel, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Projection Evaluation"));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText("Static Stress:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(jLabel2, gridBagConstraints);

        jLabel3.setText("Dynamic Stress:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(jLabel3, gridBagConstraints);

        jLabel4.setText("Total Stress:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(jLabel4, gridBagConstraints);

        static_stressTextField.setEditable(false);
        static_stressTextField.setColumns(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(static_stressTextField, gridBagConstraints);

        dynamic_stressTextField.setColumns(20);
        dynamic_stressTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(dynamic_stressTextField, gridBagConstraints);

        total_stressTextField.setColumns(20);
        total_stressTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(total_stressTextField, gridBagConstraints);

        jLabel5.setText("Time:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(jLabel5, gridBagConstraints);

        timeTextField.setColumns(20);
        timeTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        jPanel1.add(timeTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(jPanel1, gridBagConstraints);

        projectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Projection"));
        projectionPanel.setMinimumSize(new java.awt.Dimension(500, 108));
        projectionPanel.setPreferredSize(new java.awt.Dimension(500, 130));
        projectionPanel.setLayout(new java.awt.GridBagLayout());

        projTechniqueLabel.setText("Projection Technique:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        projectionPanel.add(projTechniqueLabel, gridBagConstraints);

        projTechniqueTextField.setEditable(false);
        projTechniqueTextField.setColumns(30);
        projTechniqueTextField.setMinimumSize(new java.awt.Dimension(300, 20));
        projTechniqueTextField.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        projectionPanel.add(projTechniqueTextField, gridBagConstraints);

        distanceTypeLabel.setText("Distance Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        projectionPanel.add(distanceTypeLabel, gridBagConstraints);

        distanceTypeTextField.setEditable(false);
        distanceTypeTextField.setColumns(20);
        distanceTypeTextField.setMinimumSize(new java.awt.Dimension(300, 20));
        distanceTypeTextField.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        projectionPanel.add(distanceTypeTextField, gridBagConstraints);

        jLabel8.setText("Initial Number of Control Points:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        projectionPanel.add(jLabel8, gridBagConstraints);

        initialNumberOfControlPointsTextField.setEditable(false);
        initialNumberOfControlPointsTextField.setColumns(30);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        projectionPanel.add(initialNumberOfControlPointsTextField, gridBagConstraints);

        jLabel9.setText("Number of connections:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        projectionPanel.add(jLabel9, gridBagConstraints);

        connectionsTextField.setEditable(false);
        connectionsTextField.setColumns(30);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        projectionPanel.add(connectionsTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(projectionPanel, gridBagConstraints);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Representation"));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel6.setText("Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jLabel6, gridBagConstraints);

        representationTypeTextField.setEditable(false);
        representationTypeTextField.setColumns(30);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(representationTypeTextField, gridBagConstraints);

        jLabel7.setText("Parameters:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jLabel7, gridBagConstraints);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(166, 96));

        representationParametersTextArea.setEditable(false);
        representationParametersTextArea.setBackground(new java.awt.Color(240, 240, 240));
        representationParametersTextArea.setColumns(30);
        representationParametersTextArea.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        representationParametersTextArea.setLineWrap(true);
        representationParametersTextArea.setRows(4);
        representationParametersTextArea.setWrapStyleWord(true);
        representationParametersTextArea.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jScrollPane1.setViewportView(representationParametersTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(jPanel2, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        sourceLabel.setText("Collection Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel3.add(sourceLabel, gridBagConstraints);

        sourceTextField.setEditable(false);
        sourceTextField.setColumns(40);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel3.add(sourceTextField, gridBagConstraints);

        saveParametersButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Save24.gif"))); // NOI18N
        saveParametersButton.setToolTipText("Save parameters...");
        saveParametersButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        saveParametersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveParametersButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 8, 3, 3);
        jPanel3.add(saveParametersButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(jPanel3, gridBagConstraints);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Topics"));
        jPanel4.setMinimumSize(new java.awt.Dimension(150, 130));
        jPanel4.setPreferredSize(new java.awt.Dimension(150, 130));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jLabel11.setText("Topic Extraction Technique:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel4.add(jLabel11, gridBagConstraints);

        jLabel12.setText("DBSCAN Parameterers:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel4.add(jLabel12, gridBagConstraints);

        jLabel13.setText("MONIC Parameters:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel4.add(jLabel13, gridBagConstraints);

        dbscanTextField.setEditable(false);
        dbscanTextField.setColumns(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel4.add(dbscanTextField, gridBagConstraints);

        monicTextField.setEditable(false);
        monicTextField.setColumns(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel4.add(monicTextField, gridBagConstraints);

        jScrollPane2.setMinimumSize(new java.awt.Dimension(150, 40));

        topicExtratcionTextArea.setEditable(false);
        topicExtratcionTextArea.setBackground(new java.awt.Color(240, 240, 240));
        topicExtratcionTextArea.setColumns(30);
        topicExtratcionTextArea.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        topicExtratcionTextArea.setLineWrap(true);
        topicExtratcionTextArea.setRows(3);
        topicExtratcionTextArea.setWrapStyleWord(true);
        topicExtratcionTextArea.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        topicExtratcionTextArea.setMinimumSize(new java.awt.Dimension(244, 100));
        jScrollPane2.setViewportView(topicExtratcionTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel4.add(jScrollPane2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(jPanel4, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    public void saveParametersInFile(String filename) {
        if (pdata != null) {
            BufferedWriter writer = null;
            try {
                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                writer = new BufferedWriter(new FileWriter(filename));
                writer.write(dateFormat.format(date));
                writer.newLine();
                writer.newLine();
                writer.write("File: " + pdata.getSourceFile());
                writer.newLine();
                writer.write("Database name: " + pdata.getCollectionName());
                writer.newLine();
                writer.write("Number of documents: " + pdata.getNumberOfDocuments());
                writer.newLine();
                writer.write("Number of dimensions: " + pdata.getNumberDimensions());


                writer.newLine();
                writer.newLine();
                writer.write("PREPROCESSING");
                writer.newLine();
                writer.write("Stopwords file: " + Stopwords.getInstance().getFilename());


                writer.newLine();
                writer.newLine();
                writer.write("REPRESENTATION");
                writer.newLine();
                writer.write("Type: " + pdata.getRepresentationType());
                writer.newLine();

                StringBuilder param = new StringBuilder("");
                if (RepresentationType.VECTOR_SPACE_MODEL.equals(pdata.getRepresentationType())) {
                    param.append("Luhn's Lower Cut: ").append(Integer.toString(pdata.getLunhLowerCut())).append("\n")
                            .append("Luhn's Upper Cut: ").append(Integer.toString(pdata.getLunhUpperCut()));
                } else if (RepresentationType.VECTOR_SPACE_REFERENCES.equals(pdata.getRepresentationType())) {
                    param.append("Luhn's Lower Cut for Terms: ").append(Integer.toString(pdata.getLunhLowerCut())).append("\n")
                            .append("Luhn's Upper Cut for Terms: ").append(Integer.toString(pdata.getLunhUpperCut())).append("\n")
                            .append("Luhn's Lower Cut for References: ").append(Integer.toString(pdata.getReferencesLowerCut())).append("\n")
                            .append("Luhn's Upper Cut for References: ").append(Integer.toString(pdata.getReferencesUpperCut()));
                } else {
                    param.append("Luhn's Lower Cut for Terms: ").append(Integer.toString(pdata.getLunhLowerCut())).append("\n")
                            .append("Luhn's Upper Cut for Terms: ").append(Integer.toString(pdata.getLunhUpperCut())).append("\n")
                            .append("Number of Topics: ").append(Integer.toString(pdata.getNumberOfTopics())).append("\n")
                            .append("Number of Iterations: ").append(Integer.toString(pdata.getNumberOfLDAIterations())).append("\n")
                            .append("Alpha: ").append(Double.toString(pdata.getAlpha())).append("\n")
                            .append("Beta: ").append(Double.toString(pdata.getBeta()));
                }
                writer.write(param.toString());

                writer.newLine();
                writer.newLine();
                writer.write("PROJECTION");
                writer.newLine();
                writer.write("Distance: " + pdata.getDissimilarityType().toString());
                writer.newLine();
                writer.write("Number of control points: " + pdata.getNumberControlPoints());
                writer.newLine();
                writer.write("Number of neighboors: " + pdata.getNumberNeighborsConnection());

                writer.newLine();
                writer.newLine();
                writer.write("TOPIC EXTRACTION");
                writer.newLine();
                if (tdata.getTopicType() == TopicData.TopicType.COVARIANCE) {
                    writer.write("Technique: Covariance");
                } else if (tdata.getTopicType() == TopicData.TopicType.PCA) {
                    writer.write("Technique: PCA");
                } else if (tdata.getTopicType() == TopicData.TopicType.LDA) {
                    writer.write("Technique: LDA");
                }
                writer.newLine();
                writer.write("Parameters: " + this.topicExtratcionTextArea.getText());

                writer.newLine();
                writer.newLine();
                writer.write("MONIC");
                writer.newLine();
                writer.write("Epsilon: " + pdata.getEpsilon());
                writer.newLine();
                writer.write("MinPoints: " + pdata.getMinPoints());
                writer.newLine();
                writer.write("Theta: " + pdata.getTheta());
                writer.newLine();
                writer.write("Theta split: " + pdata.getThetaSplit());

                writer.newLine();
                writer.newLine();
                writer.write("STRESS");
                writer.newLine();
                writer.write("Static stress: " + pdata.getStaticStress());
                writer.newLine();
                writer.write("Dynamic stress: " + pdata.getDynamicStress());
                writer.flush();
            } catch (IOException ex) {
                Logger.getLogger(TemporalProjectionViewer.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(TemporalProjectionViewer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void saveParametersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveParametersButtonActionPerformed
        final JFileChooser fc = new JFileChooser() {
            private static final long serialVersionUID = 1L;

            @Override
            public void approveSelection() {
                File file = getSelectedFile();
                if (file != null && file.exists()) {
                    String message = "The file \"" + file.getName() + "\" already exists. \n"
                            + "Do you want to replace the existing file?";
                    int answer = JOptionPane.showOptionDialog(this, message, "Save Warning",
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
                    if (answer == JOptionPane.NO_OPTION) {
                        return;
                    }
                }
                super.approveSelection();
            }
        };
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        SystemPropertiesManager m = SystemPropertiesManager.getInstance();
        String directory = m.getProperty("SAVEPARAM.DIR");
        if (directory != null) {
            fc.setCurrentDirectory(new File(directory));
        } else {
            fc.setCurrentDirectory(new File("."));
        }
        fc.addChoosableFileFilter(new TXTFileFilter());
        int result = fc.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String filename = fc.getSelectedFile().getAbsolutePath();
            m.setProperty("SAVEPARAM.DIR", fc.getSelectedFile().getParent());

            //checking if the name finishes with the correct extension
            if (!filename.toLowerCase().endsWith(".txt")) {
                filename = filename.concat(".txt");
            }
            this.saveParametersInFile(filename);
        }
    }//GEN-LAST:event_saveParametersButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField connectionsTextField;
    private javax.swing.JPanel dataPanel;
    private javax.swing.JTextField dbscanTextField;
    private javax.swing.JLabel distanceTypeLabel;
    private javax.swing.JTextField distanceTypeTextField;
    private javax.swing.JTextField dynamic_stressTextField;
    private javax.swing.JTextField initialNumberOfControlPointsTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField monicTextField;
    private javax.swing.JLabel numberDimensionsLabel;
    private javax.swing.JTextField numberDimensionsTextField;
    private javax.swing.JLabel numberGramsLabel;
    private javax.swing.JTextField numberGramsTextField;
    private javax.swing.JLabel numberObjectsLabel;
    private javax.swing.JTextField numberObjectsTextField;
    private javax.swing.JTextField numberReferencesTextField;
    private javax.swing.JPanel preprocessingPanel;
    private javax.swing.JLabel projTechniqueLabel;
    private javax.swing.JTextField projTechniqueTextField;
    private javax.swing.JPanel projectionPanel;
    private javax.swing.JCheckBox removeStopwordsByTaggingCheckBox;
    private javax.swing.JTextArea representationParametersTextArea;
    private javax.swing.JTextField representationTypeTextField;
    private javax.swing.JButton saveParametersButton;
    private javax.swing.JLabel sourceLabel;
    private javax.swing.JTextField sourceTextField;
    private javax.swing.JTextField static_stressTextField;
    private javax.swing.JTextField stopwordsListTextField;
    private javax.swing.JTextField timeTextField;
    private javax.swing.JTextArea topicExtratcionTextArea;
    private javax.swing.JTextField total_stressTextField;
    // End of variables declaration//GEN-END:variables
}
