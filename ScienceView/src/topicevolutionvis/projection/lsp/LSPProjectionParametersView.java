/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TemporalProjectionParametersView.java
 *
 * Created on 20/10/2009, 15:59:03
 */
package topicevolutionvis.projection.lsp;

import topicevolutionvis.database.DatabaseCorpus;
import topicevolutionvis.projection.ProjectionData;
import topicevolutionvis.projection.ProjectorType;
import topicevolutionvis.projection.distance.DissimilarityType;
import topicevolutionvis.wizard.ProjectionViewWizard;

/**
 *
 * @author Aretha
 */
public class LSPProjectionParametersView extends ProjectionViewWizard {

    private DatabaseCorpus corpus;

    /**
     * Creates new form TemporalProjectionParametersView
     */
    public LSPProjectionParametersView(ProjectionData pdata) {
        super(pdata);
        initComponents();
        this.corpus = pdata.getDatabaseCorpus();
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

        jPanel1 = new javax.swing.JPanel();
        projectionPanel = new javax.swing.JPanel();
        nnpRadioButton = new javax.swing.JRadioButton();
        fastmapRadioButton = new javax.swing.JRadioButton();
        improvementPanel = new javax.swing.JPanel();
        nIterationsLabel = new javax.swing.JLabel();
        deltaLabel = new javax.swing.JLabel();
        deltaTextField = new javax.swing.JTextField();
        nIterationsTextField = new javax.swing.JTextField();
        lspPanel = new javax.swing.JPanel();
        numberCPLabel = new javax.swing.JLabel();
        numberNeighborsLabel = new javax.swing.JLabel();
        numberCPTextField = new javax.swing.JTextField();
        numberNeighborsTextField = new javax.swing.JTextField();
        controlPointsChoicePanel = new javax.swing.JPanel();
        kmedoidsRadioButton = new javax.swing.JRadioButton();
        randomRadioButton = new javax.swing.JRadioButton();
        kmeansRadioButton = new javax.swing.JRadioButton();
        statusPanel = new javax.swing.JPanel();
        statusProgressBar = new javax.swing.JProgressBar();
        statusLabel = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Temporal Projection"));
        setLayout(new java.awt.GridBagLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("LSP Parameters"));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        projectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Type of Projection"));
        projectionPanel.setLayout(new java.awt.GridBagLayout());

        nnpRadioButton.setText("Nearest Neighbor Projection");
        nnpRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        nnpRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        projectionPanel.add(nnpRadioButton, gridBagConstraints);

        fastmapRadioButton.setSelected(true);
        fastmapRadioButton.setText("Fastmap Projection");
        fastmapRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        fastmapRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        projectionPanel.add(fastmapRadioButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(projectionPanel, gridBagConstraints);

        improvementPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Projection Improvement (Force)"));
        improvementPanel.setLayout(new java.awt.GridBagLayout());

        nIterationsLabel.setText("Number of iterations");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        improvementPanel.add(nIterationsLabel, gridBagConstraints);

        deltaLabel.setText("Fraction of delta");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        improvementPanel.add(deltaLabel, gridBagConstraints);

        deltaTextField.setColumns(5);
        deltaTextField.setText("8.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        improvementPanel.add(deltaTextField, gridBagConstraints);

        nIterationsTextField.setColumns(5);
        nIterationsTextField.setText("50");
        improvementPanel.add(nIterationsTextField, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(improvementPanel, gridBagConstraints);

        lspPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Neighborhood Parameters"));
        lspPanel.setLayout(new java.awt.GridBagLayout());

        numberCPLabel.setText("Number Control Points");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        lspPanel.add(numberCPLabel, gridBagConstraints);

        numberNeighborsLabel.setText("Number of Neighbors");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        lspPanel.add(numberNeighborsLabel, gridBagConstraints);

        numberCPTextField.setColumns(5);
        numberCPTextField.setText("10");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        lspPanel.add(numberCPTextField, gridBagConstraints);

        numberNeighborsTextField.setColumns(5);
        numberNeighborsTextField.setText("10");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        lspPanel.add(numberNeighborsTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(lspPanel, gridBagConstraints);

        controlPointsChoicePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Control Points Choice"));
        controlPointsChoicePanel.setLayout(new java.awt.GridBagLayout());

        kmedoidsRadioButton.setSelected(true);
        kmedoidsRadioButton.setText("K-medoids");
        kmedoidsRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        kmedoidsRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        controlPointsChoicePanel.add(kmedoidsRadioButton, gridBagConstraints);

        randomRadioButton.setText("Random");
        randomRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        randomRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        controlPointsChoicePanel.add(randomRadioButton, gridBagConstraints);

        kmeansRadioButton.setText("K-means");
        kmeansRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        kmeansRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        controlPointsChoicePanel.add(kmeansRadioButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(controlPointsChoicePanel, gridBagConstraints);

        add(jPanel1, new java.awt.GridBagConstraints());

        statusPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Status"));
        statusPanel.setLayout(new java.awt.BorderLayout());

        statusProgressBar.setPreferredSize(new java.awt.Dimension(150, 22));
        statusProgressBar.setStringPainted(true);
        statusPanel.add(statusProgressBar, java.awt.BorderLayout.SOUTH);

        statusLabel.setText("   ");
        statusLabel.setMinimumSize(new java.awt.Dimension(100, 22));
        statusLabel.setPreferredSize(new java.awt.Dimension(100, 22));
        statusPanel.add(statusLabel, java.awt.BorderLayout.NORTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(statusPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void setStatus(String status, int value) {
        this.statusLabel.setText(status);
        this.statusProgressBar.setValue(value);
    }

    @Override
    public void reset() {
        if (pdata.getDissimilarityType() == DissimilarityType.KOLMOGOROV) {
            this.kmeansRadioButton.setEnabled(false);
            this.kmedoidsRadioButton.setEnabled(true);

            if (this.kmeansRadioButton.isSelected()) {
                this.kmeansRadioButton.setSelected(false);
                this.kmedoidsRadioButton.setSelected(true);
            }
        } else {
            this.kmeansRadioButton.setEnabled(true);
            this.kmedoidsRadioButton.setEnabled(false);

            if (this.kmedoidsRadioButton.isSelected()) {
                this.kmeansRadioButton.setSelected(true);
                this.kmedoidsRadioButton.setSelected(false);
            }
        }

        int nrobjects = this.corpus.getDocumentsIds().length;


        this.numberCPTextField.setText(Integer.toString(nrobjects / 10)); 

        if (nrobjects < 100) {
            this.numberCPTextField.setText("10");
        } else if (nrobjects > 1500) {
            this.numberNeighborsTextField.setText("15");
        } else {
            this.numberNeighborsTextField.setText("10");
        }
    }

    @Override
    public void refreshData() {
        this.pdata.setFractionDelta(Float.parseFloat(this.deltaTextField.getText()));
        this.pdata.setNumberIterations(Integer.parseInt(this.nIterationsTextField.getText()));
        this.pdata.setNumberControlPoints(Integer.parseInt(this.numberCPTextField.getText()));
        this.pdata.setNumberNeighborsConnection(Integer.parseInt(this.numberNeighborsTextField.getText()));

        if (this.fastmapRadioButton.isSelected()) {
            this.pdata.setProjectorType(ProjectorType.FASTMAP);
        } else {
            this.pdata.setProjectorType(ProjectorType.NNP);
        }

        if (this.kmedoidsRadioButton.isSelected()) {
            this.pdata.setControlPointsChoice(ControlPointsType.KMEDOIDS);
        } else if (this.kmeansRadioButton.isSelected()) {
            this.pdata.setControlPointsChoice(ControlPointsType.KMEANS);
        } else {
            this.pdata.setControlPointsChoice(ControlPointsType.RANDOM);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel controlPointsChoicePanel;
    private javax.swing.JLabel deltaLabel;
    private javax.swing.JTextField deltaTextField;
    private javax.swing.JRadioButton fastmapRadioButton;
    private javax.swing.JPanel improvementPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton kmeansRadioButton;
    private javax.swing.JRadioButton kmedoidsRadioButton;
    private javax.swing.JPanel lspPanel;
    private javax.swing.JLabel nIterationsLabel;
    private javax.swing.JTextField nIterationsTextField;
    private javax.swing.JRadioButton nnpRadioButton;
    private javax.swing.JLabel numberCPLabel;
    private javax.swing.JTextField numberCPTextField;
    private javax.swing.JLabel numberNeighborsLabel;
    private javax.swing.JTextField numberNeighborsTextField;
    private javax.swing.JPanel projectionPanel;
    private javax.swing.JRadioButton randomRadioButton;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JProgressBar statusProgressBar;
    // End of variables declaration//GEN-END:variables

	@Override
	public boolean isNextStepTerminal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canGoToNextStep() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canGoToPreviousStep() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canCancel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
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

	@Override
	public boolean hasPreviousStep() {
		// TODO Auto-generated method stub
		return true;
	}
}
