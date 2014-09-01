/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TopicEvolutionVisMainFrame.java
 *
 * Created on 28/04/2009, 15:19:29
 */
package topicevolutionvis.view;

import java.awt.Cursor;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import topicevolutionvis.database.ConnectionManager;
import topicevolutionvis.datamining.clustering.monic.MONICSettings;
import topicevolutionvis.graph.Edge;
import topicevolutionvis.graph.Scalar;
import topicevolutionvis.graph.TemporalGraph;
import topicevolutionvis.graph.Vertex;
import topicevolutionvis.projection.stress.StressJDialog;
import topicevolutionvis.projection.temporal.TemporalProjection;
import topicevolutionvis.projection.temporal.listeners.VertexSelectionFactory;
import topicevolutionvis.topic.Topic;
import topicevolutionvis.util.SystemPropertiesManager;
import topicevolutionvis.utils.filefilter.PNGFileFilter;
import topicevolutionvis.view.tools.MemoryCheck;
import topicevolutionvis.view.tools.OpenProjectionDialog;
import topicevolutionvis.view.tools.WordsManager;
import topicevolutionvis.wizard.ProjectionWizardView;

/**
 *
 * @author Aretha
 */
public class ScienceViewMainFrame extends javax.swing.JFrame implements TreeSelectionListener, MouseListener {

    private static final long serialVersionUID = 1L;
    //   private JScrollPane documentsScrollPane = new JScrollPane();
    private DefaultListModel nearestNeighborListModel = new DefaultListModel();
    private HashMap<JComponent, JRadioButtonMenuItem> windows = new HashMap<>();
    private TemporalProjectionViewer currentViewer = null;
    private DefaultTreeModel documentsModel = new DefaultTreeModel(null);
    public JTree documentsTree = new JTree(documentsModel);
    private DefaultTreeModel topicsModel = new DefaultTreeModel(null);
    public JTree topicsTree = new JTree(topicsModel);
    //private JScrollPane groupsScrollPane = new JScrollPane();
    private static ScienceViewMainFrame _instance;
    private int documents_or_groups = 0;
    private DefaultTableModel authorsTableModel;

    /**
     * Creates new form TopicEvolutionVisMainFrame
     */
    public ScienceViewMainFrame() {
        String[] titulos = new String[]{"Author", "# Docs"};
        this.authorsTableModel = new DefaultTableModel(null, titulos);
        initComponents();
        this.createBufferStrategy(2);
        addDocumentsTreeToFrame();
        this.addTopicsTreeToFrame();
    }

    public static ScienceViewMainFrame getInstance() {
        if (_instance == null) {
            _instance = new ScienceViewMainFrame();
        }

        return _instance;
    }

    public void display() {
        Rectangle area = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        this.setSize((int) area.getWidth(), (int) area.getHeight());
        this.setVisible(true);
        this.toFront();
    }

