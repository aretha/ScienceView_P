/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TemporalProjectionViewer.java
 *
 * Created on 23/12/2009, 18:34:13
 */
package topicevolutionvis.view;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;
import javax.swing.tree.DefaultMutableTreeNode;
import topicevolutionvis.database.DatabaseCorpus;
import topicevolutionvis.graph.*;
import topicevolutionvis.projection.ProjectionData;
import topicevolutionvis.projection.temporal.TemporalProjection;
import topicevolutionvis.projection.temporal.listeners.VertexSelectionFactory;
import topicevolutionvis.topic.Topic;
import topicevolutionvis.topic.TopicData;
import topicevolutionvis.util.PExConstants;
import topicevolutionvis.util.RangeSliderPanel;
import topicevolutionvis.view.color.ColorScalePanel;
import topicevolutionvis.view.color.ColorTable;

/**
 *
 * @author Aretha
 */
public class TemporalProjectionViewer extends Viewer implements ChangeListener {

    private static final long serialVersionUID = 1L;
    private java.awt.Font font = new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 12);
    private TemporalProjection projection;
    private ViewPanel view;
    private Tracker tracker;
    private ReportView report;
    private DefaultComboBoxModel colorComboModel = new DefaultComboBoxModel(), sizeComboModel = new DefaultComboBoxModel(), edgesComboModel = new DefaultComboBoxModel(), titlesComboModel = new DefaultComboBoxModel();
    private SpinnerNumberModel titlesNumberModel = new SpinnerNumberModel(0, 0, 0, 1);
    private int lastIndex = 0;
    private int[] indexesUpdateDocumentsTree = null, indexesUpdateTopicsTree = null;
    public ArrayList<DefaultMutableTreeNode> documentsTreeMap = new ArrayList<>();
    public DefaultMutableTreeNode topicsTreeInfo = new DefaultMutableTreeNode();
    //edges
    private RangeSliderPanel edgesRangeSlider;
    private JCheckBox mapWeight;
    private float low_value_edges = 0, high_value_edges = 0;
    //animation
    private boolean animate = false;
    private boolean updating_topics = false;
    private Thread animation;
    private ColorLegendPanel sliderPanel;
    private TIntArrayList previous_vertex_with_labels = new TIntArrayList();
    private double zoom_rate = 1d;
    private TemporalGraph zoomed_graph = null;
    private ChangeListener titlesListener = new ChangeListener() {
        @Override
        public void stateChanged(javax.swing.event.ChangeEvent evt) {
            titlesSpinnerStateChanged(evt);
        }
    };

    /**
     * Creates new form TemporalProjectionViewer
     */
    public TemporalProjectionViewer() {
        super(ScienceViewMainFrame.getInstance());
        this.view = new ViewPanel();
        this.tracker = new Tracker();
        this.report = new ReportView();
        initComponents();
        addModifiersComponents();
    }

    private void addModifiersComponents() {
        //GridBagLayout edgesLayout = (GridBagLayout)this.EdgesPanel.getLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        edgesRangeSlider = new RangeSliderPanel(0, 30, 0, 30);
        edgesRangeSlider.setEnabled(false);
        edgesRangeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                low_value_edges = edgesRangeSlider.getLowValue();
                high_value_edges = edgesRangeSlider.getHighValue();
                updateImage();
            }
        });
        EdgesPanel.add(edgesRangeSlider, c);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        mapWeight = new JCheckBox("Map weight to edge width", false);
        mapWeight.setEnabled(false);
        mapWeight.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    view.mapEdgesWeight(true);
                } else {
                    view.mapEdgesWeight(false);
                }
            }
        });
        EdgesPanel.add(mapWeight, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.insets = new Insets(3, 3, 3, 3);
        c.anchor = GridBagConstraints.WEST;
        this.ColorPanel.add(new JLabel("Choose color scale:"), c);

        ColorScalePanel colorScalePanel = new ColorScalePanel(this);
        colorScalePanel.setColorTable(view.colorTable);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3, 3, 3, 3);
        this.ColorPanel.add(colorScalePanel, c);
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        projectionPanel = new javax.swing.JPanel();
        titlePanel = new javax.swing.JPanel();
        titleTextField = new javax.swing.JTextField();
        controlPanel = new javax.swing.JPanel();
        animation_controlsPanel = new javax.swing.JPanel();
        previous_graphButton = new javax.swing.JButton();
        playButton = new javax.swing.JButton();
        next_graphButton = new javax.swing.JButton();
        projectionScrollPane = new javax.swing.JScrollPane(this.view);
        ModifierPanel = new javax.swing.JPanel();
        EdgesPanel = new javax.swing.JPanel();
        edgesComboBox = new javax.swing.JComboBox(this.edgesComboModel);
        edgesLabel = new javax.swing.JLabel();
        ColorPanel = new javax.swing.JPanel();
        colorLabel = new javax.swing.JLabel();
        colorComboBox = new javax.swing.JComboBox(this.colorComboModel);
        colorLogarithmicCheckBox = new javax.swing.JCheckBox();
        SizePanel = new javax.swing.JPanel();
        sizeLabel = new javax.swing.JLabel();
        sizeComboBox = new javax.swing.JComboBox(this.sizeComboModel);
        sizeLogarithmicCheckBox = new javax.swing.JCheckBox();
        TitlesPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        titlesComboBox = new javax.swing.JComboBox(this.titlesComboModel);
        comparsionComboBox = new javax.swing.JComboBox();
        titlesSpinner = new javax.swing.JSpinner(this.titlesNumberModel);
        ((DefaultFormatter)((JSpinner.DefaultEditor)this.titlesSpinner.getEditor()).getTextField().getFormatter()).setAllowsInvalid(false);
        jScrollPane2 = new javax.swing.JScrollPane();
        reportPanel = new javax.swing.JPanel();
        reportScrollPane = new javax.swing.JScrollPane(this.report);

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameActivated(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        jSplitPane2.setResizeWeight(1.0);
        jSplitPane2.setOneTouchExpandable(true);

        projectionPanel.setLayout(new java.awt.BorderLayout());

        titlePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        titlePanel.setLayout(new java.awt.BorderLayout());

        titleTextField.setEditable(false);
        titlePanel.add(titleTextField, java.awt.BorderLayout.CENTER);

        projectionPanel.add(titlePanel, java.awt.BorderLayout.PAGE_START);

        controlPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        controlPanel.setMinimumSize(new java.awt.Dimension(516, 70));
        controlPanel.setPreferredSize(new java.awt.Dimension(516, 70));
        controlPanel.setLayout(new java.awt.BorderLayout());

        animation_controlsPanel.setLayout(new java.awt.GridBagLayout());

        previous_graphButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/media/StepBack16.gif"))); // NOI18N
        previous_graphButton.setEnabled(false);
        previous_graphButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previous_graphButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        animation_controlsPanel.add(previous_graphButton, gridBagConstraints);

        playButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/media/Play16.gif"))); // NOI18N
        playButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        animation_controlsPanel.add(playButton, gridBagConstraints);

        next_graphButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/media/StepForward16.gif"))); // NOI18N
        next_graphButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                next_graphButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        animation_controlsPanel.add(next_graphButton, gridBagConstraints);

        controlPanel.add(animation_controlsPanel, java.awt.BorderLayout.WEST);

        projectionPanel.add(controlPanel, java.awt.BorderLayout.SOUTH);
        projectionPanel.add(projectionScrollPane, java.awt.BorderLayout.CENTER);

        jSplitPane2.setLeftComponent(projectionPanel);

        ModifierPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Projection Modifier"));
        ModifierPanel.setLayout(new java.awt.GridBagLayout());

        EdgesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Edges"));
        EdgesPanel.setLayout(new java.awt.GridBagLayout());

        edgesComboBox.setMaximumSize(new java.awt.Dimension(150, 27));
        edgesComboBox.setMinimumSize(new java.awt.Dimension(150, 27));
        edgesComboBox.setPreferredSize(new java.awt.Dimension(150, 27));
        edgesComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edgesComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        EdgesPanel.add(edgesComboBox, gridBagConstraints);

        edgesLabel.setText("Type:"); // NOI18N
        edgesLabel.setMaximumSize(new java.awt.Dimension(50, 14));
        edgesLabel.setMinimumSize(new java.awt.Dimension(50, 14));
        edgesLabel.setPreferredSize(new java.awt.Dimension(50, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        EdgesPanel.add(edgesLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        ModifierPanel.add(EdgesPanel, gridBagConstraints);

        ColorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Color"));
        ColorPanel.setLayout(new java.awt.GridBagLayout());

        colorLabel.setText("Color by:"); // NOI18N
        colorLabel.setMaximumSize(new java.awt.Dimension(50, 14));
        colorLabel.setMinimumSize(new java.awt.Dimension(50, 14));
        colorLabel.setPreferredSize(new java.awt.Dimension(50, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        ColorPanel.add(colorLabel, gridBagConstraints);

        colorComboBox.setMaximumSize(new java.awt.Dimension(150, 27));
        colorComboBox.setMinimumSize(new java.awt.Dimension(150, 27));
        colorComboBox.setPreferredSize(new java.awt.Dimension(150, 27));
        colorComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        ColorPanel.add(colorComboBox, gridBagConstraints);

        colorLogarithmicCheckBox.setText("Use logarithmic scale"); // NOI18N
        colorLogarithmicCheckBox.setToolTipText("Indicated when the data covers a large range of values"); // NOI18N
        colorLogarithmicCheckBox.setEnabled(false);
        colorLogarithmicCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorLogarithmicCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        ColorPanel.add(colorLogarithmicCheckBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        ModifierPanel.add(ColorPanel, gridBagConstraints);

        SizePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Size"));
        SizePanel.setLayout(new java.awt.GridBagLayout());

        sizeLabel.setText("Resize by:"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        SizePanel.add(sizeLabel, gridBagConstraints);

        sizeComboBox.setMaximumSize(new java.awt.Dimension(150, 27));
        sizeComboBox.setMinimumSize(new java.awt.Dimension(150, 27));
        sizeComboBox.setPreferredSize(new java.awt.Dimension(150, 27));
        sizeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sizeComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        SizePanel.add(sizeComboBox, gridBagConstraints);

        sizeLogarithmicCheckBox.setText("Use logarithmic scale"); // NOI18N
        sizeLogarithmicCheckBox.setToolTipText("Indicated when the data covers a large range of values"); // NOI18N
        sizeLogarithmicCheckBox.setEnabled(false);
        sizeLogarithmicCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sizeLogarithmicCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        SizePanel.add(sizeLogarithmicCheckBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        ModifierPanel.add(SizePanel, gridBagConstraints);

        TitlesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Titles"));
        TitlesPanel.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Show titles for documents with"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        TitlesPanel.add(jLabel1, gridBagConstraints);

        titlesComboBox.setMaximumSize(new java.awt.Dimension(150, 27));
        titlesComboBox.setMinimumSize(new java.awt.Dimension(150, 27));
        titlesComboBox.setPreferredSize(new java.awt.Dimension(150, 27));
        titlesComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                titlesComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        TitlesPanel.add(titlesComboBox, gridBagConstraints);

        comparsionComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { ">=", ">", "<=", "<", "=" }));
        comparsionComboBox.setEnabled(false);
        comparsionComboBox.setMaximumSize(new java.awt.Dimension(45, 27));
        comparsionComboBox.setMinimumSize(new java.awt.Dimension(45, 27));
        comparsionComboBox.setPreferredSize(new java.awt.Dimension(45, 27));
        comparsionComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comparsionComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        TitlesPanel.add(comparsionComboBox, gridBagConstraints);

        titlesSpinner.setMaximumSize(new java.awt.Dimension(45, 27));
        titlesSpinner.setMinimumSize(new java.awt.Dimension(45, 27));
        titlesSpinner.setPreferredSize(new java.awt.Dimension(45, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        TitlesPanel.add(titlesSpinner, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        ModifierPanel.add(TitlesPanel, gridBagConstraints);

        jSplitPane2.setRightComponent(ModifierPanel);

        jTabbedPane1.addTab("Projection", jSplitPane2);

        reportPanel.setLayout(new java.awt.BorderLayout());
        reportPanel.add(reportScrollPane, java.awt.BorderLayout.CENTER);

        jScrollPane2.setViewportView(reportPanel);

        jTabbedPane1.addTab("Report", jScrollPane2);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);
        jTabbedPane1.getAccessibleContext().setAccessibleName("Projection");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated
        this.mainview.setFocusedJInternalFrame(this);
    }//GEN-LAST:event_formInternalFrameActivated

    public void saveToPngImageFile(String filename) throws IOException {
        if (this.view != null) {
            this.view.saveToPngImageFile(filename);
        }
    }

    public boolean isColorLogarithmic() {
        return this.colorLogarithmicCheckBox.isSelected();
    }

    public void saveToProjectionFile(String filename) throws IOException {
        TemporalGraph graph = this.getGraph();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("DY");
            writer.newLine();
            writer.write(String.valueOf(graph.getVertex().size()));
            writer.newLine();
            writer.write("2");
            writer.newLine();
            writer.write("x;y");
            writer.newLine();
            for (TIntObjectIterator<Vertex> it = graph.getVertex().iterator(); it.hasNext();) {
                it.advance();
                Vertex v = it.value();
                writer.write(v.getId() + ";" + v.getX() + ";" + v.getY() + ";0.0");
                writer.newLine();
                writer.flush();
            }
        }

    }

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        this.mainview.setFocusedJInternalFrame(null);
        System.gc();
    }//GEN-LAST:event_formInternalFrameClosing

private void comparsionComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comparsionComboBoxActionPerformed
    titlesSpinnerStateChanged(null);
}//GEN-LAST:event_comparsionComboBoxActionPerformed

private void titlesComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_titlesComboBoxActionPerformed
    Scalar scalar = (Scalar) this.titlesComboBox.getSelectedItem();
    if (scalar.toString().compareToIgnoreCase(PExConstants.DOTS) == 0) {
        this.titlesSpinner.removeChangeListener(titlesListener);
        this.titlesNumberModel.setMaximum(0);
        this.titlesNumberModel.setMinimum(0);
        this.titlesSpinner.setEnabled(false);
        this.comparsionComboBox.setSelectedIndex(0);
        this.comparsionComboBox.setEnabled(false);
        this.titlesNumberModel.setValue(0);
        for (TIntIterator it = previous_vertex_with_labels.iterator(); it.hasNext();) {
            this.getGraph().getVertexById(it.next()).setShowLabel(false);
        }
        this.updateImage();
    } else {
        this.comparsionComboBox.setEnabled(true);
        this.titlesSpinner.setEnabled(true);
        this.titlesNumberModel.setMaximum((int) scalar.getMax());
        this.titlesNumberModel.setMinimum((int) scalar.getMin());
        this.titlesNumberModel.setValue((int) scalar.getMax());
        this.titlesSpinner.addChangeListener(titlesListener);
        titlesSpinnerStateChanged(null);
    }
}//GEN-LAST:event_titlesComboBoxActionPerformed

private void sizeLogarithmicCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sizeLogarithmicCheckBoxActionPerformed
    Scalar scalar = (Scalar) this.sizeComboBox.getSelectedItem();
    if (scalar != null) {
        this.view.resizeAs(scalar, this.sizeLogarithmicCheckBox.isSelected());
    }
}//GEN-LAST:event_sizeLogarithmicCheckBoxActionPerformed

private void sizeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sizeComboBoxActionPerformed
    Scalar scalar = (Scalar) this.sizeComboBox.getSelectedItem();
    if (scalar != null) {
        if (scalar.toString().compareTo(PExConstants.DOTS) == 0 || scalar.toString().compareTo(PExConstants.YEAR) == 0) {
            this.sizeLogarithmicCheckBox.setSelected(false);
            this.sizeLogarithmicCheckBox.setEnabled(false);
        } else {
            this.sizeLogarithmicCheckBox.setEnabled(true);
        }
        this.view.resizeAs(scalar, this.sizeLogarithmicCheckBox.isSelected());
    }
}//GEN-LAST:event_sizeComboBoxActionPerformed

private void colorLogarithmicCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorLogarithmicCheckBoxActionPerformed
    Scalar scalar = (Scalar) this.colorComboBox.getSelectedItem();
    if (scalar != null) {
        this.view.colorAs(scalar, this.colorLogarithmicCheckBox.isSelected());
    }
}//GEN-LAST:event_colorLogarithmicCheckBoxActionPerformed

private void colorComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorComboBoxActionPerformed
    Scalar scalar = (Scalar) this.colorComboBox.getSelectedItem();
    if (scalar != null) {
        if (scalar.toString().compareTo(PExConstants.DOTS) == 0) {
            this.colorLogarithmicCheckBox.setSelected(false);
            this.colorLogarithmicCheckBox.setEnabled(false);
            this.sliderPanel.removeYearLegend();
        } else if (scalar.toString().compareTo(PExConstants.YEAR) == 0) {
            this.colorLogarithmicCheckBox.setSelected(false);
            this.colorLogarithmicCheckBox.setEnabled(false);
            this.sliderPanel.addYearLegend();
        } else {
            this.colorLogarithmicCheckBox.setEnabled(true);
            this.sliderPanel.removeYearLegend();
        }


        this.view.colorAs(scalar, this.colorLogarithmicCheckBox.isSelected());
    }
}//GEN-LAST:event_colorComboBoxActionPerformed

