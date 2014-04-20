/* ***** BEGIN LICENSE BLOCK *****
 *
 * Copyright (c) 2005-2007 Universidade de Sao Paulo, Sao Carlos/SP, Brazil.
 * All Rights Reserved.
 *
 * This file is part of Projection Explorer (PEx).
 *
 * How to cite this work:
 *  
 @inproceedings{paulovich2007pex,
 author = {Fernando V. Paulovich and Maria Cristina F. Oliveira and Rosane 
 Minghim},
 title = {The Projection Explorer: A Flexible Tool for Projection-based 
 Multidimensional Visualization},
 booktitle = {SIBGRAPI '07: Proceedings of the XX Brazilian Symposium on 
 Computer Graphics and Image Processing (SIBGRAPI 2007)},
 year = {2007},
 isbn = {0-7695-2996-8},
 pages = {27--34},
 doi = {http://dx.doi.org/10.1109/SIBGRAPI.2007.39},
 publisher = {IEEE Computer Society},
 address = {Washington, DC, USA},
 }
 *  
 * PEx is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 *
 * PEx is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 *
 * This code was developed by members of Computer Graphics and Image
 * Processing Group (http://www.lcad.icmc.usp.br) at Instituto de Ciencias
 * Matematicas e de Computacao - ICMC - (http://www.icmc.usp.br) of 
 * Universidade de Sao Paulo, Sao Carlos/SP, Brazil. The initial developer 
 * of the original code is Fernando Vieira Paulovich <fpaulovich@gmail.com>.
 *
 * Contributor(s): Roberto Pinho <robertopinho@yahoo.com.br>, 
 *                 Rosane Minghim <rminghim@icmc.usp.br>
 *
 * You should have received a copy of the GNU General Public License along 
 * with PEx. If not, see <http://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ***** */
package topicevolutionvis.wizard;