    public void updateTopicInfoPanel(Topic t) {
        if (t == null) {
            this.topicIdTextField.setText(null);
            this.averageYearTextField.setText(null);
            this.numberOfDocumentsTextField.setText(null);
            for (int i = this.authorsTableModel.getRowCount() - 1; i >= 0; i--) {
                this.authorsTableModel.removeRow(i);
            }
        } else {
            String[] titulos = new String[]{"Author", "# Docs"};
            Object[][] data = this.currentViewer.getTemporalProjection().getDatabaseCorpus().getMainAuthors(t.getVertexIdList());
            this.authorsTableModel.setDataVector(data, titulos);
            this.authorsTable.setModel(authorsTableModel);
            this.topicIdTextField.setText(Integer.toString(t.getId()));
            this.numberOfDocumentsTextField.setText(Integer.toString(t.size()));
            this.averageYearTextField.setText(Integer.toString(t.getAverageYearOfDocuments()));
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (this.documentsTabbedPane.getSelectedIndex() == 0) {
            TreePath[] treePaths = this.documentsTree.getSelectionPaths();
            if (treePaths != null) {
                DefaultMutableTreeNode node;
                Vertex v;
                JInternalFrame frame = this.desktop.getSelectedFrame();
                if (frame != null && frame instanceof TemporalProjectionViewer) {
                    TemporalProjectionViewer vv = (TemporalProjectionViewer) frame;
                    TemporalGraph graph = vv.getGraph();
                    for (TreePath treePath : treePaths) {
                        node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                        if (node.getUserObject() instanceof DocumentInfo) {
                            v = graph.getVertexById(((DocumentInfo) node.getUserObject()).id);
                            if (v != null) {
                                vv.markVertex(v);
                            }
                        } else {
                            Enumeration enumeration = node.children();
                            ArrayList<Vertex> vertex = new ArrayList<>();
                            while (enumeration.hasMoreElements()) {
                                vertex.add(graph.getVertexById(((DocumentInfo) ((DefaultMutableTreeNode) enumeration.nextElement()).getUserObject()).id));
                            }
                            vv.selectVertices(vertex);
                        }
                    }
                }
            }
        } else if (this.documentsTabbedPane.getSelectedIndex() == 1) {
            TreePath[] treePaths = this.topicsTree.getSelectionPaths();
            if (treePaths != null) {
                DefaultMutableTreeNode node;
                Vertex v;
                JInternalFrame frame = this.desktop.getSelectedFrame();
                if (frame != null && frame instanceof TemporalProjectionViewer) {
                    TemporalProjectionViewer vv = (TemporalProjectionViewer) frame;
                    TemporalGraph graph = vv.getGraph();
                    for (TreePath treePath : treePaths) {
                        node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                        if (node.getUserObject() instanceof DocumentInfo) {
                            v = graph.getVertexById(((DocumentInfo) node.getUserObject()).id);
                            if (v != null) {
                                vv.markVertex(v);
                            }
                            this.updateTopicInfoPanel(null);
                        } else if (node.getUserObject() instanceof Topic) {
                            this.updateTopicInfoPanel(((Topic) node.getUserObject()));
                            vv.setSelectedTopic(((Topic) node.getUserObject()));
                            vv.updateImage();
                        }
                    }
                }

            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        JInternalFrame frame = this.desktop.getSelectedFrame();
        if (frame != null && frame instanceof TemporalProjectionViewer) {
            TemporalProjectionViewer gv = (TemporalProjectionViewer) this.desktop.getSelectedFrame();
            if (this.documentsTabbedPane.getSelectedIndex() == 0) { //by year
                TreePath path = documentsTree.getSelectionPath();
                if (path != null) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                    if (node.isLeaf() && e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                        DocumentInfo documentInfo = (DocumentInfo) node.getUserObject();
                        int[] documents = new int[1];
                        documents[0] = documentInfo.id;
                        (new MultipleDocumentViewer(documents, gv.getCorpus())).display();
                    }
                }
            } else if (this.documentsTabbedPane.getSelectedIndex() == 1) { //by cluster
                TreePath path = topicsTree.getSelectionPath();
                if (path != null) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                    if (node.isLeaf() && e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                        DocumentInfo documentInfo = (DocumentInfo) node.getUserObject();
                        int[] documents = new int[1];
                        documents[0] = documentInfo.id;
                        (new MultipleDocumentViewer(documents, gv.getCorpus())).display();
                    }
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public void setNearestNeighborsPoints(HashMap<Edge, Vertex> neighborsMap, boolean show_weight) {
        nearestNeighborListModel.clear();
        if (neighborsMap != null) {

            ArrayList<Neighbor> neighbors = new ArrayList<>();
            for (Entry<Edge, Vertex> entry : neighborsMap.entrySet()) {
                neighbors.add(new Neighbor(entry.getKey(), entry.getValue(), show_weight));
            }

            Collections.sort(neighbors, new Comparator<Neighbor>() {
                @Override
                public int compare(Neighbor o1, Neighbor o2) {
                    int weightComp = Float.compare(o2.getEdge().getWeight(), o1.getEdge().getWeight());
                    if (weightComp != 0) {
                        return weightComp;
                    } else {
                        return currentViewer.getTemporalProjection().getTitleDocument(o1.getVertex().getId()).
                                compareTo(currentViewer.getTemporalProjection().getTitleDocument(o2.getVertex().getId()));
                    }
                }
            });

            for (Neighbor neighbor : neighbors) {
                nearestNeighborListModel.addElement(neighbor);
            }
        }
    }

//    public void setMarkedPointText(Vertex vertex) {
//        if (vertex != null) {
//            markedPointField.setText(vertex.toString());
//            markedPointField.setCaretPosition(0);
//        } else {
//            markedPointField.setText("");
//        }
//    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        menus_buttonGroup = new javax.swing.ButtonGroup();
        jToolBar1 = new javax.swing.JToolBar();
        openButton = new javax.swing.JButton();
        spaceLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        spaceLabel1 = new javax.swing.JLabel();
        zoomSpinner = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        spaceLabel2 = new javax.swing.JLabel();
        removezoomButton = new javax.swing.JButton();
        searchPanel = new javax.swing.JPanel();
        searchToolbarLabel = new javax.swing.JLabel();
        searchToolbarTextField = new javax.swing.JTextField();
        goToolbarButton = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        desktop = new javax.swing.JDesktopPane();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        documentsPanel = new javax.swing.JPanel();
        documentsTabbedPane = new javax.swing.JTabbedPane();
        documentsScrollPane = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        topicsScrollPane = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        averageYearTextField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        numberOfDocumentsTextField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        topicIdTextField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        authorsTable = new javax.swing.JTable();
        contentPanel = new javax.swing.JPanel();
        titleTextField = new javax.swing.JTextField();
        contentScrollPane = new javax.swing.JScrollPane();
        contentTextArea = new javax.swing.JTextArea();
        neighborsPanel = new javax.swing.JPanel();
        markedPointPanel = new javax.swing.JPanel();
        nearestNeighborScrollPanel = new javax.swing.JScrollPane();
        nearestNeighborsList = new javax.swing.JList(nearestNeighborListModel);
        markedPointFields = new javax.swing.JPanel();
        markedPointField = new javax.swing.JTextField();
        propertiesPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        gccTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        lccTextField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        idTextField = new javax.swing.JTextField();
        graphToolBar = new javax.swing.JToolBar();
        jToggleButton1 = new javax.swing.JToggleButton();
        viewContentToggleButton = new javax.swing.JToggleButton();
        showVertexLabelToggleButton = new javax.swing.JToggleButton();
        selectVertexToggleButton = new javax.swing.JToggleButton();
        separatorLabel3 = new javax.swing.JLabel();
        showAllLabelsToggleButton = new javax.swing.JToggleButton();
        highlightLabelToggleButton = new javax.swing.JToggleButton();
        cleanLabelsButton = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        newProjectionMenuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        exportMenu = new javax.swing.JMenu();
        saveMenuItem = new javax.swing.JMenuItem();
        exportPNGMenuItem = new javax.swing.JMenuItem();
        importMenu = new javax.swing.JMenu();
        openProjectionMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        exitFileMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        cleanMenuItem = new javax.swing.JMenuItem();
        ToolMenu = new javax.swing.JMenu();
        managestopwordsMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        optionsMenuItem = new javax.swing.JMenuItem();
        memorycheckMenuItem = new javax.swing.JMenuItem();
        dataminingMenu = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        stressMenuItem = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        menuWindows = new javax.swing.JMenu();
        alignVerticallyMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Science View");
        setMinimumSize(new java.awt.Dimension(200, 483));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jToolBar1.setRollover(true);
        jToolBar1.setOpaque(false);

        openButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/New16.gif"))); // NOI18N
        openButton.setToolTipText("Create a new projection...");
        openButton.setFocusable(false);
        openButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wizardmenu(evt);
            }
        });
        jToolBar1.add(openButton);

        spaceLabel.setPreferredSize(new java.awt.Dimension(25, 0));
        jToolBar1.add(spaceLabel);

        jLabel1.setText("Zoom:");
        jToolBar1.add(jLabel1);

        spaceLabel1.setPreferredSize(new java.awt.Dimension(5, 0));
        jToolBar1.add(spaceLabel1);

        zoomSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(100), Integer.valueOf(10), null, Integer.valueOf(5)));
        zoomSpinner.setMaximumSize(new java.awt.Dimension(60, 20));
        zoomSpinner.setMinimumSize(new java.awt.Dimension(60, 20));
        zoomSpinner.setPreferredSize(new java.awt.Dimension(60, 20));
        zoomSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                zoomSpinnerStateChanged(evt);
            }
        });
        jToolBar1.add(zoomSpinner);

        jLabel4.setText("%");
        jToolBar1.add(jLabel4);

        spaceLabel2.setPreferredSize(new java.awt.Dimension(25, 0));
        jToolBar1.add(spaceLabel2);

        removezoomButton.setText("1:1");
        removezoomButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        removezoomButton.setFocusable(false);
        removezoomButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        removezoomButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        removezoomButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removezoomButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(removezoomButton);

        searchPanel.setPreferredSize(new java.awt.Dimension(300, 27));
        searchPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 0));

        searchToolbarLabel.setText("Search");
        searchPanel.add(searchToolbarLabel);

        searchToolbarTextField.setColumns(15);
        searchToolbarTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                searchToolbarTextFieldKeyPressed(evt);
            }
        });
        searchPanel.add(searchToolbarTextField);

        goToolbarButton.setText("...");
        goToolbarButton.setFocusable(false);
        goToolbarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        goToolbarButton.setMaximumSize(new java.awt.Dimension(29, 27));
        goToolbarButton.setMinimumSize(new java.awt.Dimension(29, 27));
        goToolbarButton.setPreferredSize(new java.awt.Dimension(29, 27));
        goToolbarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        goToolbarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goToolbarButtonActionPerformed(evt);
            }
        });
        searchPanel.add(goToolbarButton);

        jToolBar1.add(searchPanel);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        jSplitPane1.setDividerLocation(320);
        jSplitPane1.setOneTouchExpandable(true);

        desktop.setBackground(new java.awt.Color(240, 240, 240));
        desktop.setToolTipText("Create Labels");
        desktop.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                desktopComponentAdded(evt);
            }
            public void componentRemoved(java.awt.event.ContainerEvent evt) {
                desktopComponentRemoved(evt);
            }
        });
        jSplitPane1.setRightComponent(desktop);

        jPanel2.setLayout(new java.awt.BorderLayout());

        documentsPanel.setLayout(new java.awt.BorderLayout());

        documentsTabbedPane.setMinimumSize(new java.awt.Dimension(349, 400));
        documentsTabbedPane.setPreferredSize(new java.awt.Dimension(469, 400));
        documentsTabbedPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                documentsTabbedPaneMouseClicked(evt);
            }
        });
        documentsTabbedPane.addTab("By Year", documentsScrollPane);

        jPanel3.setLayout(new java.awt.BorderLayout());
        jPanel3.add(topicsScrollPane, java.awt.BorderLayout.CENTER);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Info"));
        jPanel4.setMaximumSize(new java.awt.Dimension(500, 500));
        jPanel4.setMinimumSize(new java.awt.Dimension(250, 250));
        jPanel4.setPreferredSize(new java.awt.Dimension(250, 270));
        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel5.setText("Number of documents:");
        jLabel5.setMinimumSize(new java.awt.Dimension(110, 14));
        jLabel5.setPreferredSize(new java.awt.Dimension(110, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(jLabel5, gridBagConstraints);

        averageYearTextField.setEditable(false);
        averageYearTextField.setColumns(8);
        averageYearTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        averageYearTextField.setMinimumSize(new java.awt.Dimension(55, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(averageYearTextField, gridBagConstraints);

        jLabel6.setText("Average Year:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(jLabel6, gridBagConstraints);

        numberOfDocumentsTextField.setEditable(false);
        numberOfDocumentsTextField.setColumns(8);
        numberOfDocumentsTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        numberOfDocumentsTextField.setMinimumSize(new java.awt.Dimension(55, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(numberOfDocumentsTextField, gridBagConstraints);

        jLabel7.setText("Id:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(jLabel7, gridBagConstraints);

        topicIdTextField.setEditable(false);
        topicIdTextField.setColumns(8);
        topicIdTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        topicIdTextField.setMinimumSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(topicIdTextField, gridBagConstraints);

        jPanel4.add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(150, 150));

        authorsTable.setModel(this.authorsTableModel);
        authorsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        this.authorsTable.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent event) {
                    int viewRow = authorsTable.getSelectedRow();
                    if (viewRow >= 0) {
                        String author_name = (String) authorsTableModel.getValueAt(authorsTable.convertRowIndexToModel(viewRow), 0);
                        TemporalProjectionViewer gv = (TemporalProjectionViewer) desktop.getSelectedFrame();
                        int[] ids = gv.getCorpus().getDocumentsFromAuthor(author_name, gv.getCurrentYear());
                        TemporalGraph graph = gv.getGraph();
                        ArrayList<Vertex> vertices = new ArrayList<>(ids.length);
                        for (int i = 0; i < ids.length; i++) {
                            vertices.add(graph.getVertexById(ids[i]));
                        }
                        gv.selectVertices(vertices);
                    }
                }
            });
            jScrollPane1.setViewportView(authorsTable);

            jPanel4.add(jScrollPane1, java.awt.BorderLayout.CENTER);

            jPanel3.add(jPanel4, java.awt.BorderLayout.PAGE_END);

            documentsTabbedPane.addTab("By Topic", jPanel3);

            documentsPanel.add(documentsTabbedPane, java.awt.BorderLayout.CENTER);

            jTabbedPane2.addTab("Documents", documentsPanel);

            contentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Document Content"));
            contentPanel.setLayout(new java.awt.BorderLayout());

            titleTextField.setColumns(30);
            titleTextField.setEditable(false);
            contentPanel.add(titleTextField, java.awt.BorderLayout.PAGE_START);

            contentTextArea.setColumns(20);
            contentTextArea.setLineWrap(true);
            contentTextArea.setRows(5);
            contentScrollPane.setViewportView(contentTextArea);

            contentPanel.add(contentScrollPane, java.awt.BorderLayout.CENTER);

            jTabbedPane2.addTab("Content", contentPanel);

            neighborsPanel.setLayout(new java.awt.BorderLayout());

            markedPointPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected Instance"));
            markedPointPanel.setPreferredSize(new java.awt.Dimension(100, 128));
            markedPointPanel.setLayout(new java.awt.BorderLayout());

            nearestNeighborScrollPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Nearest Neighbors"));
            nearestNeighborScrollPanel.setAutoscrolls(true);
            nearestNeighborScrollPanel.setPreferredSize(new java.awt.Dimension(100, 50));

            nearestNeighborsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            nearestNeighborsList.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    nearestNeighborsListMouseClicked(evt);
                }
            });
            nearestNeighborScrollPanel.setViewportView(nearestNeighborsList);

            markedPointPanel.add(nearestNeighborScrollPanel, java.awt.BorderLayout.CENTER);

            markedPointFields.setLayout(new java.awt.BorderLayout());

            markedPointField.setEditable(false);
            markedPointField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
            markedPointFields.add(markedPointField, java.awt.BorderLayout.NORTH);

            markedPointPanel.add(markedPointFields, java.awt.BorderLayout.NORTH);

            neighborsPanel.add(markedPointPanel, java.awt.BorderLayout.CENTER);

            jTabbedPane2.addTab("Neighbors", neighborsPanel);

            propertiesPanel.setLayout(new java.awt.GridBagLayout());

            jLabel2.setText("Global Citation Count:");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
            gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
            propertiesPanel.add(jLabel2, gridBagConstraints);

            gccTextField.setEditable(false);
            gccTextField.setColumns(8);
            gccTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            gccTextField.setToolTipText("Number of citations this document has received (including documents outside the collection).");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
            propertiesPanel.add(gccTextField, gridBagConstraints);

            jLabel3.setText("Local Citation Count:");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
            gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
            propertiesPanel.add(jLabel3, gridBagConstraints);

            lccTextField.setEditable(false);
            lccTextField.setColumns(8);
            lccTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            lccTextField.setToolTipText("Numer of times this document was cited by other documents inside the collection.");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
            propertiesPanel.add(lccTextField, gridBagConstraints);

            jLabel8.setText("Id:");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
            gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
            propertiesPanel.add(jLabel8, gridBagConstraints);

            idTextField.setEditable(false);
            idTextField.setColumns(8);
            idTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
            propertiesPanel.add(idTextField, gridBagConstraints);

            jTabbedPane2.addTab("Properties", propertiesPanel);

            jPanel2.add(jTabbedPane2, java.awt.BorderLayout.CENTER);

            jSplitPane1.setLeftComponent(jPanel2);

            getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

            graphToolBar.setOrientation(javax.swing.SwingConstants.VERTICAL);
            graphToolBar.setRollover(true);

            buttonGroup1.add(jToggleButton1);
            jToggleButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Information16.gif"))); // NOI18N
            jToggleButton1.setSelected(true);
            jToggleButton1.setToolTipText("Create label");
            jToggleButton1.setFocusable(false);
            jToggleButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            jToggleButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    createLabelToggleButtonActionPerformed(evt);
                }
            });
            graphToolBar.add(jToggleButton1);

            buttonGroup1.add(viewContentToggleButton);
            viewContentToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Copy16.gif"))); // NOI18N
            viewContentToggleButton.setToolTipText("View Content");
            viewContentToggleButton.setFocusable(false);
            viewContentToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            viewContentToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            viewContentToggleButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    viewContentToggleButtonActionPerformed(evt);
                }
            });
            graphToolBar.add(viewContentToggleButton);

            buttonGroup1.add(showVertexLabelToggleButton);
            showVertexLabelToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/text/Italic16.gif"))); // NOI18N
            showVertexLabelToggleButton.setFocusable(false);
            showVertexLabelToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            showVertexLabelToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            showVertexLabelToggleButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    showVertexLabelToggleButtonActionPerformed(evt);
                }
            });
            graphToolBar.add(showVertexLabelToggleButton);

            buttonGroup1.add(selectVertexToggleButton);
            selectVertexToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/AlignCenter16.gif"))); // NOI18N
            selectVertexToggleButton.setFocusable(false);
            selectVertexToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            selectVertexToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            selectVertexToggleButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    selectVertexToggleButtonActionPerformed(evt);
                }
            });
            graphToolBar.add(selectVertexToggleButton);

            separatorLabel3.setText("    ");
            graphToolBar.add(separatorLabel3);

            showAllLabelsToggleButton.setSelected(true);
            showAllLabelsToggleButton.setText("SL");
            showAllLabelsToggleButton.setToolTipText("Show all labels");
            showAllLabelsToggleButton.setFocusable(false);
            showAllLabelsToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            showAllLabelsToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            showAllLabelsToggleButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    showAllLabelsToggleButtonActionPerformed(evt);
                }
            });
            graphToolBar.add(showAllLabelsToggleButton);

            highlightLabelToggleButton.setText("HL");
            highlightLabelToggleButton.setToolTipText("Highlight Labels");
            highlightLabelToggleButton.setFocusable(false);
            highlightLabelToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            highlightLabelToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            highlightLabelToggleButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    highlightLabelToggleButtonActionPerformed(evt);
                }
            });
            graphToolBar.add(highlightLabelToggleButton);

            cleanLabelsButton.setText("CL");
            cleanLabelsButton.setToolTipText("Clean Projection");
            cleanLabelsButton.setFocusable(false);
            cleanLabelsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            cleanLabelsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            cleanLabelsButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    cleanLabelsButtonActionPerformed(evt);
                }
            });
            graphToolBar.add(cleanLabelsButton);

            getContentPane().add(graphToolBar, java.awt.BorderLayout.EAST);

            jMenu1.setText("File");

            newProjectionMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
            newProjectionMenuItem.setText("New Projection...");
            newProjectionMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    wizardmenu(evt);
                }
            });
            jMenu1.add(newProjectionMenuItem);
            jMenu1.add(jSeparator4);

            exportMenu.setText("Export");
            exportMenu.setEnabled(false);

            saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
            saveMenuItem.setText("Save Projection...");
            saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    saveMenuItemActionPerformed(evt);
                }
            });
            exportMenu.add(saveMenuItem);

            exportPNGMenuItem.setText("Export Projection Image to PNG File");
            exportPNGMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    exportPNGMenuItemActionPerformed(evt);
                }
            });
            exportMenu.add(exportPNGMenuItem);

            jMenu1.add(exportMenu);

            importMenu.setText("Import");

            openProjectionMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
            openProjectionMenuItem.setText("Open Projection...");
            openProjectionMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    openProjectionMenuItemActionPerformed(evt);
                }
            });
            importMenu.add(openProjectionMenuItem);

            jMenu1.add(importMenu);
            jMenu1.add(jSeparator1);

            exitFileMenuItem.setText("Exit");
            exitFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    exitFileMenuItemActionPerformed(evt);
                }
            });
            jMenu1.add(exitFileMenuItem);

            jMenuBar1.add(jMenu1);

            editMenu.setText("Edit");

            cleanMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
            cleanMenuItem.setText("Clean Projection");
            cleanMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    cleanMenuItemActionPerformed(evt);
                }
            });
            editMenu.add(cleanMenuItem);

            jMenuBar1.add(editMenu);

            ToolMenu.setText("Tools");

            managestopwordsMenuItem.setText("Manage Stopwords");
            managestopwordsMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    managestopwordsMenuItemActionPerformed(evt);
                }
            });
            ToolMenu.add(managestopwordsMenuItem);
            ToolMenu.add(jSeparator2);

            optionsMenuItem.setText("Options");
            optionsMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    optionsMenuItemActionPerformed(evt);
                }
            });
            ToolMenu.add(optionsMenuItem);

            memorycheckMenuItem.setText("Memory Check");
            memorycheckMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    memorycheckMenuItemActionPerformed(evt);
                }
            });
            ToolMenu.add(memorycheckMenuItem);

            jMenuBar1.add(ToolMenu);

            dataminingMenu.setText("Data Mining");

            jMenu2.setText("Data Analysis");
            jMenu2.setToolTipText("");

            stressMenuItem.setText("Projection Stress");
            stressMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    stressMenuItemActionPerformed(evt);
                }
            });
            jMenu2.add(stressMenuItem);

            dataminingMenu.add(jMenu2);

            jMenuItem1.setText("Topic Extraction and Tracking");
            jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jMenuItem1ActionPerformed(evt);
                }
            });
            dataminingMenu.add(jMenuItem1);

            jMenuBar1.add(dataminingMenu);

            menuWindows.setText("Windows");

            alignVerticallyMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
            alignVerticallyMenuItem.setText("Align Windows Vertically");
            alignVerticallyMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    alignVerticallyMenuItemActionPerformed(evt);
                }
            });
            menuWindows.add(alignVerticallyMenuItem);
            menuWindows.add(jSeparator3);

            jMenuBar1.add(menuWindows);

            helpMenu.setText("Help");

            aboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
            aboutMenuItem.setText("About");
            aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    aboutMenuItemActionPerformed(evt);
                }
            });
            helpMenu.add(aboutMenuItem);

            jMenuBar1.add(helpMenu);

            setJMenuBar(jMenuBar1);

            pack();
        }// </editor-fold>//GEN-END:initComponents

    public void clearTopicsTree() {
        this.topicsModel.setRoot(null);
    }

    public void updateTopicsTree() {
        TemporalProjectionViewer temporalViewer = (TemporalProjectionViewer) this.desktop.getSelectedFrame();
        if (temporalViewer != null) {
            DefaultMutableTreeNode topicsNodes = temporalViewer.createTopicsTree();
            if (topicsNodes.getChildCount() != 0) {
                DefaultMutableTreeNode parent = new DefaultMutableTreeNode("Topics");
                this.topicsModel.insertNodeInto(topicsNodes, parent, parent.getChildCount());
                this.topicsModel.setRoot(parent);
                this.topicsTree.expandRow(2);
                this.topicsTree.expandPath(new TreePath(topicsNodes.getPath()));
            }
        }

    }

    public void updateDocumentsTree(ArrayList<DefaultMutableTreeNode> treeMap, int index) {
        this.documentsModel.setRoot(null);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        int count = 0;
        if (index != treeMap.size()) {
            for (int i = 0; i <= index; i++) {
                root.add(treeMap.get(i));
                count += treeMap.get(i).getChildCount();
            }
        } else {
            for (int i = 0; i <= index - 1; i++) {
                root.add(treeMap.get(i));
                count += treeMap.get(i).getChildCount();
            }
        }
        root.setUserObject("Documents [" + count + " documents]");
        documentsModel.setRoot(root);
    }

    public void addDocumentsTreeToFrame() {
        documentsTree.addTreeSelectionListener(this);
        documentsTree.addMouseListener(this);
        documentsScrollPane.setViewportView(documentsTree);
        documentsTree.setDoubleBuffered(true);
    }

    public void addTopicsTreeToFrame() {
        this.topicsTree.addTreeSelectionListener(this);
        this.topicsTree.addMouseListener(this);
        this.topicsScrollPane.setViewportView(this.topicsTree);
        this.documentsTabbedPane.setSelectedIndex(this.documents_or_groups);
    }

    public void setPropertiesPanel(int id_doc) {
        if (id_doc != -1) {
            TemporalProjectionViewer gv = (TemporalProjectionViewer) this.desktop.getSelectedFrame();
            this.idTextField.setText(Integer.toString(id_doc));
            this.gccTextField.setText(Integer.toString(gv.getCorpus().getGlobalCitationCount(id_doc)));
            this.lccTextField.setText(Integer.toString(gv.getCorpus().getLocalCitationCount(id_doc)));
        } else {
            this.idTextField.setText(null);
            this.gccTextField.setText(null);
            this.lccTextField.setText(null);
        }
    }

    public void setContentPanel(int id_doc) {
        if (id_doc != - 1) {
            TemporalProjectionViewer gv = (TemporalProjectionViewer) this.desktop.getSelectedFrame();
            this.titleTextField.setText(gv.getCorpus().getTitle(id_doc));
            this.titleTextField.setCaretPosition(0);
            this.contentTextArea.setText(gv.getCorpus().getViewContent(id_doc));
            this.contentTextArea.setCaretPosition(0);
        } else {
            this.titleTextField.setText(null);
            this.contentTextArea.setText(null);
        }

    }

    public void setMarkedPointText(Vertex v) {
        if (v != null) {
            this.markedPointField.setText(v.toString());
            this.markedPointField.setCaretPosition(0);
        } else {
            this.markedPointField.setText(null);
        }
    }

    public void expandTopicsPath(Topic topic) {
        Object rootObject = this.topicsModel.getRoot();
        if ((rootObject != null) && (rootObject instanceof DefaultMutableTreeNode)) {
            Enumeration en = ((DefaultMutableTreeNode) rootObject).getChildAt(0).children();
            while (en.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
                Topic t = (Topic) node.getUserObject();
                if (t.equals(topic)) {
                    this.documentsTabbedPane.setSelectedIndex(1);
                    TreePath path = new TreePath(node.getPath());
                    this.topicsTree.scrollPathToVisible(path);
                    this.topicsTree.setSelectionPath(path);
                    break;
                }
            }
        } else {
            this.topicsTree.clearSelection();
        }
    }

    public void expandDocumentsPath(int id) {
        Object rootObject = this.documentsModel.getRoot();
        if ((rootObject != null) && (rootObject instanceof DefaultMutableTreeNode)) {
            DefaultMutableTreeNode node;
            Enumeration depth = ((DefaultMutableTreeNode) rootObject).depthFirstEnumeration();
            while (depth.hasMoreElements()) {
                node = (DefaultMutableTreeNode) depth.nextElement();
                if ((node.getUserObject() instanceof DocumentInfo) && ((DocumentInfo) node.getUserObject()).id == id) {
                    this.documentsTabbedPane.setSelectedIndex(0);
                    TreePath path = new TreePath(node.getPath());
                    this.documentsTree.scrollPathToVisible(path);
                    this.documentsTree.setSelectionPath(path);
                    break;
                }
            }
        }
    }

    public void recreatingLists() {
        this.nearestNeighborListModel.clear();
//        this.markedPointField.setText(null);
    }

    public void setFocusedJInternalFrame(JInternalFrame frame) {
        if (frame != null) {
            for (JComponent c : this.windows.keySet()) {
                if (c instanceof TemporalProjectionViewer) {
                    TemporalProjectionViewer gv = (TemporalProjectionViewer) c;
                    this.currentViewer = gv;
                    this.windows.get(gv).setText(gv.getTitle());
//                    this.saveButton.setEnabled(true);
                    this.saveMenuItem.setEnabled(true);
                    this.exportMenu.setEnabled(true);
                    this.cleanMenuItem.setEnabled(true);
                    this.optionsMenuItem.setEnabled(true);
                } else if (c instanceof JInternalFrame.JDesktopIcon) {
                    Viewer gv = (Viewer) ((JInternalFrame.JDesktopIcon) c).getInternalFrame();
                    this.windows.get(gv).setText(gv.getTitle());
                }
            }
            for (int i = 0; i < this.menuWindows.getItemCount(); i++) {
                JMenuItem item = this.menuWindows.getItem(i);
                if (item != null && item.getText().equals(frame.getTitle())) {
                    item.setSelected(true);
                    if (frame instanceof Viewer) {
                        this.recreatingLists();
                        ((TemporalProjectionViewer) frame).updateDocumentasAndTopicsTrees();
                    }
                    break;
                }
            }
        } else {
            this.nearestNeighborListModel.clear();
            this.documentsTree.setModel(null);
            this.saveMenuItem.setEnabled(false);
            this.exportMenu.setEnabled(false);
            this.cleanMenuItem.setEnabled(false);
            this.optionsMenuItem.setEnabled(false);
            this.currentViewer = null;
        }
    }

    private void managestopwordsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_managestopwordsMenuItemActionPerformed
        WordsManager.getInstance(this, true).display();
    }//GEN-LAST:event_managestopwordsMenuItemActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        AboutDialog aboutDialog = new AboutDialog();
        aboutDialog.setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed
    private void exitFileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitFileMenuItemActionPerformed
        dispose();
    }//GEN-LAST:event_exitFileMenuItemActionPerformed

    private void optionsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionsMenuItemActionPerformed
        ToolOptions.getInstance().display(currentViewer);
    }//GEN-LAST:event_optionsMenuItemActionPerformed

    public void addTemporalProjectionViewer(TemporalProjection tproj) {
        try {
            TemporalProjectionViewer tpv = new TemporalProjectionViewer();
            this.currentViewer = tpv;
            tpv.setTemporalProjection(tproj);

            tpv.setSize(this.desktop.getSize());
            tpv.setVisible(true);
            tpv.setTitle("New Projection " + (this.desktop.getAllFrames().length + 1) + " - " + tproj.getDatabaseCorpus().getCollectionName());
            tpv.setGraphChanged(true);

            this.desktop.add(tpv);

            tpv.setSelected(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(ScienceViewMainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void wizardmenu(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wizardmenu
        TemporalProjection tproj = new TemporalProjection();
        int result = ProjectionWizardView.getInstance().display(tproj);
        if (result == ProjectionWizardView.PROJECTION_GENERATED) {
            this.addTemporalProjectionViewer(tproj);
        }
    }//GEN-LAST:event_wizardmenu

    private void desktopComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_desktopComponentAdded
        if (evt.getChild() instanceof TemporalProjectionViewer) {
            this.exportMenu.setEnabled(true);
            final TemporalProjectionViewer gv = (TemporalProjectionViewer) evt.getChild();
            JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(gv.getTitle());
            this.menus_buttonGroup.add(menuItem);
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ev) {
                    try {
                        gv.setSelected(true);
                    } catch (PropertyVetoException e) {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
                    }
                }
            });
            this.windows.put(gv, menuItem);
            this.menuWindows.add(menuItem);
            menuItem.setSelected(true);
        }
    }//GEN-LAST:event_desktopComponentAdded

    private void searchToolbarTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchToolbarTextFieldKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            this.goToolbarButtonActionPerformed(null);
        }
}//GEN-LAST:event_searchToolbarTextFieldKeyPressed

    private void goToolbarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goToolbarButtonActionPerformed
        Viewer gv = (Viewer) this.desktop.getSelectedFrame();
        if (gv != null && gv instanceof TemporalProjectionViewer) {
            try {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                Scalar s = ((TemporalProjectionViewer) gv).getTemporalProjection().createQueryScalar(this.searchToolbarTextField.getText());
                gv.updateScalars(s);
                this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            } catch (IOException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }

        }
}//GEN-LAST:event_goToolbarButtonActionPerformed

    private void alignVerticallyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alignVerticallyMenuItemActionPerformed
        int nrWindows = this.desktop.getAllFrames().length;
        if (nrWindows > 0) {
            java.awt.Dimension deskSize = this.desktop.getSize();
            int width = deskSize.width / nrWindows;
            for (int i = 0; i < nrWindows; i++) {
                if (this.desktop.getAllFrames()[i] instanceof Viewer) {
                    this.desktop.getAllFrames()[i].setBounds(i * width, 0, width, deskSize.height);
                }
            }
        }
    }//GEN-LAST:event_alignVerticallyMenuItemActionPerformed

    private void highlightLabelToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_highlightLabelToggleButtonActionPerformed
        Viewer.setHighlightTopic(!Viewer.isHighlightTopic());
}//GEN-LAST:event_highlightLabelToggleButtonActionPerformed

    public void cleanTopics() {
        this.cleanLabelsButtonActionPerformed(null);
    }

    private void cleanLabelsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanLabelsButtonActionPerformed
        TemporalProjectionViewer gv = (TemporalProjectionViewer) this.desktop.getSelectedFrame();
        if (gv != null) {
            gv.getProjectionData().setTopicEvolutionGenerated(false);
            gv.cleanSelection(true);
            this.updateTopicInfoPanel(null);
            gv.cleanTopics();
            gv.updateImage();
        }
}//GEN-LAST:event_cleanLabelsButtonActionPerformed

    private void showAllLabelsToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showAllLabelsToggleButtonActionPerformed
        Topic.setShowTopics(!Topic.isShowTopics());
        Viewer gv = (Viewer) this.desktop.getSelectedFrame();
        if (gv != null) {
            gv.repaint();
        }
}//GEN-LAST:event_showAllLabelsToggleButtonActionPerformed

    private void viewContentToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewContentToggleButtonActionPerformed
        Viewer.setType(VertexSelectionFactory.SelectionType.VIEW_CONTENT);
}//GEN-LAST:event_viewContentToggleButtonActionPerformed

    private void createLabelToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createLabelToggleButtonActionPerformed
        Viewer.setType(VertexSelectionFactory.SelectionType.CREATE_TOPIC);
    }//GEN-LAST:event_createLabelToggleButtonActionPerformed

    private void cleanMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanMenuItemActionPerformed
        cleanLabelsButtonActionPerformed(null);
    }//GEN-LAST:event_cleanMenuItemActionPerformed

    private void desktopComponentRemoved(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_desktopComponentRemoved
        if (evt.getChild() instanceof TemporalProjectionViewer) {
            final TemporalProjectionViewer gv = (TemporalProjectionViewer) evt.getChild();
            JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(gv.getTitle());
            this.windows.remove(gv);
            this.menuWindows.remove(menuItem);
            if (this.desktop.getAllFrames().length == 0) {
                this.exportMenu.setEnabled(false);
            }
        }
    }//GEN-LAST:event_desktopComponentRemoved

    private void memorycheckMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_memorycheckMenuItemActionPerformed
        MemoryCheck.showMemoryCheck();
    }//GEN-LAST:event_memorycheckMenuItemActionPerformed

    private void saveProjection() {
        if (this.currentViewer instanceof TemporalProjectionViewer) {
            SaveProjectionDialog saveProjection = new SaveProjectionDialog(currentViewer.getTemporalProjection());
            saveProjection.setVisible(true);
        }
    }

    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
        this.saveProjection();
    }//GEN-LAST:event_saveMenuItemActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            ConnectionManager.getInstance().dispose();
        } catch (IOException ex) {
            Logger.getLogger(ScienceViewMainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formWindowClosing

    private void nearestNeighborsListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nearestNeighborsListMouseClicked
        TemporalProjectionViewer temporalViewer = (TemporalProjectionViewer) this.desktop.getSelectedFrame();
        if (temporalViewer.getGraph() != null) {
            Vertex v = ((Neighbor) this.nearestNeighborsList.getSelectedValue()).getVertex();
            if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                if (evt.getClickCount() == 2) {
                    int[] documents = new int[1];
                    documents[0] = v.getId();
                    MultipleDocumentViewer documentViewer = new MultipleDocumentViewer(documents, temporalViewer.getTemporalProjection().getDatabaseCorpus());
                    documentViewer.display();
                }
            }
        }
}//GEN-LAST:event_nearestNeighborsListMouseClicked

    private void exportPNGMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportPNGMenuItemActionPerformed
        JInternalFrame frame = this.desktop.getSelectedFrame();
        if (frame != null && frame instanceof TemporalProjectionViewer) {
            TemporalProjectionViewer temporalViewer = (TemporalProjectionViewer) frame;
            String filename;
            if (temporalViewer.getGraph() != null) {
                SystemPropertiesManager m = SystemPropertiesManager.getInstance();
                final JFileChooser fc = new JFileChooser();
                filename = temporalViewer.getTemporalProjection().getProjectionData().getCollectionName().concat(".png");

                fc.setAcceptAllFileFilterUsed(false);
                fc.setMultiSelectionEnabled(false);
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                String directory = m.getProperty("IMAGES.DIR");
                if (directory != null) {
                    fc.setCurrentDirectory(new File(directory));
                } else {
                    fc.setCurrentDirectory(new File("."));
                }
                PNGFileFilter filter = new PNGFileFilter();
                fc.addChoosableFileFilter(filter);
                fc.setSelectedFile(new File(filename));
                int result = fc.showSaveDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    try {
                        filename = fc.getSelectedFile().getPath();
                        m.setProperty("IMAGES.DIR", fc.getSelectedFile().getParent());
                        //checking if the name finishes with the correct extension
                        if (!filename.toLowerCase().endsWith("." + filter.getFileExtension())) {
                            filename = filename.concat("." + filter.getFileExtension());
                        }
                        temporalViewer.saveToPngImageFile(filename);
                    } catch (IOException ex) {
                        Logger.getLogger(ScienceViewMainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }//GEN-LAST:event_exportPNGMenuItemActionPerformed

    private void showVertexLabelToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showVertexLabelToggleButtonActionPerformed
        Viewer.setType(VertexSelectionFactory.SelectionType.SHOW_VERTEX_LABEL);
    }//GEN-LAST:event_showVertexLabelToggleButtonActionPerformed

    private void openProjectionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openProjectionMenuItemActionPerformed
        (new OpenProjectionDialog()).setVisible(true);
    }//GEN-LAST:event_openProjectionMenuItemActionPerformed

    private void selectVertexToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectVertexToggleButtonActionPerformed
        Viewer.setType(VertexSelectionFactory.SelectionType.SELECT_GRAPH);
    }//GEN-LAST:event_selectVertexToggleButtonActionPerformed

    private void documentsTabbedPaneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_documentsTabbedPaneMouseClicked
        this.documents_or_groups = this.documentsTabbedPane.getSelectedIndex();
    }//GEN-LAST:event_documentsTabbedPaneMouseClicked

    private void stressMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stressMenuItemActionPerformed
        JInternalFrame frame = this.desktop.getSelectedFrame();
        if (frame != null && frame instanceof TemporalProjectionViewer) {
            TemporalProjectionViewer temporalViewer = (TemporalProjectionViewer) frame;
            StressJDialog stressDialog = new StressJDialog(this);
            stressDialog.createStressChart(temporalViewer.getTemporalProjection().getProjectionData());
            stressDialog.setVisible(true);
            stressDialog.setLocationRelativeTo(this);
        }
    }//GEN-LAST:event_stressMenuItemActionPerformed

    private void zoomSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_zoomSpinnerStateChanged
        JInternalFrame frame = this.desktop.getSelectedFrame();
        if (frame != null && frame instanceof TemporalProjectionViewer) {
            TemporalProjectionViewer temporalViewer = (TemporalProjectionViewer) frame;
            temporalViewer.setZoomRate(((Integer) this.zoomSpinner.getValue()).doubleValue() / 100d);
            temporalViewer.updateImage();
        }
    }//GEN-LAST:event_zoomSpinnerStateChanged

    private void removezoomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removezoomButtonActionPerformed
        this.zoomSpinner.setValue(100);
        JInternalFrame frame = this.desktop.getSelectedFrame();
        if (frame != null && frame instanceof TemporalProjectionViewer) {
            TemporalProjectionViewer temporalViewer = (TemporalProjectionViewer) frame;
            temporalViewer.resetScrollBars();
            this.zoomSpinner.setValue(100);
            System.gc();
            temporalViewer.setZoomRate(1d);
            temporalViewer.updateImage();
        }
    }//GEN-LAST:event_removezoomButtonActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        JInternalFrame frame = this.desktop.getSelectedFrame();
        if (frame != null && frame instanceof TemporalProjectionViewer) {
            TemporalProjectionViewer temporalViewer = (TemporalProjectionViewer) frame;
            MONICSettings monicSettings = new MONICSettings(temporalViewer);
            monicSettings.setVisible(true);
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    public double setZoom(int increment) {
        this.zoomSpinner.setValue((Integer) this.zoomSpinner.getValue() + Integer.valueOf(increment) * 5);
        return ((Integer) this.zoomSpinner.getValue()).doubleValue() / 100d;
    }

//    private void export2DMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
//        JInternalFrame frame = this.desktop.getSelectedFrame();
//        if (frame != null && frame instanceof TemporalProjectionViewer) {
//            TemporalProjectionViewer temporalViewer = (TemporalProjectionViewer) frame;
//            String filename = "projection.prj";
//            if (temporalViewer.getGraph() != null) {
//                SystemPropertiesManager m = SystemPropertiesManager.getInstance();
//                final JFileChooser fc = new JFileChooser();
//                filename = temporalViewer.getTemporalProjection().getProjectionData().getCollectionName().concat(".prj");
//
//                fc.setAcceptAllFileFilterUsed(false);
//                fc.setMultiSelectionEnabled(false);
//                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
//
//                String directory = m.getProperty("PROJECTION.DIR");
//                if (directory != null) {
//                    fc.setCurrentDirectory(new File(directory));
//                } else {
//                    fc.setCurrentDirectory(new File("."));
//                }
//                ProjectionPointsFileFilter filter = new ProjectionPointsFileFilter();
//                fc.addChoosableFileFilter(filter);
//                fc.setSelectedFile(new File(filename));
//                int result = fc.showSaveDialog(this);
//                if (result == JFileChooser.APPROVE_OPTION) {
//                    try {
//                        filename = fc.getSelectedFile().getPath();
//                        m.setProperty("PROJECTION.DIR", fc.getSelectedFile().getParent());
//                        //checking if the name finishes with the correct extension
//                        if (!filename.toLowerCase().endsWith("." + filter.getFileExtension())) {
//                            filename = filename.concat("." + filter.getFileExtension());
//                        }
//                        temporalViewer.saveToProjectionFile(filename);
//                    } catch (IOException ex) {
//                        Logger.getLogger(ScienceViewMainFrame.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//            }
//        }
//    }
    public class Neighbor {

        private Edge edge;
        private Vertex vertex;
        private boolean show_weight = false;
        public int in_or_out = Neighbor.IN;
        public static final int IN = 1, OUT = 2;

        public Neighbor(Edge edge, Vertex vertex, boolean show_weight) {
            this.edge = edge;
            this.vertex = vertex;
            this.show_weight = show_weight;
        }

        public Edge getEdge() {
            return this.edge;
        }

        public Vertex getVertex() {
            return this.vertex;
        }

        @Override
        public String toString() {
            if (show_weight) {
                return "[" + edge.getWeight() + "] " + currentViewer.getTemporalProjection().getTitleDocument(vertex.getId());
            } else if (in_or_out == Neighbor.IN) {
                return "[IN] " + currentViewer.getTemporalProjection().getTitleDocument(vertex.getId());
            } else {
                return "[OUT] " + currentViewer.getTemporalProjection().getTitleDocument(vertex.getId());
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu ToolMenu;
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenuItem alignVerticallyMenuItem;
    private javax.swing.JTable authorsTable;
    private javax.swing.JTextField averageYearTextField;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton cleanLabelsButton;
    private javax.swing.JMenuItem cleanMenuItem;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JScrollPane contentScrollPane;
    private javax.swing.JTextArea contentTextArea;
    private javax.swing.JMenu dataminingMenu;
    private javax.swing.JDesktopPane desktop;
    private javax.swing.JPanel documentsPanel;
    private javax.swing.JScrollPane documentsScrollPane;
    private javax.swing.JTabbedPane documentsTabbedPane;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitFileMenuItem;
    private javax.swing.JMenu exportMenu;
    private javax.swing.JMenuItem exportPNGMenuItem;
    private javax.swing.JTextField gccTextField;
    private javax.swing.JButton goToolbarButton;
    private javax.swing.JToolBar graphToolBar;
    private javax.swing.JMenu helpMenu;
    public javax.swing.JToggleButton highlightLabelToggleButton;
    private javax.swing.JTextField idTextField;
    private javax.swing.JMenu importMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTextField lccTextField;
    private javax.swing.JMenuItem managestopwordsMenuItem;
    private javax.swing.JTextField markedPointField;
    private javax.swing.JPanel markedPointFields;
    private javax.swing.JPanel markedPointPanel;
    private javax.swing.JMenuItem memorycheckMenuItem;
    private javax.swing.JMenu menuWindows;
    private javax.swing.ButtonGroup menus_buttonGroup;
    private javax.swing.JScrollPane nearestNeighborScrollPanel;
    private javax.swing.JList nearestNeighborsList;
    private javax.swing.JPanel neighborsPanel;
    private javax.swing.JMenuItem newProjectionMenuItem;
    private javax.swing.JTextField numberOfDocumentsTextField;
    private javax.swing.JButton openButton;
    private javax.swing.JMenuItem openProjectionMenuItem;
    private javax.swing.JMenuItem optionsMenuItem;
    private javax.swing.JPanel propertiesPanel;
    private javax.swing.JButton removezoomButton;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JLabel searchToolbarLabel;
    private javax.swing.JTextField searchToolbarTextField;
    private javax.swing.JToggleButton selectVertexToggleButton;
    private javax.swing.JLabel separatorLabel3;
    public javax.swing.JToggleButton showAllLabelsToggleButton;
    private javax.swing.JToggleButton showVertexLabelToggleButton;
    private javax.swing.JLabel spaceLabel;
    private javax.swing.JLabel spaceLabel1;
    private javax.swing.JLabel spaceLabel2;
    private javax.swing.JMenuItem stressMenuItem;
    private javax.swing.JTextField titleTextField;
    private javax.swing.JTextField topicIdTextField;
    private javax.swing.JScrollPane topicsScrollPane;
    private javax.swing.JToggleButton viewContentToggleButton;
    private javax.swing.JSpinner zoomSpinner;
    // End of variables declaration//GEN-END:variables
}
