/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SaveToPExFromatDialog.java
 *
 * Created on Jul 21, 2010, 9:51:39 AM
 */
package topicevolutionvis.wizard;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import topicevolutionvis.projection.ProjectionData;
import topicevolutionvis.util.SaveDialog;
import topicevolutionvis.utils.filefilter.ScalarFileFilter;
import topicevolutionvis.utils.filefilter.ZipFileFilter;

/**
 *
 * @author Aretha
 */
public class SaveToPExFromatDialog extends javax.swing.JDialog {

    ProjectionData pdata;
    PreprocessingWizard preprocessing;

    /** Creates new form SaveToPExFromatDialog */
    public SaveToPExFromatDialog(ProjectionData pdata, PreprocessingWizard preprocessing) {
        initComponents();
        this.setLocationRelativeTo(null);
        this.pdata = pdata;
        this.preprocessing = preprocessing;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        titleCheckBox = new javax.swing.JCheckBox();
        authorsCheckBox = new javax.swing.JCheckBox();
        keywordsCheckBox = new javax.swing.JCheckBox();
        abstractCheckBox = new javax.swing.JCheckBox();
        referencesCheckBox = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        saveScalarCheckBox = new javax.swing.JCheckBox();
        scalarsLabel = new javax.swing.JLabel();
        scalarsFilenameTextField = new javax.swing.JTextField();
        scalarsSearchButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        filenameTextField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        yearSpinner = new javax.swing.JSpinner();
        timeSliceCheckBox = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Fields Options"));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Save the following fields:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(jLabel1, gridBagConstraints);

        titleCheckBox.setSelected(true);
        titleCheckBox.setText("Title");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(titleCheckBox, gridBagConstraints);

        authorsCheckBox.setSelected(true);
        authorsCheckBox.setText("Author(s)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(authorsCheckBox, gridBagConstraints);

        keywordsCheckBox.setSelected(true);
        keywordsCheckBox.setText("Keywords");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(keywordsCheckBox, gridBagConstraints);

        abstractCheckBox.setSelected(true);
        abstractCheckBox.setText("Abstract");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(abstractCheckBox, gridBagConstraints);

        referencesCheckBox.setSelected(true);
        referencesCheckBox.setText("References");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(referencesCheckBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        getContentPane().add(jPanel1, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        okButton.setText("Ok");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(okButton, gridBagConstraints);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(3, 15, 3, 15);
        jPanel2.add(cancelButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        getContentPane().add(jPanel2, gridBagConstraints);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Other Options"));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        saveScalarCheckBox.setText("Save scalars");
        saveScalarCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveScalarCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel3.add(saveScalarCheckBox, gridBagConstraints);

        scalarsLabel.setText("Scalars Filename:");
        scalarsLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanel3.add(scalarsLabel, gridBagConstraints);

        scalarsFilenameTextField.setColumns(20);
        scalarsFilenameTextField.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanel3.add(scalarsFilenameTextField, gridBagConstraints);

        scalarsSearchButton.setText("Search...");
        scalarsSearchButton.setEnabled(false);
        scalarsSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scalarsSearchButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel3.add(scalarsSearchButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        getContentPane().add(jPanel3, gridBagConstraints);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("General Options"));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jLabel3.setText("Filename:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel4.add(jLabel3, gridBagConstraints);

        filenameTextField.setColumns(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel4.add(filenameTextField, gridBagConstraints);

        searchButton.setText("Search...");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel4.add(searchButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(jPanel4, gridBagConstraints);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Slice Options"));
        jPanel5.setLayout(new java.awt.GridBagLayout());

        yearSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 5, 1));
        yearSpinner.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanel5.add(yearSpinner, gridBagConstraints);

        timeSliceCheckBox.setText("Time slice every");
        timeSliceCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeSliceCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanel5.add(timeSliceCheckBox, gridBagConstraints);

        jLabel2.setText(" year(s)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanel5.add(jLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(jPanel5, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.pdata = null;
        this.dispose();
        this.preprocessing.saveToPExFormatCheckBox.setSelected(false);

    }//GEN-LAST:event_cancelButtonActionPerformed

    private boolean oneFieldIsChecked() {
        if ((!this.titleCheckBox.isSelected())
                && (!this.authorsCheckBox.isSelected())
                && (!this.abstractCheckBox.isSelected())
                && (!this.keywordsCheckBox.isSelected())
                && (!this.referencesCheckBox.isSelected())) {
            return false;
        }
        return true;
    }

    private boolean isScalarFieldsCorrect() {
        if (this.saveScalarCheckBox.isSelected() && this.scalarsFilenameTextField.getText().trim().compareTo("") != 0) {
            return true;
        }
        if (!this.saveScalarCheckBox.isSelected()) {
            return true;
        }
        return false;
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        if (this.filenameTextField.getText().trim().compareTo("") != 0 && oneFieldIsChecked()) {
            if (isScalarFieldsCorrect()) {
                //pex filename
                this.pdata.setPExFilename(this.filenameTextField.getText());

                //time slice
                this.pdata.setIndividualFilesToPExFormat(this.timeSliceCheckBox.isSelected());
                this.pdata.setYearStepToPExFormat(((Integer) this.yearSpinner.getValue()).intValue());

                //fields options
                this.pdata.setSaveTitleToPExFormat(this.titleCheckBox.isSelected());
                this.pdata.setSaveAuthorsToPExFormat(this.authorsCheckBox.isSelected());
                this.pdata.setSaveAbstractToPExFormat(this.abstractCheckBox.isSelected());
                this.pdata.setSaveKeywordsToPExFormat(this.keywordsCheckBox.isSelected());
                this.pdata.setSaveReferencesToPExFormat(this.referencesCheckBox.isSelected());

                //other options
                this.pdata.setScalarFilename(this.scalarsFilenameTextField.getText());

                this.dispose();
                this.preprocessing.saveToPExFormatCheckBox.setSelected(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "You must specify a scalars filename to be able to save this information.",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this,
                    "You must specify a PEx filename and \n at least one of the fields must be selected.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);

        }

    }//GEN-LAST:event_okButtonActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        String filename = this.pdata.getSourceFile();
        int result = SaveDialog.showSaveDialog(new ZipFileFilter(), this, filename);
        if (result == JFileChooser.APPROVE_OPTION) {
            filename = SaveDialog.getFilename();
            this.filenameTextField.setText(filename);
        }
    }//GEN-LAST:event_searchButtonActionPerformed

    private void saveScalarCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveScalarCheckBoxActionPerformed
        if (this.saveScalarCheckBox.isSelected()) {
            this.scalarsLabel.setEnabled(true);
            this.scalarsFilenameTextField.setEnabled(true);
            this.scalarsSearchButton.setEnabled(true);
        } else {
            this.scalarsFilenameTextField.setText(null);
            this.scalarsLabel.setEnabled(false);
            this.scalarsFilenameTextField.setEnabled(false);
            this.scalarsSearchButton.setEnabled(false);
        }
    }//GEN-LAST:event_saveScalarCheckBoxActionPerformed

    private void scalarsSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scalarsSearchButtonActionPerformed
        String filename = this.pdata.getSourceFile();
        int result = SaveDialog.showSaveDialog(new ScalarFileFilter(), this, filename);
        if (result == JFileChooser.APPROVE_OPTION) {
            filename = SaveDialog.getFilename();
            this.scalarsFilenameTextField.setText(filename);
        } else {
            this.saveScalarCheckBox.setSelected(false);
            this.scalarsFilenameTextField.setText(null);
            this.scalarsLabel.setEnabled(false);
            this.scalarsFilenameTextField.setEnabled(false);
            this.scalarsSearchButton.setEnabled(false);
        }
    }//GEN-LAST:event_scalarsSearchButtonActionPerformed

    private void timeSliceCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeSliceCheckBoxActionPerformed
        if (this.timeSliceCheckBox.isSelected()) {
            this.yearSpinner.setEnabled(true);
        } else {
            this.yearSpinner.setEnabled(false);
        }
    }//GEN-LAST:event_timeSliceCheckBoxActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox abstractCheckBox;
    private javax.swing.JCheckBox authorsCheckBox;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField filenameTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JCheckBox keywordsCheckBox;
    private javax.swing.JButton okButton;
    private javax.swing.JCheckBox referencesCheckBox;
    private javax.swing.JCheckBox saveScalarCheckBox;
    private javax.swing.JTextField scalarsFilenameTextField;
    private javax.swing.JLabel scalarsLabel;
    private javax.swing.JButton scalarsSearchButton;
    private javax.swing.JButton searchButton;
    private javax.swing.JCheckBox timeSliceCheckBox;
    private javax.swing.JCheckBox titleCheckBox;
    private javax.swing.JSpinner yearSpinner;
    // End of variables declaration//GEN-END:variables
}