private void edgesComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edgesComboBoxActionPerformed
    Connectivity con = this.getCurrentConnectivity();
    if (con != null) {
        this.repaint();
        if (!con.getType().equals(ConnectivityType.NONE) && !con.getType().equals(ConnectivityType.CORE_CITATIONS)) {
            con = projection.getConnectivity(projection.getYearWithIndex(projection.getNumberOfYears() - 1), con.getType());
//            if (con.getType().equals(ConnectivityType.SIMILARITY)) {
//                this.edgesRangeSlider.setIsSimilarity(true);
//                this.edgesRangeSlider.setParameters(0, 10, 0, 10);
//            } else {
            this.edgesRangeSlider.setParameters((int) con.getMinWeight(), (int) con.getMaxWeight(), (int) con.getMinWeight(), (int) con.getMaxWeight());
//            }

            this.low_value_edges = con.getMinWeight();
            this.high_value_edges = con.getMaxWeight();
            this.edgesRangeSlider.setEnabled(true);
            this.mapWeight.setEnabled(true);
        } else {
            this.edgesRangeSlider.setEnabled(false);
            this.mapWeight.setEnabled(false);
        }
    }
}//GEN-LAST:event_edgesComboBoxActionPerformed

private void next_graphButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_next_graphButtonActionPerformed
    //  this.yearsSlider.setValue(this.yearsSlider.getValue() + 1);
    int index = Arrays.binarySearch(this.indexesUpdateDocumentsTree, this.sliderPanel.getValue());
    if (index < 0) {
        index = (index + 1) * -1;
        if (index != projection.getNumberOfYears()) {
            this.sliderPanel.setValue(this.indexesUpdateDocumentsTree[index]);
        }
    } else {
        if (index != projection.getNumberOfYears()) {
            this.sliderPanel.setValue(this.indexesUpdateDocumentsTree[index + 1]);
        }
    }
}//GEN-LAST:event_next_graphButtonActionPerformed