import topicevolutionvis.preprocessing.RepresentationType;
import topicevolutionvis.preprocessing.steemer.StemmerType;
import topicevolutionvis.preprocessing.transformation.MatrixTransformationType;
import topicevolutionvis.projection.ProjectionData;
import topicevolutionvis.view.tools.LuhnCutAnalizer;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class PreprocessingWizard extends WizardPanel {

    ProjectionData pdata;

    /**
     * Creates new form Preprocessing
     *
     * @param pdata
     */
    public PreprocessingWizard(ProjectionData pdata) {
        this.pdata = pdata;
        initComponents();

        for (StemmerType.Type st : StemmerType.getTypes()) {
            this.stemmerComboBox.addItem(st);
        }

        //adicionando os tipos de distância disponíveis ao combo box
        for (RepresentationType disstype : RepresentationType.getTypes()) {
            this.representationComboBox.addItem(disstype);
        }

        for (MatrixTransformationType mtt : MatrixTransformationType.getTypes()) {
            this.matrixtransfComboBox.addItem(mtt);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        wordListTypeButtonGroup = new javax.swing.ButtonGroup();
        preProcessingPanel = new javax.swing.JPanel();
        gramsComboBox = new javax.swing.JComboBox();
        gramsLabel = new javax.swing.JLabel();
        stemmerComboBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        cutsPanel = new javax.swing.JPanel();
        referencesCutPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        luhnUpperReferencesCutTextField = new javax.swing.JTextField();
        luhnLowerReferencesCutTextField = new javax.swing.JTextField();
        analyzeReferencesButton = new javax.swing.JButton();
        termsCutPanel = new javax.swing.JPanel();
        luhnLabel = new javax.swing.JLabel();
        luhnLowerTextField = new javax.swing.JTextField();
        luhnUpperLabel = new javax.swing.JLabel();
        luhnUpperTextField = new javax.swing.JTextField();
        analyzeTermsButton = new javax.swing.JButton();
        matrixTypePanel = new javax.swing.JPanel();
        matrixtransfComboBox = new javax.swing.JComboBox();
        wordListTypePanel = new javax.swing.JPanel();
        stopwordRadioButton = new javax.swing.JRadioButton();
        startwordRadioButton = new javax.swing.JRadioButton();
        useWeightCheckBox = new javax.swing.JCheckBox();
        saveToPExFormatCheckBox = new javax.swing.JCheckBox();
        RepresentationPanel = new javax.swing.JPanel();
        representationComboBox = new javax.swing.JComboBox();
        ldaParametersPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        numberOfTopicsTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        betaTextField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        alphaTextField = new javax.swing.JTextField();
        refreshAlphaButton = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        iterationsTextField = new javax.swing.JTextField();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Corpus Pre-processing"));
        setLayout(new java.awt.GridBagLayout());

        preProcessingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("General Parameters"));
        preProcessingPanel.setLayout(new java.awt.GridBagLayout());

        gramsComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        preProcessingPanel.add(gramsComboBox, gridBagConstraints);

        gramsLabel.setText("Number of grams:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        preProcessingPanel.add(gramsLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        preProcessingPanel.add(stemmerComboBox, gridBagConstraints);

        jLabel1.setText("Steemer:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        preProcessingPanel.add(jLabel1, gridBagConstraints);

        cutsPanel.setLayout(new java.awt.GridBagLayout());

        referencesCutPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("References Cut"));
        referencesCutPanel.setEnabled(false);
        referencesCutPanel.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText("Luhn's lower cut:");
        jLabel2.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        referencesCutPanel.add(jLabel2, gridBagConstraints);

        jLabel3.setText("Luhn's upper cut:");
        jLabel3.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        referencesCutPanel.add(jLabel3, gridBagConstraints);

        luhnUpperReferencesCutTextField.setColumns(5);
        luhnUpperReferencesCutTextField.setText("-1");
        luhnUpperReferencesCutTextField.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        referencesCutPanel.add(luhnUpperReferencesCutTextField, gridBagConstraints);

        luhnLowerReferencesCutTextField.setColumns(5);
        luhnLowerReferencesCutTextField.setText("1");
        luhnLowerReferencesCutTextField.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        referencesCutPanel.add(luhnLowerReferencesCutTextField, gridBagConstraints);

        analyzeReferencesButton.setText("Analyze");
        analyzeReferencesButton.setEnabled(false);
        analyzeReferencesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                analyzeReferencesButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        referencesCutPanel.add(analyzeReferencesButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        cutsPanel.add(referencesCutPanel, gridBagConstraints);

        termsCutPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Terms Cut"));
        termsCutPanel.setLayout(new java.awt.GridBagLayout());

        luhnLabel.setText("Luhn's lower cut:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        termsCutPanel.add(luhnLabel, gridBagConstraints);

        luhnLowerTextField.setColumns(5);
        luhnLowerTextField.setText("10");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        termsCutPanel.add(luhnLowerTextField, gridBagConstraints);

        luhnUpperLabel.setText("Luhn's upper cut:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        termsCutPanel.add(luhnUpperLabel, gridBagConstraints);

        luhnUpperTextField.setColumns(5);
        luhnUpperTextField.setText("-1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        termsCutPanel.add(luhnUpperTextField, gridBagConstraints);

        analyzeTermsButton.setText("Analyze");
        analyzeTermsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                analyzeTermsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        termsCutPanel.add(analyzeTermsButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        cutsPanel.add(termsCutPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        preProcessingPanel.add(cutsPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(preProcessingPanel, gridBagConstraints);

        matrixTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Matrix Transformation"));
        matrixTypePanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        matrixTypePanel.add(matrixtransfComboBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(matrixTypePanel, gridBagConstraints);

        wordListTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Word List Type"));
        wordListTypePanel.setLayout(new java.awt.GridBagLayout());

        wordListTypeButtonGroup.add(stopwordRadioButton);
        stopwordRadioButton.setSelected(true);
        stopwordRadioButton.setText("Stop Words");
        stopwordRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        stopwordRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        wordListTypePanel.add(stopwordRadioButton, gridBagConstraints);

        wordListTypeButtonGroup.add(startwordRadioButton);
        startwordRadioButton.setText("Start Words");
        startwordRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        startwordRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        wordListTypePanel.add(startwordRadioButton, gridBagConstraints);

        useWeightCheckBox.setText("Use weights");
        useWeightCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        useWeightCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        wordListTypePanel.add(useWeightCheckBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(wordListTypePanel, gridBagConstraints);

        saveToPExFormatCheckBox.setText("Save corpus to PEx format");
        saveToPExFormatCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveToPExFormatCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(saveToPExFormatCheckBox, gridBagConstraints);

        RepresentationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Representation Model"));
        RepresentationPanel.setLayout(new java.awt.GridBagLayout());

        representationComboBox.setMinimumSize(new java.awt.Dimension(100, 20));
        representationComboBox.setPreferredSize(new java.awt.Dimension(100, 20));
        representationComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                representationComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        RepresentationPanel.add(representationComboBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(RepresentationPanel, gridBagConstraints);

        ldaParametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("LDA Parameters"));
        ldaParametersPanel.setEnabled(false);
        ldaParametersPanel.setLayout(new java.awt.GridBagLayout());

        jLabel4.setText("Alpha:");
        jLabel4.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        ldaParametersPanel.add(jLabel4, gridBagConstraints);

        numberOfTopicsTextField.setColumns(5);
        numberOfTopicsTextField.setText("300");
        numberOfTopicsTextField.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        ldaParametersPanel.add(numberOfTopicsTextField, gridBagConstraints);

        jLabel5.setText("Beta:");
        jLabel5.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        ldaParametersPanel.add(jLabel5, gridBagConstraints);

        betaTextField.setColumns(5);
        betaTextField.setText("0.01");
        betaTextField.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        ldaParametersPanel.add(betaTextField, gridBagConstraints);

        jLabel6.setText("Number of topics:");
        jLabel6.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        ldaParametersPanel.add(jLabel6, gridBagConstraints);

        alphaTextField.setColumns(5);
        alphaTextField.setText("0.16");
        alphaTextField.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        ldaParametersPanel.add(alphaTextField, gridBagConstraints);

        refreshAlphaButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Refresh16.gif"))); // NOI18N
        refreshAlphaButton.setToolTipText("Calculate alpha as a function of the number of topics (50/T)");
        refreshAlphaButton.setEnabled(false);
        refreshAlphaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshAlphaButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        ldaParametersPanel.add(refreshAlphaButton, gridBagConstraints);

        jLabel7.setText("Number of iterations:");
        jLabel7.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        ldaParametersPanel.add(jLabel7, gridBagConstraints);

        iterationsTextField.setColumns(5);
        iterationsTextField.setText("1000");
        iterationsTextField.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        ldaParametersPanel.add(iterationsTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(ldaParametersPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    private void analyzeTermsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_analyzeTermsButtonActionPerformed
        refreshData();
        LuhnCutAnalizer.getInstance((ProjectionWizardView) getTopLevelAncestor()).display(pdata);
        luhnLowerTextField.setText(Integer.toString(pdata.getLunhLowerCut()));
        luhnUpperTextField.setText(Integer.toString(pdata.getLunhUpperCut()));
    }//GEN-LAST:event_analyzeTermsButtonActionPerformed

    private void saveToPExFormatCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveToPExFormatCheckBoxActionPerformed
        if (this.saveToPExFormatCheckBox.isSelected()) {
            SaveToPExFromatDialog pexDialog = new SaveToPExFromatDialog(pdata, this);
            pexDialog.setVisible(true);
//            this.oneFileToEachYearCheckBox.setEnabled(true);
//            this.yearStepSpinner.setEnabled(true);
//            this.yearsLabel.setEnabled(true);
//            String filename = this.pdata.getSourceFile();
//            int result = SaveDialog.showSaveDialog(new ZipFileFilter(), this, filename);
//
//            if (result == JFileChooser.APPROVE_OPTION) {
//                filename = SaveDialog.getFilename();
//                this.pdata.setPExFilename(filename);
//            } else {
//                this.saveToPExFormatCheckBox.setSelected(false);
//            }
        }
//        else {
//            this.oneFileToEachYearCheckBox.setEnabled(false);
//            this.yearStepSpinner.setEnabled(false);
//            this.yearsLabel.setEnabled(false);
//            this.pdata.setPExFilename("");
//        }
    }//GEN-LAST:event_saveToPExFormatCheckBoxActionPerformed

    private void analyzeReferencesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_analyzeReferencesButtonActionPerformed
        CosinePlusReferencesSettings.getInstance((ProjectionWizardView) getTopLevelAncestor()).display(pdata);
        this.luhnLowerReferencesCutTextField.setText(Integer.toString(pdata.getReferencesLowerCut()));
        this.luhnUpperReferencesCutTextField.setText(Integer.toString(pdata.getReferencesUpperCut()));
    }//GEN-LAST:event_analyzeReferencesButtonActionPerformed

    private void setEnabledReferencesCutPanel(boolean enabled) {
        this.referencesCutPanel.setEnabled(enabled);
        this.jLabel2.setEnabled(enabled);
        this.jLabel3.setEnabled(enabled);
        this.luhnLowerReferencesCutTextField.setEnabled(enabled);
        this.luhnUpperReferencesCutTextField.setEnabled(enabled);
        this.analyzeReferencesButton.setEnabled(enabled);
    }

    private void setEnabledLDAParametersPanel(boolean status) {
        this.jLabel4.setEnabled(status);
        this.jLabel5.setEnabled(status);
        this.jLabel6.setEnabled(status);
        this.jLabel7.setEnabled(status);
        this.numberOfTopicsTextField.setEnabled(status);
        this.alphaTextField.setEnabled(status);
        this.betaTextField.setEnabled(status);
        this.refreshAlphaButton.setEnabled(status);
        this.iterationsTextField.setEnabled(status);
    }

    private void representationComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_representationComboBoxActionPerformed
        RepresentationType represType = (RepresentationType) this.representationComboBox.getSelectedItem();
        if (represType.equals(RepresentationType.VECTOR_SPACE_MODEL)) {
            this.setEnabledLDAParametersPanel(false);
            this.setEnabledReferencesCutPanel(false);
            this.matrixTypePanel.setEnabled(true);
            this.ldaParametersPanel.setEnabled(false);
            this.matrixtransfComboBox.setEnabled(true);
            this.gramsLabel.setEnabled(true);
            this.gramsComboBox.setEnabled(true);
        } else if (represType.equals(RepresentationType.VECTOR_SPACE_REFERENCES)) {
            this.setEnabledLDAParametersPanel(false);
            this.setEnabledReferencesCutPanel(true);
            this.matrixTypePanel.setEnabled(true);
            this.ldaParametersPanel.setEnabled(false);
            this.matrixtransfComboBox.setEnabled(true);
            this.gramsLabel.setEnabled(true);
            this.gramsComboBox.setEnabled(true);
        } else { //LDA
            this.setEnabledLDAParametersPanel(true);
            this.setEnabledReferencesCutPanel(false);
            this.matrixTypePanel.setEnabled(false);
            this.ldaParametersPanel.setEnabled(true);
            this.matrixtransfComboBox.setEnabled(false);
            this.gramsLabel.setEnabled(false);
            this.gramsComboBox.setEnabled(false);
        }
    }//GEN-LAST:event_representationComboBoxActionPerformed

    private void refreshAlphaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshAlphaButtonActionPerformed
        this.alphaTextField.setText(String.valueOf((float) 50 / (float) Integer.parseInt(this.numberOfTopicsTextField.getText())));
        this.alphaTextField.setCaretPosition(0);
    }//GEN-LAST:event_refreshAlphaButtonActionPerformed

    public PreprocessingWizard reset() {
        return this;
    }

    @Override
    public void refreshData() {
        pdata.setStemmer((StemmerType.Type) stemmerComboBox.getSelectedItem());
        pdata.setUseStopword(stopwordRadioButton.isSelected());
        pdata.setUseWeight(useWeightCheckBox.isSelected());
        pdata.setRepresentationType((RepresentationType) this.representationComboBox.getSelectedItem());

        if (luhnLowerTextField.getText().trim().length() > 0) {
            pdata.setLunhLowerCut(Integer.parseInt(luhnLowerTextField.getText()));
        } else {
            pdata.setLunhLowerCut(1);
        }

        if (luhnUpperTextField.getText().trim().length() > 0) {
            pdata.setLunhUpperCut(Integer.parseInt(luhnUpperTextField.getText()));
        } else {
            pdata.setLunhUpperCut(-1);
        }

        RepresentationType represType = (RepresentationType) this.representationComboBox.getSelectedItem();
        pdata.setRepresentationType(represType);
        if (represType.equals(RepresentationType.VECTOR_SPACE_MODEL)) {
            pdata.setMatrixTransformationType((MatrixTransformationType) matrixtransfComboBox.getSelectedItem());
            pdata.setNumberGrams(gramsComboBox.getSelectedIndex() + 1);
        } else if (represType.equals(RepresentationType.VECTOR_SPACE_REFERENCES)) {
            pdata.setIncludeReferencesInBOW(true);
            if (luhnLowerReferencesCutTextField.getText().trim().length() > 0) {
                pdata.setReferencesLowerCut(Integer.parseInt(luhnLowerReferencesCutTextField.getText()));
            } else {
                pdata.setReferencesLowerCut(1);
            }
            if (luhnUpperReferencesCutTextField.getText().trim().length() > 0) {
                pdata.setReferencesUpperCut(Integer.parseInt(luhnUpperReferencesCutTextField.getText()));
            } else {
                pdata.setReferencesUpperCut(-1);
            }
            pdata.setMatrixTransformationType((MatrixTransformationType) matrixtransfComboBox.getSelectedItem());
            pdata.setNumberGrams(gramsComboBox.getSelectedIndex() + 1);
        } else if (represType.equals(RepresentationType.LDA)) {
            pdata.setNumberOfTopics(Integer.parseInt(this.numberOfTopicsTextField.getText()));
            pdata.setAlpha(Double.parseDouble(this.alphaTextField.getText()));
            pdata.setBeta(Double.parseDouble(this.betaTextField.getText()));
            pdata.setNumberOfLDAIterations(Integer.parseInt(this.iterationsTextField.getText()));
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel RepresentationPanel;
    private javax.swing.JTextField alphaTextField;
    private javax.swing.JButton analyzeReferencesButton;
    private javax.swing.JButton analyzeTermsButton;
    private javax.swing.JTextField betaTextField;
    private javax.swing.JPanel cutsPanel;
    private javax.swing.JComboBox gramsComboBox;
    private javax.swing.JLabel gramsLabel;
    private javax.swing.JTextField iterationsTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel ldaParametersPanel;
    private javax.swing.JLabel luhnLabel;
    private javax.swing.JTextField luhnLowerReferencesCutTextField;
    private javax.swing.JTextField luhnLowerTextField;
    private javax.swing.JLabel luhnUpperLabel;
    private javax.swing.JTextField luhnUpperReferencesCutTextField;
    private javax.swing.JTextField luhnUpperTextField;
    private javax.swing.JPanel matrixTypePanel;
    private javax.swing.JComboBox matrixtransfComboBox;
    private javax.swing.JTextField numberOfTopicsTextField;
    private javax.swing.JPanel preProcessingPanel;
    private javax.swing.JPanel referencesCutPanel;
    private javax.swing.JButton refreshAlphaButton;
    private javax.swing.JComboBox representationComboBox;
    public javax.swing.JCheckBox saveToPExFormatCheckBox;
    private javax.swing.JRadioButton startwordRadioButton;
    private javax.swing.JComboBox stemmerComboBox;
    private javax.swing.JRadioButton stopwordRadioButton;
    private javax.swing.JPanel termsCutPanel;
    private javax.swing.JCheckBox useWeightCheckBox;
    private javax.swing.ButtonGroup wordListTypeButtonGroup;
    private javax.swing.JPanel wordListTypePanel;
    // End of variables declaration//GEN-END:variables

	@Override
	public boolean isNextStepTerminal() {
		return false;
	}

	@Override
	public boolean canGoToNextStep() {
		return true;
	}

	@Override
	public boolean canGoToPreviousStep() {
		return true;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public void cancel() {
	}

	@Override
	public boolean hasPreviousStep() {
		return true;
	}

	@Override
	public boolean canResetConfiguration() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resetConfiguration() {
		// TODO Auto-generated method stub
		
	}
}