private void playButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playButtonActionPerformed
    if (!animate) {
        animationChanged(true);
        this.startAnimation();
    } else {
        animationChanged(false);
        this.animation.stop();
    }
}//GEN-LAST:event_playButtonActionPerformed

private void previous_graphButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previous_graphButtonActionPerformed
    //    this.yearsSlider.setValue(this.yearsSlider.getValue() - 1);
    int index = Arrays.binarySearch(this.indexesUpdateDocumentsTree, this.sliderPanel.getValue());
    if (index < 0) {
        index = (index + 1) * -1;
        if (index != 0) {
            this.sliderPanel.setValue(this.indexesUpdateDocumentsTree[index - 1]);
        }
    } else {
        if (index != 0) {
            this.sliderPanel.setValue(this.indexesUpdateDocumentsTree[index - 1]);
        }
    }
}//GEN-LAST:event_previous_graphButtonActionPerformed

    private void titlesSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {
        if (this.titlesComboBox.getSelectedIndex() == 1) {
            int[] ids = projection.getDatabaseCorpus().getDocumentsWithLCC((Integer) this.titlesNumberModel.getValue(), (String) this.comparsionComboBox.getSelectedItem());
            int id_doc;
            for (TIntIterator it = previous_vertex_with_labels.iterator(); it.hasNext();) {
                this.getGraph().getVertexById(it.next()).setShowLabel(false);
            }
            for (int i = 0; i < ids.length; i++) {
                id_doc = ids[i];
                this.getGraph().getVertexById(id_doc).setShowLabel(true);
                this.previous_vertex_with_labels.add(id_doc);
            }
            this.updateImage();
        } else if (this.titlesComboBox.getSelectedIndex() == 2) {
            int[] ids = projection.getDatabaseCorpus().getDocumentsWithGCC((Integer) this.titlesNumberModel.getValue(), (String) this.comparsionComboBox.getSelectedItem());
            int id_doc;
            for (TIntIterator it = previous_vertex_with_labels.iterator(); it.hasNext();) {
                this.getGraph().getVertexById(it.next()).setShowLabel(false);
            }
            for (int i = 0; i < ids.length; i++) {
                id_doc = ids[i];
                this.getGraph().getVertexById(id_doc).setShowLabel(true);
                this.previous_vertex_with_labels.add(id_doc);
            }
            this.updateImage();
        }
    }

    private void startAnimation() {
        animation = new Thread() {
            @Override
            public void run() {
                for (int i = sliderPanel.getValue(); i < sliderPanel.getMaximum(); i++) {
                    sliderPanel.setValue(sliderPanel.getValue() + 1);
                    try {
                        Thread.sleep(60);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TemporalProjectionViewer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                animationChanged(false);
            }
        };
        animation.start();
    }

    public void animationChanged(boolean param) {
        if (!animate) {
            animate = true;
            this.playButton.setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/media/Pause16.gif")));
            this.playButton.setToolTipText("Pause Animation");
        } else {
            this.playButton.setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/media/Play16.gif")));
            this.playButton.setToolTipText("Play Animation");
            animate = false;
        }
    }

    public DefaultMutableTreeNode createTopicsTree() {
        StringBuilder aux;
        DefaultMutableTreeNode root = null, cluster;
        TemporalGraph graph = this.getGraph();
        ArrayList<Topic> topics_year = graph.getTopics();
        if (topics_year != null) {
            aux = new StringBuilder(String.valueOf(graph.getYear()));
            root = new DefaultMutableTreeNode(aux.toString());
            for (Topic t : topics_year) {
                if (!t.toString().isEmpty()) {
                    cluster = new DefaultMutableTreeNode(t);
                    ArrayList<Vertex> vertex = t.getVertexList();
                    Collections.sort(vertex, new Comparator<Vertex>() {
                        @Override
                        public int compare(Vertex o1, Vertex o2) {
                            return Integer.compare(o1.getPublishedYear(), o2.getPublishedYear());
                        }
                    });
                    for (Vertex v : vertex) {
                        aux = new StringBuilder(String.valueOf(v.getPublishedYear())).append(" - ").append(projection.getDatabaseCorpus().getTitle(v.getId()));
                        cluster.add(new DefaultMutableTreeNode(new DocumentInfo(aux.toString(), v.getId())));
                    }
                    root.add(cluster);
                }

            }
        }
        return root;
    }

    private void createDocumentsTree() {
        int id_doc;
        DefaultMutableTreeNode year_on_tree;
        int[] ids;
        StringBuilder aux;
        for (Integer year : projection.getYears()) {
            ids = projection.getDatabaseCorpus().getDocumentsIdsSortedByTitle(year);
            aux = new StringBuilder(String.valueOf(year)).append(" [").append(ids.length).append(" documents]");
            year_on_tree = new DefaultMutableTreeNode(aux.toString());
            for (int j = 0; j < ids.length; j++) {
                id_doc = ids[j];
                aux = new StringBuilder(String.valueOf(year)).append(" - ").append(projection.getDatabaseCorpus().getTitle(id_doc));
                year_on_tree.add(new DefaultMutableTreeNode(new DocumentInfo(aux.toString(), id_doc)));
            }
            this.documentsTreeMap.add(year_on_tree);
        }
    }

    /**
     * Listen to the slider.
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        int value = this.sliderPanel.getValue();
        if (value == 0) {
            this.next_graphButton.setEnabled(true);
            this.previous_graphButton.setEnabled(false);
        } else if (value == this.sliderPanel.getMaximum()) {
            this.next_graphButton.setEnabled(false);
            this.previous_graphButton.setEnabled(true);
        } else {
            this.next_graphButton.setEnabled(true);
            this.previous_graphButton.setEnabled(true);
        }
        TemporalGraph graph = this.getGraph();
        graph.unselectAllTopics();
        this.updateDocumentasAndTopicsTrees();
        setGraph(graph);

        if (this.view.markedVertexId != null) {
            this.view.markNeighbors(this.view.markedVertexId);
        }

        if (this.view.selectedTopic != null) {
            for (Topic t : graph.getTopics()) {
                if (view.selectedTopic.equals(t)) {
                    view.selectedTopic = t;
                    t.setSelected(true);
                    break;
                }
            }
        }
    }

    public void updateTopicsTree() {
        this.mainview.updateTopicsTree();
    }

    public void updateDocumentasAndTopicsTrees() {
        int index = Arrays.binarySearch(indexesUpdateDocumentsTree, sliderPanel.getValue());
        if (index < 0) {
            //TEST int currentIndex = (index + 1) * (-1);
            int currentIndex = (index * -1) + 1;
            if (currentIndex != lastIndex) {
                this.mainview.updateDocumentsTree(documentsTreeMap, this.getYearIndex());
                mainview.recreatingLists();
                this.lastIndex = currentIndex;
            }
        } else {
            this.mainview.updateDocumentsTree(documentsTreeMap, this.getYearIndex());
            mainview.recreatingLists();
        }
        this.mainview.updateTopicsTree();
    }

    public void selectVertices(ArrayList<Vertex> vertices) {
        if (this.view != null) {
            this.view.markVertices(vertices);
        }
    }

    public void selectVertices() {
        if (this.view.selectedVerticesId != null) {
            ArrayList<Vertex> vertices = new ArrayList<>();
            Vertex v;
            for (Integer currentid : this.view.selectedVerticesId) {
                v = this.getGraph().getVertexById(currentid);
                if (v != null) {
                    vertices.add(v);
                }
            }
            this.view.markVertices(vertices);
        }
    }

    public TemporalProjection getTemporalProjection() {
        return this.projection;
    }

    public ProjectionData getProjectionData() {
        return this.projection.getProjectionData();
    }

    @Override
    public void setFont(java.awt.Font font) {
        this.font = font;
        if (this.view != null) {
            this.view.repaint();
        }
    }

    @Override
    public java.awt.Font getFont() {
        return this.font;
    }

    public void setTemporalProjection(TemporalProjection projection) {
        if (projection != null && projection.getNumberOfYears() > 0) {
            this.projection = projection;

            this.tracker.setGraph(projection.getLastGraph());
            sliderPanel = new ColorLegendPanel(this);

            this.sliderPanel.setMinimum(0);
            this.sliderPanel.setMaximum(projection.getNumberOfYears() * TemporalProjection.getN() - 1);
            this.sliderPanel.setValue(0);
            this.sliderPanel.setMajorTickSpacing(TemporalProjection.getN());

            this.setGraph(this.getGraph());
            Hashtable labelTable = new Hashtable();
            int[] years = projection.getYears();
            indexesUpdateDocumentsTree = new int[years.length + 1];
            this.indexesUpdateTopicsTree = new int[2 * years.length + 1];
            this.indexesUpdateDocumentsTree[0] = 0;
            this.indexesUpdateTopicsTree[0] = 0;
            this.indexesUpdateTopicsTree[1] = TemporalProjection.getN() / 2;
            labelTable.put(Integer.valueOf(0), new JLabel("Start"));

            for (int i = 1; i < projection.getNumberOfYears(); i++) {
                labelTable.put(Integer.valueOf(i * TemporalProjection.getN() - 1), new JLabel(Integer.toString(years[i - 1])));
                this.indexesUpdateDocumentsTree[i] = i * TemporalProjection.getN() - 1;
                this.indexesUpdateTopicsTree[2 * i] = i * TemporalProjection.getN() - 1;
                this.indexesUpdateTopicsTree[2 * i + 1] = i * TemporalProjection.getN() + TemporalProjection.getN() / 2;
            }
            labelTable.put(Integer.valueOf(sliderPanel.getMaximum()), new JLabel(Integer.toString(years[years.length - 1])));
            this.indexesUpdateDocumentsTree[projection.getNumberOfYears()] = sliderPanel.getMaximum();
            this.indexesUpdateTopicsTree[2 * projection.getNumberOfYears() - 1] = sliderPanel.getMaximum() - TemporalProjection.getN() / 2;
            this.indexesUpdateTopicsTree[2 * projection.getNumberOfYears()] = sliderPanel.getMaximum();
            sliderPanel.setLabelTable(labelTable);
            sliderPanel.setPaintLabels(true);
            controlPanel.add(sliderPanel, java.awt.BorderLayout.CENTER);

            this.createDocumentsTree();
            this.updateDocumentasAndTopicsTrees();
            this.updateScalars();
            this.updateConnectivities();
            this.updateTitlesComboModel();
            sliderPanel.addChangeListener(this);

            this.report.reset(this.projection.getProjectionData(), this.projection.getTopicData());
//            this.saveParametersInFile();
        }
    }

    public void updateReport() {
        this.report.reset(this.projection.getProjectionData(), this.projection.getTopicData());
    }

    public int getYearIndex() {
        return (this.sliderPanel.getValue()) / TemporalProjection.getN();
    }

    public int getYearIndexForValue(int value) {
        return value / TemporalProjection.getN();
    }

    public int getCurrentYear() {
        return projection.getYearWithIndex(getYearIndex());
    }

    @Override
    public TemporalGraph getGraph() {
        int year = projection.getYearWithIndex(getYearIndex());
        if (year != -1) {
            return this.zoom(projection.getGraphs().get(year).get(this.sliderPanel.getValue() % TemporalProjection.getN()), zoom_rate);
        }

        return null;
    }

    public void setGraph(TemporalGraph graph) {
        if (graph != null) {
            this.updateImage();
        }
    }

    public void markVertex(Vertex v) {
        this.view.markNeighbors(v.getId());
    }

    @Override
    public void resizeAs(Scalar scalar) {
        if (this.view != null) {
            this.view.resizeAs(scalar, colorLogarithmicCheckBox.isSelected());
        }
    }

    @Override
    public void colorAs(Scalar scalar) {
        if (this.view != null) {
            this.view.colorAs(scalar, colorLogarithmicCheckBox.isSelected());
        }
    }

    @SuppressWarnings("unchecked")
    public void updateTitlesComboModel() {
        this.titlesComboModel.removeAllElements();
        Scalar s;
        this.titlesComboModel.addElement(this.projection.getVertexScalarByName(PExConstants.DOTS));
        if ((s = this.projection.getVertexScalarByName(PExConstants.LOCAL_CITATION_COUNT)) != null) {
            this.titlesComboModel.addElement(s);
        }
        if ((s = this.projection.getVertexScalarByName(PExConstants.GLOBAL_CITATION_COUNT)) != null) {
            this.titlesComboModel.addElement(s);
        }

    }

    @Override
    public void updateScalars(Scalar scalar) {
        this.colorComboModel.removeAllElements();
        this.sizeComboModel.removeAllElements();
        for (Scalar s : this.projection.getVertexScalars()) {
            this.colorComboModel.addElement(s);
            this.sizeComboModel.addElement(s);
        }
        if (scalar != null) {
            this.colorComboBox.setSelectedItem(scalar);
        }
        this.setGraphChanged(true);
    }

    @Override
    public void updateScalars() {
        String selected_scalarColor = null, selected_scalarSize = null;
        if (this.colorComboModel.getSelectedItem() != null) {
            selected_scalarColor = ((Scalar) this.colorComboModel.getSelectedItem()).toString();
        }
        if (this.sizeComboModel.getSelectedItem() != null) {
            selected_scalarSize = ((Scalar) this.sizeComboModel.getSelectedItem()).toString();
        }
        this.colorComboModel.removeAllElements();
        this.sizeComboModel.removeAllElements();

        for (Scalar s : this.projection.getVertexScalars()) {
            this.colorComboModel.addElement(s);
            this.sizeComboModel.addElement(s);
            if (selected_scalarColor != null && s.toString().compareTo(selected_scalarColor) == 0) {
                this.colorComboModel.setSelectedItem(s);
            }
            if (selected_scalarSize != null && s.toString().compareTo(selected_scalarSize) == 0) {
                this.sizeComboModel.setSelectedItem(s);
            }
        }
        this.setGraphChanged(true);
    }

    @Override
    public void updateConnectivities() {
        ConnectivityType selected_connectivity = null;
        if (this.edgesComboModel.getSelectedItem() != null) {
            selected_connectivity = ((ConnectivityType) this.edgesComboModel.getSelectedItem());
        }
        this.edgesComboModel.removeAllElements();
        TemporalGraph graph = this.getGraph();
        if (graph != null) {
            for (ConnectivityType con_type : ConnectivityType.getTypes()) {
                this.edgesComboModel.addElement(con_type);
                if (selected_connectivity != null && con_type.equals(selected_connectivity)) {
                    this.edgesComboModel.setSelectedItem(con_type);
                }
            }
            this.edgesComboBox.setModel(edgesComboModel);
            this.setGraphChanged(true);
        }

    }

    @Override
    public void updateImage() {
        if (this.view != null) {
            this.view.updateUI();
        }
        if (this.tracker != null) {
            this.tracker.updateUI();
        }
    }

    @Override
    public void updateTitles() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Scalar getCurrentScalar() {
        return (Scalar) this.colorComboModel.getSelectedItem();
    }

    @Override
    public Connectivity getCurrentConnectivity() {
        return this.projection.getConnectivity(getGraph().getYear(), (ConnectivityType) this.edgesComboBox.getSelectedItem());
    }

    @Override
    public ColorTable getColorTable() {
        return this.view.colorTable;
    }

    public void setUpdatingTopics(boolean value) {
        this.view.setUpdatingTopics(value);
    }

    public DatabaseCorpus getCorpus() {
        return this.projection.getDatabaseCorpus();
    }

    @Override
    public void cleanTopics() {
        for (Iterator<Entry<Integer, ArrayList<TemporalGraph>>> it = this.projection.getGraphs().entrySet().iterator(); it.hasNext();) {
            Entry<Integer, ArrayList<TemporalGraph>> entry = it.next();
            for (TemporalGraph g : entry.getValue()) {
                g.getTopics().clear();
            }
        }
        this.projection.getTopicData().setNextAvailableId(1);
        ScienceViewMainFrame.getInstance().clearTopicsTree();
        this.view.repaint();
    }

    @Override
    public void cleanSelection(boolean cleanVertexLabels) {
        if (this.view != null) {
            this.view.cleanMarkedVertices(cleanVertexLabels);

        }
    }

    @Override
    public Font getViewerFont() {
        if (this.view != null) {
            return this.view.getFont();
        }
        return null;
    }

    @Override
    public void setViewerFont(Font font) {
        if (this.view != null) {
            this.view.setFont(font);
        }
    }

    public void setZoomRate(double zoom_rate) {
        this.zoom_rate = zoom_rate;
    }

    public void resetScrollBars() {
        this.projectionScrollPane.getVerticalScrollBar().getModel().setValue(-1);
        this.projectionScrollPane.getHorizontalScrollBar().getModel().setValue(-1);
    }

    private TemporalGraph zoom(TemporalGraph original_graph, double rate) {
        try {
            if (rate == 1) {
                if (zoomed_graph != null) {
                    for (Topic t : this.zoomed_graph.getTopics()) {
                        if (original_graph.getTopics().indexOf(t) == -1) {
                            Topic aux = (Topic) t.clone();
                            for (Vertex v : aux.getVertexList()) {
                                v.setX(original_graph.getVertexById(v.getId()).getX());
                                v.setY(original_graph.getVertexById(v.getId()).getY());
                            }
                            aux.setTemporalGraph(original_graph);
                            aux.calcPolygon();
                            original_graph.addTopic(aux);
                        }
                    }
                }
                this.zoomed_graph = null;
                return original_graph;
            }
            if (zoomed_graph != null && original_graph.equals(zoomed_graph) && rate == zoomed_graph.getZoomRate()) {
                return this.zoomed_graph;
            }
            if (rate != 1) {
                ArrayList<Topic> aux = null;
                if (zoomed_graph != null) {
                    aux = new ArrayList<>(zoomed_graph.getTopics().size());
                    for (Topic t : zoomed_graph.getTopics()) {
                        aux.add((Topic) t.clone());
                    }
                }
                zoomed_graph = (TemporalGraph) original_graph.clone();
                TIntObjectHashMap<Vertex> vertex = zoomed_graph.getVertex();
                double maxX = vertex.get(vertex.keys()[0]).getX();
                double minX = vertex.get(vertex.keys()[0]).getX();
                double maxY = vertex.get(vertex.keys()[0]).getY();
                double minY = vertex.get(vertex.keys()[0]).getY();

                //Encontra o maior e menor valores para X e Y
                for (TIntObjectIterator<Vertex> it = vertex.iterator(); it.hasNext();) {
                    it.advance();
                    Vertex v = it.value();
                    if (maxX < v.getX()) {
                        maxX = v.getX();
                    } else if (minX > v.getX()) {
                        minX = v.getX();
                    }

                    if (maxY < v.getY()) {
                        maxY = v.getY();
                    } else if (minY > v.getY()) {
                        minY = v.getY();
                    }

                }

                double endX = maxX * rate;
                double endY = maxY * rate;
                zoomed_graph.setMaxX(endX);
                zoomed_graph.setMaxY(endY);

                //Normalizo vértices normais
                for (TIntObjectIterator<Vertex> it = vertex.iterator(); it.hasNext();) {
                    it.advance();
                    Vertex v = it.value();
                    if (maxX != minX) {
                        v.setX((((v.getX() - minX) / (maxX - minX)) * (endX - minX)) + minX);
                    } else {
                        v.setX(minX);
                    }

                    if (maxY != minY) {
                        v.setY(((((v.getY() - minY) / (maxY - minY)) * (endY - minY)) + minY));
                    } else {
                        v.setY(minY);
                    }

                }
                //Change the size of the panel according to the graph
                this.view.setPreferredSize(new Dimension((int) ((this.projection.getMaxx() + (Vertex.getRayBase() * 5) + 350) * rate), (int) ((this.projection.getMaxy() + (Vertex.getRayBase() * 5)) * rate)));
                this.view.setSize(new Dimension((int) ((this.projection.getMaxx() + (Vertex.getRayBase() * 5) + 350) * rate), (int) ((this.projection.getMaxy() + (Vertex.getRayBase() * 5)) * rate)));

                for (Topic topic : zoomed_graph.getTopics()) {
                    if (aux != null) {
                        int index_topic_in_aux = aux.indexOf(topic);
                        if (index_topic_in_aux != -1) {
                            aux.remove(index_topic_in_aux);
                        }
                    }
                    for (Vertex v : topic.getVertexList()) {
                        v.setX(zoomed_graph.getVertexById(v.getId()).getX());
                        v.setY(zoomed_graph.getVertexById(v.getId()).getY());
                    }
                    for (Coordinate coord : topic.getFakeVertexList()) {
                        if (maxX != minX) {
                            coord.x = (((coord.x - minX) / (maxX - minX)) * (endX - minX)) + minX;
                        } else {
                            coord.x = minX;
                        }
                        if (maxY != minY) {
                            coord.y = (((coord.y - minY) / (maxY - minY)) * (endY - minY)) + minY;
                        } else {
                            coord.y = minY;
                        }
                    }
                    topic.calcPolygon();

                }
                if (aux != null) {
                    for (Topic topic : aux) {
                        for (Vertex v : topic.getVertexList()) {
                            v.setX(zoomed_graph.getVertexById(v.getId()).getX());
                            v.setY(zoomed_graph.getVertexById(v.getId()).getY());
                        }
                        for (Coordinate coord : topic.getFakeVertexList()) {
                            if (maxX != minX) {
                                coord.x = (((coord.x - minX) / (maxX - minX)) * (endX - minX)) + minX;
                            } else {
                                coord.x = minX;
                            }
                            if (maxY != minY) {
                                coord.y = (((coord.y - minY) / (maxY - minY)) * (endY - minY)) + minY;
                            } else {
                                coord.y = minY;
                            }
                        }
                        topic.setTemporalGraph(zoomed_graph);
                        topic.calcPolygon();
                        zoomed_graph.addTopic(topic);
                    }
                }
                zoomed_graph.setZoomRate(rate);
                return zoomed_graph;
            }
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(TemporalProjectionViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

//    public void zoom2(double rate) {
//        TreeMap<Integer, ArrayList<TemporalGraph>> graphs = projection.getGraphs();
//        double maxX = projection.getMaxx(), minX = projection.getMinx(), maxY = projection.getMaxy(), minY = projection.getMiny();
//        double endX = maxX * rate, endY = maxY * rate;
//        for (ArrayList<TemporalGraph> graphs_year : graphs.values()) {
//            for (TemporalGraph tgraph : graphs_year) {
//                for (TIntObjectIterator<Vertex> it = tgraph.getVertex().iterator(); it.hasNext();) {
//                    it.advance();
//                    Vertex v = it.value();
//                    if (maxX != minX) {
//                        v.setX((((v.getX() - minX) / (maxX - minX)) * (endX - minX)) + minX);
//                    } else {
//                        v.setX(minX);
//                    }
//
//                    if (maxY != minY) {
//                        v.setY(((((v.getY() - minY) / (maxY - minY)) * (endY - minY)) + minY));
//                    } else {
//                        v.setY(minY);
//                    }
//                }
//                for (Topic topic : tgraph.getTopics()) {
//                    topic.calcPolygon();
//                }
//            }
//        }
//        endX = ((endX + (Vertex.getRayBase() * 5)) + 350) * 2;
//        endY = ((endY + (Vertex.getRayBase() * 5)) + 350) * 2;
//        //Change the size of the panel according to the graph
//        this.view.setPreferredSize(new Dimension((int) endX, (int) endY));
//        this.view.setSize(new Dimension((int) endX, (int) endY));
//        this.updateImage();
//    }
//    public void zoom(float rate) {
//        TemporalGraph graph = this.getGraph();
//        if (graph != null) {
//            double maxX = Double.NEGATIVE_INFINITY;
//            double minX = Double.POSITIVE_INFINITY;
//            double maxY = Double.NEGATIVE_INFINITY;
//            double minY = Double.NEGATIVE_INFINITY;
//
//            //Encontra o maior e menor valores para X e Y
//            for (TIntObjectIterator<Vertex> it = graph.getVertex().iterator(); it.hasNext();) {
//                it.advance();
//                Vertex v = it.value();
//                if (maxX < v.getX()) {
//                    maxX = v.getX();
//                } else if (minX > v.getX()) {
//                    minX = v.getX();
//                }
//
//                if (maxY < v.getY()) {
//                    maxY = v.getY();
//                } else if (minY > v.getY()) {
//                    minY = v.getY();
//                }
//            }
//
//            double endX = maxX * rate;
//            double endY = maxY * rate;
//
//            //Normalizo
//            for (TIntObjectIterator<Vertex> it = graph.getVertex().iterator(); it.hasNext();) {
//                it.advance();
//                Vertex v = it.value();
//                if (maxX != minX) {
//                    v.setX((((v.getX() - minX) / (maxX - minX)) * (endX - minX)) + minX);
//                } else {
//                    v.setX(minX);
//                }
//
//                if (maxY != minY) {
//                    v.setY(((((v.getY() - minY) / (maxY - minY)) * (endY - minY)) + minY));
//                } else {
//                    v.setY(minY);
//                }
//            }
//
//            //Change the size of the panel according to the graph
//            this.view.setPreferredSize(new Dimension(graph.getSize().width * 2, graph.getSize().height * 2));
//            this.view.setSize(new Dimension(graph.getSize().width * 2, graph.getSize().height * 2));
//
//            this.updateImage();
//        }
//    }
    public void setViewerBackground(Color bg) {
        if (this.view != null) {
            this.view.setBackground(bg);
        }
    }

    public void setSelectedTopic(Topic topic) {
        if (this.view.selectedTopic != null) {
            this.view.selectedTopic.setSelected(false);
            this.view.selectedTopic = null;
        }

        this.view.selectedTopic = topic;
        this.view.selectedTopic.setSelected(true);
    }

    public final class ViewPanel extends JPanel {

        private static final long serialVersionUID = 1L;
        public ColorTable colorTable;
        private ColorScalePanel csp;
        private Integer markedVertexId;
        private String toolTipLabel = "";
        private Point toolTipPosition;
        //Used to select points with the retangle
        private java.awt.Point source = null;
        private java.awt.Point target = null;
        //Used to select points with the polygon
        private java.awt.Polygon polygon;
        private java.awt.Color color = java.awt.Color.RED;
        private ArrayList<Integer> selectedVerticesId;
        //the topic been selected
        private Topic selectedTopic = null;

        public ViewPanel() {
            this.setLayout(new FlowLayout(FlowLayout.LEFT));
            this.colorTable = new ColorTable();

            this.setBackground(Color.WHITE);
//            this.setDoubleBuffered(true);

            this.addMouseListener(new MouseClickedListener());
            this.addMouseMotionListener(new MouseMotionListener());
            MouseWheelListener listener = new MouseWheelListener() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    if (e.isControlDown()) {
                        zoom_rate = ScienceViewMainFrame.getInstance().setZoom(-e.getWheelRotation());
                    }
                }
            };
            this.addMouseWheelListener(listener);
        }

        @Override
        public void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);

            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_DEFAULT);
            g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DEFAULT);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            TemporalGraph graph = getGraph();

            BufferedImage imageBuffer = null;
            if (graph != null) {
                Topic selected_topic = null;
                TIntArrayList selected_ids = null;
                imageBuffer = new BufferedImage(graph.getSize().width + 150, graph.getSize().height + 130, BufferedImage.TYPE_INT_RGB);
                java.awt.Graphics2D g2Buffer = imageBuffer.createGraphics();
                g2Buffer.setColor(this.getBackground());
                g2Buffer.fillRect(0, 0, graph.getSize().width + 150, graph.getSize().height + 130);
                if (highQualityRender) {
                    g2Buffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                } else {
                    g2Buffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                }

                if (!updating_topics) {
                    for (Topic topic : graph.getTopics()) {
                        if (!topic.isSelected()) {
                            topic.drawTopic(imageBuffer, null, highQualityRender);
                        } else {
                            selected_topic = topic;
                            selected_ids = selected_topic.getVertexIdList();
                        }
                    }
                }

                Scalar scalar_color = (Scalar) colorComboBox.getSelectedItem();
                Scalar scalar_size = (Scalar) sizeComboBox.getSelectedItem();
                if (scalar_color != null && scalar_size != null) {
                    for (TIntObjectIterator<Vertex> it = graph.getVertex().iterator(); it.hasNext();) {
                        it.advance();
                        Vertex v = it.value();
                        v.setColor(scalar_color, this.colorTable, colorLogarithmicCheckBox.isSelected());
                        v.setRayFactor(scalar_size, sizeLogarithmicCheckBox.isSelected());
                    }
                } else if (scalar_color != null) {
                    for (TIntObjectIterator<Vertex> it = graph.getVertex().iterator(); it.hasNext();) {
                        it.advance();
                        it.value().setColor(scalar_color, this.colorTable, colorLogarithmicCheckBox.isSelected());
                    }
                } else if (scalar_size != null) {
                    for (TIntObjectIterator<Vertex> it = graph.getVertex().iterator(); it.hasNext();) {
                        it.advance();
                        it.value().setRayFactor(scalar_size, sizeLogarithmicCheckBox.isSelected());
                    }
                }

                boolean globalsel = false;
                TIntObjectIterator<Vertex> iterator = graph.getVertex().iterator();
                while (iterator.hasNext()) {
                    iterator.advance();
                    if (iterator.value().isSelected()) {
                        globalsel = true;
                    }
                }
                Connectivity connectivity = getCurrentConnectivity();
                if (connectivity != null) {
                    ArrayList<Edge> edges;
                    if (!connectivity.isWeighted()) {
                        edges = connectivity.getEdges();
                    } else {
                        edges = connectivity.getEdgesWithinRange(low_value_edges, high_value_edges);
                    } //Draw each edges of the graph
                    for (Edge edge : edges) {
                        edge.draw(imageBuffer, null, globalsel, highQualityRender, connectivity.getName(), graph);
                    }
                }

                //Draw each vertice of the graph
                TIntObjectIterator<Vertex> iterator_vertex = graph.getVertex().iterator();
                if (selected_ids != null) {
                    while (iterator_vertex.hasNext()) {
                        iterator_vertex.advance();
                        Vertex v = iterator_vertex.value();
                        if (!selected_ids.contains(v.getId())) {
                            if (!v.isSelected()) {
                                v.draw(imageBuffer, null, false, highQualityRender);
                            } else {
                                selected_ids.add(v.getId());
                            }
                        }
                    }
                } else {
                    while (iterator_vertex.hasNext()) {
                        iterator_vertex.advance();
                        Vertex v = iterator_vertex.value();
                        if (!v.isSelected()) {
                            v.draw(imageBuffer, g2, globalsel, highQualityRender);
                        } else {
                            selected_ids = new TIntArrayList(1);
                            selected_ids.add(v.getId());
                        }
                    }
                }

                if (!updating_topics) {
                    for (Topic topic : graph.getTopics()) {
                        if (!topic.isSelected()) {
                            if (this.selectedTopic != null) {
                                topic.setUseGrayColor(true);
                            }
                            topic.drawTopicTerm(imageBuffer, null, this.getFont(), this, highQualityRender);
                        }
                    }
                }

                if (!updating_topics && selected_topic != null) {
                    selected_topic.drawTopic(imageBuffer, g2, highQualityRender);
                }
                //Draw selected vertices of the graph
                if (selected_ids != null) {
                    TIntIterator it = selected_ids.iterator();
                    while (it.hasNext()) {
                        graph.getVertexById(it.next()).draw(imageBuffer, null, false, highQualityRender);
                    }
                }
                if (!updating_topics && selected_topic != null) {
                    selected_topic.drawTopicTerm(imageBuffer, null, this.getFont(), this, highQualityRender);
                    selected_topic.setUseGrayColor(false);
                }
                g2Buffer.dispose();
            }
            if (imageBuffer
                    != null) {
                g2.drawImage(imageBuffer, 0, 0, null);
            }
            //Draw the rectangle to select the points
            if (this.source != null && this.target
                    != null) {
                int x = this.source.x;
                int width = this.target.x - this.source.x;

                int y = this.source.y;
                int height = this.target.y - this.source.y;

                if (this.source.x > this.target.x) {
                    x = this.target.x;
                    width = this.source.x - this.target.x;
                }

                if (this.source.y > this.target.y) {
                    y = this.target.y;
                    height = this.source.y - this.target.y;
                }
                g2.setColor(this.color);
                g2.drawRect(x, y, width, height);

                g2.setComposite(AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.45f));
                g2.setPaint(this.color);
                g2.fill(new Rectangle(x, y, width, height));
            } else if (vertexLabelVisible && this.toolTipLabel != null && this.toolTipPosition
                    != null) {
                //Getting the font information
                g2.setFont(this.getFont());
                java.awt.FontMetrics metrics = g2.getFontMetrics(g2.getFont());

                //Getting the label size
                int width = metrics.stringWidth(this.toolTipLabel);
                int height = metrics.getAscent();

                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.75f));
                g2.setPaint(Color.WHITE);
                g2.fill(new Rectangle(this.toolTipPosition.x - 2,
                        this.toolTipPosition.y - height, width + 4, height + 4));
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));

                g2.setColor(Color.DARK_GRAY);
                g2.drawRect(this.toolTipPosition.x - 2, this.toolTipPosition.y - height, width + 4, height + 4);

                //Drawing the label
                g2.drawString(this.toolTipLabel, this.toolTipPosition.x, this.toolTipPosition.y);
            }
            //drawn the selection polygon
            if (this.polygon
                    != null) {
                g2.setColor(this.color);
                g2.drawPolygon(this.polygon);

                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.45f));
                g2.setPaint(this.color);
                g2.fillPolygon(this.polygon);
            }
            if (imageBuffer
                    != null) {
                imageBuffer.flush();
            }

            g2.dispose();
        }

        public void setUpdatingTopics(boolean value) {
            updating_topics = value;
        }

        public TopicData getTopicData() {
            return projection.getTopicData();
        }

        @Override
        public void setBackground(Color bg) {
            super.setBackground(bg);
            if (this.csp != null) {
                this.csp.setBackground(bg);
            }
        }

        public ColorTable getColorTable() {
            return colorTable;
        }

        public void cleanMarkedVertices(boolean cleanVertex) {
            if (getGraph() != null) {
                this.markedVertexId = null;
                this.selectedVerticesId = null;

                for (TIntObjectIterator<Vertex> it = getGraph().getVertex().iterator(); it.hasNext();) {
                    it.advance();
                    Vertex vertex = it.value();
                    vertex.setSelected(false);
                    vertex.setInNeighborhood(false);

                    if (cleanVertex) {
                        vertex.setShowLabel(false);
                    }
                }
                mainview.setContentPanel(-1);
                mainview.setPropertiesPanel(-1);
                mainview.setNearestNeighborsPoints(null, false);
                mainview.setMarkedPointText(null);
            }
            this.repaint();
        }

        @Override
        public void setFont(java.awt.Font font) {
            TemporalProjectionViewer.this.setFont(font);
        }

        @Override
        public java.awt.Font getFont() {
            return TemporalProjectionViewer.this.getFont();
        }

        public void markNeighbors(Integer vertexId) {
            if (getGraph() != null) {
                //clean the marked vertices
                this.cleanMarkedVertices(false);
                this.markedVertexId = vertexId;
                //mark the new vertices
                Vertex vertex = getGraph().getVertexById(vertexId);
                if (vertex != null) {
                    ArrayList<Vertex> neighborsVertex = new ArrayList<>();
                    ArrayList<Edge> neighborsEdges = new ArrayList<>();
                    getGraph().getNeighbors(neighborsVertex, neighborsEdges, getCurrentConnectivity(), vertex, 1, low_value_edges, high_value_edges);

                    HashMap<Edge, Vertex> neighborsMap = new HashMap<>();
                    for (Vertex v : neighborsVertex) {
                        for (Edge edge : neighborsEdges) {
                            if (v.getId() == edge.getSource() || v.getId() == edge.getTarget()) {
                                neighborsMap.put(edge, v);
                            }
                        }
                    }
                    ConnectivityType con_tye = getCurrentConnectivity().getType();
                    if (con_tye.equals(ConnectivityType.BIBLIOGRAPHIC_COUPLING) || con_tye.equals(ConnectivityType.CO_AUTHORSHIP)) {
                        mainview.setNearestNeighborsPoints(neighborsMap, true);
                    } else {
                        mainview.setNearestNeighborsPoints(neighborsMap, false);
                    }
                    mainview.setMarkedPointText(vertex);

                    mainview.setContentPanel(vertexId);
                    mainview.setPropertiesPanel(vertexId);

                    //selecting vertex
                    vertex.setSelected(true);
                    if (neighborsVertex != null) {
                        for (Vertex v : neighborsVertex) {
                            v.setInNeighborhood(true);
                        }
                    }
                }
                this.repaint();
            }
        }

        public void markVertices(ArrayList<Vertex> vertices) {
            if (vertices != null) {
                this.cleanMarkedVertices(false);

                //change the vertices' colors
                selectedVerticesId = new ArrayList<>(vertices.size());
                for (Vertex v : vertices) {
                    v.setSelected(true);
                    selectedVerticesId.add(v.getId());
                }
                this.repaint();
            }
        }

        private void mapEdgesWeight(boolean selected) {
            Connectivity con = getCurrentConnectivity();
            ArrayList<Edge> edges = con.getEdges();
            if (selected) {
                if (con.isWeighted()) {
                    float min_weight = con.getMinWeight();
                    float max_weight = con.getMaxWeight();
                    for (Edge e : edges) {
                        e.setWidthFactor((e.getWeight() - min_weight) / (max_weight - min_weight));
                    }
                }
            } else {
                for (Edge e : edges) {
                    e.setWidthFactor(0);
                }
            }
            this.repaint();
        }

        private void colorAs(Scalar scalar, boolean logscale) {
            TemporalGraph graph = getGraph();
            if (graph != null) {
                for (TIntObjectIterator<Vertex> it = graph.getVertex().iterator(); it.hasNext();) {
                    it.advance();
                    it.value().setColor(scalar, this.colorTable, logscale);
                }
                this.repaint();
            }
        }

        public void resizeAs(Scalar scalar, boolean logscale) {
            TemporalGraph graph = getGraph();
            if (graph != null) {
                for (TIntObjectIterator<Vertex> it = graph.getVertex().iterator(); it.hasNext();) {
                    it.advance();
                    it.value().setRayFactor(scalar, logscale);
                }
                this.repaint();
            }
        }

        public TIntArrayList getSelectedVertex(java.awt.Polygon polygon) {
            TIntArrayList selected = new TIntArrayList();
            if (getGraph() != null) {
                for (TIntObjectIterator<Vertex> it = getGraph().getVertex().iterator(); it.hasNext();) {
                    it.advance();
                    Vertex v = it.value();
                    if (polygon.contains(v.getX(), v.getY())) {
                        selected.add(v.getId());
                    }
                }
            }
            return selected;
        }

        public TIntArrayList getSelectedVertex(Point localSource, Point localTarget) {
            TIntArrayList selVertex = new TIntArrayList();
            if (getGraph() != null) {
                int x = localSource.x;
                int width = localTarget.x - localSource.x;

                int y = localSource.y;
                int height = localTarget.y - localSource.y;

                if (localSource.x > localTarget.x) {
                    x = localTarget.x;
                    width = localSource.x - localTarget.x;
                }

                if (localSource.y > localTarget.y) {
                    y = localTarget.y;
                    height = localSource.y - localTarget.y;
                }

                Rectangle rect = new Rectangle(x, y, width, height);

                for (TIntObjectIterator<Vertex> it = getGraph().getVertex().iterator(); it.hasNext();) {
                    it.advance();
                    Vertex v = it.value();
                    if (v.isInside(rect)) {
                        selVertex.add(v.getId());
                    }
                }
            }
            return selVertex;
        }

        public Topic getTopicByPosition(java.awt.Point point) {
            GeometryFactory factory = new GeometryFactory();
            com.vividsolutions.jts.geom.Point p = factory.createPoint(new Coordinate(point.x, point.y));
            for (Topic t : getGraph().getTopics()) {
                if (t.contains(p) && !t.isAnimation()) {
                    return t;
                }
            }
            return null;
        }


        private void saveToPngImageFile(String filename) throws IOException {
            try {
                TemporalGraph graph = getGraph();
                BufferedImage image = new BufferedImage(graph.getSize().width + 50, graph.getSize().height + 50, BufferedImage.TYPE_INT_ARGB);
                this.paint(image.getGraphics());
                ImageIO.write(image, "png", new File(filename));
            } catch (IOException ex) {
                Logger.getLogger(TemporalProjectionViewer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        class MouseClickedListener extends MouseAdapter {

            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                super.mouseClicked(evt);
                if (getGraph() != null) {
                    if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                        if (!TemporalProjectionViewer.highlightTopic && !evt.isControlDown()) {
                            Vertex v = getGraph().getVertexByPosition(evt.getX(), evt.getY());
                            if (v != null) {
                                if (evt.getClickCount() == 1) {
                                    ViewPanel.this.markNeighbors(v.getId());
                                } else {
                                    int[] documents = new int[1];
                                    documents[0] = v.getId();
                                    MultipleDocumentViewer documentViewer = new MultipleDocumentViewer(documents, projection.getDatabaseCorpus());
                                    documentViewer.display();
                                }
                                mainview.expandDocumentsPath(v.getId());
                            }
                        } else if (evt.getClickCount() == 2 && evt.isControlDown()) {
                            Topic topicByPosition = ViewPanel.this.getTopicByPosition(evt.getPoint());
                            if (topicByPosition != null) {
                                ViewPanel.this.selectedTopic = topicByPosition;
                                ViewPanel.this.selectedTopic.setSelected(true);
                                mainview.expandTopicsPath(selectedTopic);
                            } else {
                                if (ViewPanel.this.selectedTopic != null) {
                                    ViewPanel.this.selectedTopic.setSelected(false);
                                }
                                ViewPanel.this.selectedTopic = null;
                            }
                        }
                    } else if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                        ViewPanel.this.cleanMarkedVertices(true);
                        if (ViewPanel.this.selectedTopic != null) {
                            ViewPanel.this.selectedTopic.setSelected(false);
                        }
                        ViewPanel.this.selectedTopic = null;
                        for (Topic t : getGraph().getTopics()) {
                            t.setUseGrayColor(false);
                        }
                    }
                }
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                super.mousePressed(evt);
                if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                    if (getGraph() != null) {
                        ViewPanel.this.source = evt.getPoint();
                        ViewPanel.this.color = VertexSelectionFactory.getInstance(TemporalProjectionViewer.this,
                                TemporalProjectionViewer.type).getColor();
                    }
                } else if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                    ViewPanel.this.polygon = new Polygon();
                    ViewPanel.this.polygon.addPoint(evt.getX(), evt.getY());
                    ViewPanel.this.color = VertexSelectionFactory.getInstance(TemporalProjectionViewer.this,
                            TemporalProjectionViewer.type).getColor();
                }
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                super.mouseReleased(evt);

                if ((getGraph() != null) && (ViewPanel.this.source != null && ViewPanel.this.target != null) || ViewPanel.this.polygon != null) {
                    TIntArrayList vertices;
                    if (ViewPanel.this.polygon != null) {
                        vertices = ViewPanel.this.getSelectedVertex(ViewPanel.this.polygon);
                    } else {
                        vertices = ViewPanel.this.getSelectedVertex(ViewPanel.this.source, ViewPanel.this.target);
                    }
                    if (!vertices.isEmpty()) {
                       ViewPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        ViewPanel.this.cleanMarkedVertices(false);
                        VertexSelectionFactory.getInstance(TemporalProjectionViewer.this,
                                TemporalProjectionViewer.type).vertexSelected(null, null, vertices);
                        ViewPanel.this.selectedVerticesId = new ArrayList<>();
                        for (int i = 0; i < vertices.size(); i++) {
                            ViewPanel.this.selectedVerticesId.add(vertices.get(i));
                        }
                        ViewPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    }
                }

                ViewPanel.this.polygon = null;
                ViewPanel.this.source = null;
                ViewPanel.this.target = null;
                ViewPanel.this.repaint();
            }
        }

        class MouseMotionListener extends MouseMotionAdapter {

            @Override
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                super.mouseMoved(evt);
                if (getGraph() != null) {
                    Vertex vertex;

                    if (TemporalProjectionViewer.highlightTopic || evt.isControlDown()) {
                        Topic topicByPosition = ViewPanel.this.getTopicByPosition(evt.getPoint());

                        if (ViewPanel.this.selectedTopic != null) {
                            ViewPanel.this.selectedTopic.setSelected(false);
                            ViewPanel.this.selectedTopic = null;
                            for (Topic t : getGraph().getTopics()) {
                                t.setUseGrayColor(false);
                            }
                        }

                        if (topicByPosition != null) {
                            ViewPanel.this.selectedTopic = topicByPosition;
                            ViewPanel.this.selectedTopic.setSelected(true);
                            mainview.expandTopicsPath(selectedTopic);
                            ViewPanel.this.selectedTopic.setUseGrayColor(false);
                            for (Topic t : getGraph().getTopics()) {
                                if (!t.isSelected()) {
                                    t.setUseGrayColor(true);
                                }
                            }

                        } else {
                            for (Topic t : getGraph().getTopics()) {
                                t.setUseGrayColor(false);
                            }
                        }
                    }

                    vertex = getGraph().getVertexByPosition(evt.getX(), evt.getY());
                    if (vertex != null) {
                        ViewPanel.this.toolTipLabel = vertex.toString();
                        ScienceViewMainFrame.getInstance().setContentPanel(vertex.getId());
                        if (ViewPanel.this.toolTipLabel.trim().length() > 0) {
                            titleTextField.setText(ViewPanel.this.toolTipLabel);
                            titleTextField.setCaretPosition(0);
                            if (ViewPanel.this.toolTipLabel.length() > 100) {
                                ViewPanel.this.toolTipLabel = ViewPanel.this.toolTipLabel.substring(0, 96) + "...";
                            }
                            ViewPanel.this.toolTipPosition = evt.getPoint();
                            ViewPanel.this.repaint();
                        }
                    } else {
                        //Clear the tool tip
                        titleTextField.setText(null);
                        ViewPanel.this.toolTipLabel = null;
                        ViewPanel.this.toolTipPosition = null;
                        ViewPanel.this.repaint();
                    }
                }
            }

            @Override
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                if (ViewPanel.this.source != null) {
                    ViewPanel.this.target = evt.getPoint();
                }

                if (ViewPanel.this.polygon != null) {
                    ViewPanel.this.polygon.addPoint(evt.getX(), evt.getY());
                }

                ViewPanel.this.repaint();
            }
        }
    }

    public class Tracker extends javax.swing.JPanel {

        private static final long serialVersionUID = 1L;
        public ColorTable colorTable;
        private BufferedImage imageBuffer;
        private TemporalGraph graph = null;
//        private java.awt.Polygon polygon;

        public Tracker() {
            this.setLayout(new FlowLayout(FlowLayout.LEFT));
            this.colorTable = new ColorTable();
            this.setBackground(Color.WHITE);
//            this.setDoubleBuffered(true);


            this.addMouseListener(new MouseClickedListener());
        }

        public void setGraph(TemporalGraph graph) {
            this.graph = graph;
        }

        @Override
        public void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DEFAULT);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            if (graph != null) {
                this.imageBuffer = new BufferedImage(graph.getSize().width + 1, graph.getSize().height + 1, BufferedImage.TYPE_INT_RGB);
                java.awt.Graphics2D g2Buffer = this.imageBuffer.createGraphics();
                g2Buffer.setColor(this.getBackground());
                g2Buffer.fillRect(0, 0, graph.getSize().width + 1, graph.getSize().height + 1);
                for (TIntObjectIterator<Vertex> it = graph.getVertex().iterator(); it.hasNext();) {
                    it.advance();
                    it.value().draw(imageBuffer, null, false, true);
                }

                g2Buffer.dispose();
            }

            if (this.imageBuffer != null) {
                g2.drawImage(this.imageBuffer, 0, 0, null);
            }

        }

        public void cleanMarkedVertices(boolean cleanVertex) {

//                this.markedVertexId = null;
//                this.selectedVerticesId = null;

            for (TIntObjectIterator<Vertex> it = graph.getVertex().iterator(); it.hasNext();) {
                it.advance();
                Vertex vertex = it.value();
                vertex.setSelected(false);
                vertex.setInNeighborhood(false);

                if (cleanVertex) {
                    vertex.setShowLabel(false);
                }
            }
            mainview.setContentPanel(-1);
            mainview.setPropertiesPanel(-1);
            mainview.setNearestNeighborsPoints(null, false);
            mainview.setMarkedPointText(null);

            this.repaint();
        }

        public void markNeighbors(Integer vertexId) {
            if (getGraph() != null) {
                //clean the marked vertices
                this.cleanMarkedVertices(false);
//                this.markedVertexId = vertexId;
                //mark the new vertices
                Vertex vertex = projection.getLastGraph().getVertexById(vertexId);

                mainview.setMarkedPointText(vertex);

                mainview.setContentPanel(vertexId);
                mainview.setPropertiesPanel(vertexId);

                //selecting vertex
                vertex.setSelected(true);

                this.repaint();
            }
        }

        class MouseClickedListener extends MouseAdapter {

            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                super.mouseClicked(evt);
                if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                    if (!TemporalProjectionViewer.highlightTopic && !evt.isControlDown()) {
                        Vertex v = graph.getVertexByPosition(evt.getX(), evt.getY());
                        if (v != null) {
                            if (evt.getClickCount() == 1) {
                                markNeighbors(v.getId());
                            } else {
                                int[] documents = new int[1];
                                documents[0] = v.getId();
                                MultipleDocumentViewer documentViewer = new MultipleDocumentViewer(documents, projection.getDatabaseCorpus());
                                documentViewer.display();
                            }
                            mainview.expandDocumentsPath(v.getId());
                        }
                    }
                } else if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {

                    cleanMarkedVertices(true);
                    cleanTopics();
                }
            }
//            @Override
//            public void mousePressed(java.awt.event.MouseEvent evt) {
//                super.mousePressed(evt);
//            }
//
//            @Override
//            public void mouseReleased(java.awt.event.MouseEvent evt) {
//                super.mouseReleased(evt);
//            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ColorPanel;
    private javax.swing.JPanel EdgesPanel;
    private javax.swing.JPanel ModifierPanel;
    private javax.swing.JPanel SizePanel;
    private javax.swing.JPanel TitlesPanel;
    private javax.swing.JPanel animation_controlsPanel;
    private javax.swing.JComboBox colorComboBox;
    private javax.swing.JLabel colorLabel;
    private javax.swing.JCheckBox colorLogarithmicCheckBox;
    private javax.swing.JComboBox comparsionComboBox;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JComboBox edgesComboBox;
    private javax.swing.JLabel edgesLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton next_graphButton;
    private javax.swing.JButton playButton;
    private javax.swing.JButton previous_graphButton;
    private javax.swing.JPanel projectionPanel;
    private javax.swing.JScrollPane projectionScrollPane;
    private javax.swing.JPanel reportPanel;
    private javax.swing.JScrollPane reportScrollPane;
    private javax.swing.JComboBox sizeComboBox;
    private javax.swing.JLabel sizeLabel;
    private javax.swing.JCheckBox sizeLogarithmicCheckBox;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JTextField titleTextField;
    private javax.swing.JComboBox titlesComboBox;
    private javax.swing.JSpinner titlesSpinner;
    // End of variables declaration//GEN-END:variables
}
