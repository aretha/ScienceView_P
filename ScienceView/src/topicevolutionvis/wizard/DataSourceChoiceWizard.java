package topicevolutionvis.wizard;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import topicevolutionvis.data.*;
import topicevolutionvis.database.CollectionManager;
import topicevolutionvis.database.DatabaseCorpus;
import topicevolutionvis.projection.ProjectionData;
import topicevolutionvis.util.SystemPropertiesManager;
import topicevolutionvis.utils.filefilter.*;
import topicevolutionvis.wizard.WizardPanel;

/**
 * Wizard to create and select collections.
 */
public class DataSourceChoiceWizard extends WizardPanel implements ActionListener
{
    private ProjectionData pdata;
    
    private DatabaseImporter importer;
    
    private CollectionManager collectionManager;

    private JPanel selectCorpusPanel;
    private JLabel selectCorpusNameLabel;
    private JComboBox<String> selectCorpusNameComboBox;
    private JButton selectCorpusRemoveButton;
    private JLabel selectCorpusDescriptionLabel;
    private JTextField selectCorpusDescriptionTextField;
    
    private JPanel newCorpusPanel;
    private JLabel newCorpusNameLabel;
    private JTextField newCorpusNameTextField;
    private JLabel newCorpusDescriptionLabel;
    private JTextField newCorpusDescriptionTextField;
    private JLabel newCorpusInputFilenameLabel;
    private JTextField newCorpusFilenameTextField;
    private JButton newCorpusFilenameSearchButton;
    private JLabel newCorpusNgramLabel;
    private JComboBox<Integer> newCorpusNgramDropbox;
    private JButton newCorpusFilenameLoadCancelButton;
    private JProgressBar newCorpusProgressBar;
       
    private JPanel corpusInformationPanel;
    private JLabel corpusNgramsLabel;
    private JTextField corpusNgramsTextField;
    private JLabel corpusNumberDocumentsLabel;
    private JTextField corpusNumberDocumentsTextField;
    private JLabel corpusNumberReferencesLabel;
    private JTextField corpusNumberReferencesTextField;
    
    private static final String DEFAULT_CORPUS_NAME = "Select...";
    
    /**
     * Creates new form DataSourceChoice
     */
    public DataSourceChoiceWizard(ProjectionData pdata) {
        this.pdata = pdata;
        collectionManager = new CollectionManager();
        initComponents();
        updateCollections("");
    }

    private void updateCollections(String collection) {
        String index = null;
        ArrayList<String> collections = collectionManager.getCollections();

        selectCorpusNameComboBox.removeAllItems();
        selectCorpusNameComboBox.addItem(DEFAULT_CORPUS_NAME);
        for (String col : collections) {
            selectCorpusNameComboBox.addItem(col);
            if (col.equals(collection)) {
                index = col;
            }
        }
        
        if (collection.isEmpty()) {
            selectCorpusNameComboBox.setSelectedIndex(0);
        }
        if (index != null) {
            selectCorpusNameComboBox.setSelectedItem(index);
        }
    }

    private void initComponents() {
    	GridBagConstraints bc = new GridBagConstraints();
		bc.insets = new Insets(1, 1, 1, 1);
		bc.anchor = GridBagConstraints.LINE_START;
		bc.fill = GridBagConstraints.HORIZONTAL;
        
		initSelectCorpusPanel();
    	initCorpusInformationPanel();
    	initNewCorpusPanel();

		setLayout(new GridBagLayout());
    	
    	bc.gridy = 0;
        bc.gridx = 0;
        bc.gridwidth = 1;
    	add(selectCorpusPanel, bc);
    	
    	bc.gridy = 0;
        bc.gridx = 1;
        bc.gridwidth = 1;
    	add(corpusInformationPanel, bc);

        bc.gridy = 1;
        bc.gridx = 0;
        bc.gridwidth = 2;
    	add(newCorpusPanel, bc);
    }
   
    private void initSelectCorpusPanel() {
    	GridBagConstraints bc = new GridBagConstraints();
		bc.insets = new Insets(1, 1, 1, 1);

        selectCorpusPanel = new JPanel();
        selectCorpusPanel.setBorder(BorderFactory.createTitledBorder("Selected collection"));
        selectCorpusPanel.setLayout(new GridBagLayout());

        selectCorpusNameLabel = new JLabel();
        selectCorpusNameLabel.setText("Name:");
		bc.anchor = GridBagConstraints.LINE_START;
        bc.fill = GridBagConstraints.NONE;
        bc.gridy = 0;
        bc.gridx = 0;
        bc.gridwidth = 1;
        selectCorpusPanel.add(selectCorpusNameLabel, bc);
        
        selectCorpusNameComboBox = new JComboBox<String>();
        selectCorpusNameComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                corpusComboBoxActionPerformed(evt);
            }
        });
		bc.anchor = GridBagConstraints.LINE_START;
		bc.fill = GridBagConstraints.HORIZONTAL;
        bc.gridy = 0;
        bc.gridx = 1;
        bc.gridwidth = 1;
        selectCorpusPanel.add(selectCorpusNameComboBox, bc);

        selectCorpusRemoveButton = new JButton();
        selectCorpusRemoveButton.setText("Remove");
        selectCorpusRemoveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });
        bc.fill = GridBagConstraints.NONE;
		bc.anchor = GridBagConstraints.LINE_END;
        bc.gridy = 0;
        bc.gridx = 2;
        bc.gridwidth = 1;
        selectCorpusPanel.add(selectCorpusRemoveButton, bc);
        
        
        selectCorpusDescriptionLabel = new JLabel();
        selectCorpusDescriptionLabel.setText("Description:");
        bc.fill = GridBagConstraints.NONE;
		bc.anchor = GridBagConstraints.LINE_START;
        bc.gridy = 1;
        bc.gridx = 0;
        bc.gridwidth = 1;
        selectCorpusPanel.add(selectCorpusDescriptionLabel, bc);
        
        selectCorpusDescriptionTextField = new JTextField();
        selectCorpusDescriptionTextField.setColumns(30);
        selectCorpusDescriptionTextField.setText("");
		bc.fill = GridBagConstraints.HORIZONTAL;
		bc.anchor = GridBagConstraints.LINE_START;
        bc.gridy = 1;
        bc.gridx = 1;
        bc.gridwidth = 2;
        selectCorpusPanel.add(selectCorpusDescriptionTextField, bc);
    }

    
    private void initCorpusInformationPanel() {
		GridBagConstraints bc = new GridBagConstraints();
		bc.insets = new Insets(1, 1, 1, 1);
		
		corpusInformationPanel = new JPanel();
		corpusInformationPanel.setBorder(BorderFactory.createTitledBorder("Collection information"));
		corpusInformationPanel.setLayout(new GridBagLayout());
		   
		corpusNumberDocumentsLabel = new JLabel();
		corpusNumberDocumentsLabel.setText("Documents");
		bc.anchor = GridBagConstraints.LINE_START;
        bc.fill = GridBagConstraints.NONE;
        bc.gridy = 0;
        bc.gridx = 0;
        bc.gridwidth = 1;
		corpusInformationPanel.add(corpusNumberDocumentsLabel, bc);
		   
		corpusNumberDocumentsTextField = new JTextField();
		corpusNumberDocumentsTextField.setEditable(false);
		corpusNumberDocumentsTextField.setText("");
		bc.fill = GridBagConstraints.HORIZONTAL;
		bc.anchor = GridBagConstraints.LINE_START;
        bc.gridy = 0;
        bc.gridx = 1;
        bc.gridwidth = 1;
		corpusInformationPanel.add(corpusNumberDocumentsTextField, bc);
		
		corpusNumberReferencesLabel = new JLabel();
		corpusNumberReferencesLabel.setText("References");
		bc.anchor = GridBagConstraints.LINE_START;
        bc.fill = GridBagConstraints.NONE;
        bc.gridy = 1;
        bc.gridx = 0;
        bc.gridwidth = 1;
		corpusInformationPanel.add(corpusNumberReferencesLabel, bc);
		   
		corpusNumberReferencesTextField = new JTextField();
		corpusNumberReferencesTextField.setEditable(false);
		corpusNumberReferencesTextField.setText("");
		bc.fill = GridBagConstraints.HORIZONTAL;
		bc.anchor = GridBagConstraints.LINE_START;
        bc.gridy = 1;
        bc.gridx = 1;
        bc.gridwidth = 1;
		corpusInformationPanel.add(corpusNumberReferencesTextField, bc);
		
		corpusNgramsLabel = new JLabel();
		corpusNgramsLabel.setText("NGrams");
		bc.anchor = GridBagConstraints.LINE_START;
        bc.fill = GridBagConstraints.NONE;
        bc.gridy = 2;
        bc.gridx = 0;
        bc.gridwidth = 1;
		corpusInformationPanel.add(corpusNgramsLabel, bc);
		   
		corpusNgramsTextField = new JTextField();
		corpusNgramsTextField.setEditable(false);
		corpusNgramsTextField.setText("");
		bc.fill = GridBagConstraints.HORIZONTAL;
		bc.anchor = GridBagConstraints.LINE_START;
        bc.gridy = 2;
        bc.gridx = 1;
        bc.gridwidth = 1;
		corpusInformationPanel.add(corpusNgramsTextField, bc);
    }
    
    
    private void initNewCorpusPanel() {
    	GridBagConstraints bc = new GridBagConstraints();
		bc.fill = GridBagConstraints.HORIZONTAL;
		bc.anchor = GridBagConstraints.LINE_START;
		bc.insets = new Insets(1, 1, 1, 1);
	
		
	    newCorpusPanel = new JPanel();
	    newCorpusPanel.setBorder(BorderFactory.createTitledBorder("Create new collection"));
	    newCorpusPanel.setLayout(new GridBagLayout());

	    // Name
        newCorpusNameLabel = new JLabel();
	    newCorpusNameLabel.setText("New name:");
		bc.anchor = GridBagConstraints.LINE_START;
        bc.fill = GridBagConstraints.NONE;
        bc.gridy = 0;
        bc.gridx = 0;
        bc.gridwidth = 1;
	    newCorpusPanel.add(newCorpusNameLabel, bc);
	
        newCorpusNameTextField = new JTextField();
	    newCorpusNameTextField.setColumns(30);
		bc.fill = GridBagConstraints.HORIZONTAL;
		bc.anchor = GridBagConstraints.LINE_START;
        bc.gridy = 0;
        bc.gridx = 1;
        bc.gridwidth = 3;
	    newCorpusPanel.add(newCorpusNameTextField, bc);

	    // Description
        newCorpusDescriptionLabel = new JLabel();
	    newCorpusDescriptionLabel.setText("Description:");
		bc.anchor = GridBagConstraints.LINE_START;
        bc.fill = GridBagConstraints.NONE;
        bc.gridy = 1;
        bc.gridx = 0;
        bc.gridwidth = 1;
	    newCorpusPanel.add(newCorpusDescriptionLabel, bc);
	
        newCorpusDescriptionTextField = new JTextField();
	    newCorpusDescriptionTextField.setColumns(30);
		bc.fill = GridBagConstraints.HORIZONTAL;
		bc.anchor = GridBagConstraints.LINE_START;
        bc.gridy = 1;
        bc.gridx = 1;
        bc.gridwidth = 3;
        newCorpusPanel.add(newCorpusDescriptionTextField, bc);

        
	    // N-grams
        newCorpusNgramLabel = new JLabel();
	    newCorpusNgramLabel.setText("Number of grams:");
		bc.anchor = GridBagConstraints.LINE_START;
        bc.fill = GridBagConstraints.NONE;
        bc.gridy = 2;
        bc.gridx = 0;
        bc.gridwidth = 1;
	    newCorpusPanel.add(newCorpusNgramLabel, bc);
	
        newCorpusNgramDropbox = new JComboBox<Integer>();
        newCorpusNgramDropbox.setEditable(false);
	    newCorpusNgramDropbox.setModel(new DefaultComboBoxModel<Integer>(new Integer[] { 1, 2, 3, 4, 5 }));
		bc.fill = GridBagConstraints.HORIZONTAL;
		bc.anchor = GridBagConstraints.LINE_START;
        bc.gridy = 2;
        bc.gridx = 1;
        bc.gridwidth = 1;
	    newCorpusPanel.add(newCorpusNgramDropbox, bc);

	    
	    // Input file
	    newCorpusInputFilenameLabel = new JLabel();
	    newCorpusInputFilenameLabel.setText("Input file:");
		bc.anchor = GridBagConstraints.LINE_START;
        bc.fill = GridBagConstraints.NONE;
        bc.gridy = 3;
        bc.gridx = 0;
        bc.gridwidth = 1;
	    newCorpusPanel.add(newCorpusInputFilenameLabel, bc);
	
        newCorpusFilenameTextField = new JTextField();
	    newCorpusFilenameTextField.setColumns(30);
	    newCorpusFilenameTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				corpusFilenameActionPerformed();
			}
		});
		bc.fill = GridBagConstraints.HORIZONTAL;
		bc.anchor = GridBagConstraints.LINE_START;
        bc.gridy = 3;
        bc.gridx = 1;
        bc.gridwidth = 2;
	    newCorpusPanel.add(newCorpusFilenameTextField, bc);
	
        newCorpusFilenameSearchButton = new JButton();
	    newCorpusFilenameSearchButton.setText("Select file");
	    newCorpusFilenameSearchButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent evt) {
	            searchButtonActionPerformed();
	        }
	    });
		bc.fill = GridBagConstraints.HORIZONTAL;
		bc.anchor = GridBagConstraints.LINE_START;
        bc.gridy = 3;
        bc.gridx = 3;
        bc.gridwidth = 1;
	    newCorpusPanel.add(newCorpusFilenameSearchButton, bc);

	    
	    // Load, cancel, progress bar
        newCorpusFilenameLoadCancelButton = new JButton();
	    newCorpusFilenameLoadCancelButton.setText("Load");
	    newCorpusFilenameLoadCancelButton.setEnabled(false);
	    newCorpusFilenameLoadCancelButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent evt) {
	            loadCancelButtonActionPerformed(evt);
	        }
	    });
		bc.anchor = GridBagConstraints.LINE_START;
        bc.fill = GridBagConstraints.NONE;
        bc.gridy = 4;
        bc.gridx = 0;
        bc.gridwidth = 1;
	    newCorpusPanel.add(newCorpusFilenameLoadCancelButton, bc);

        newCorpusProgressBar = new JProgressBar();
	    newCorpusProgressBar.setPreferredSize(new Dimension(300, 14));
		bc.anchor = GridBagConstraints.LINE_START;
        bc.fill = GridBagConstraints.NONE;
        bc.gridy = 4;
        bc.gridx = 1;
        bc.gridwidth = 3;
	    newCorpusPanel.add(newCorpusProgressBar, bc);
    }
   
    
    protected void corpusFilenameActionPerformed() {
    	File file = new File(newCorpusFilenameTextField.getText().trim());
    	checkNewCorpusInputFile(file);
	}

	private void searchButtonActionPerformed() {
        JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        
        SystemPropertiesManager m = SystemPropertiesManager.getInstance();
        String directory = m.getProperty("COLLECTIONS.DIR");
        if (directory != null) {
            fc.setCurrentDirectory(new File(directory));
        } else {
            fc.setCurrentDirectory(new File("."));
        }
        
        fc.addChoosableFileFilter(new BibTeXFileFilter());
        fc.addChoosableFileFilter(new ISIFileFilter());
        fc.addChoosableFileFilter(new EndnoteExportFileFilter());
        fc.addChoosableFileFilter(new DatabaseFileFilter());
        int result = fc.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            newCorpusFilenameTextField.setText(file.getAbsolutePath());
            newCorpusFilenameTextField.setCaretPosition(0);
            if (newCorpusNameTextField.getText().isEmpty()) {
            	newCorpusNameTextField.setText(file.getName().substring(0, file.getName().indexOf(".")));
            }
            checkNewCorpusInputFile(file);
        }
    }
	
	private void checkNewCorpusInputFile(File file) {
        SystemPropertiesManager m = SystemPropertiesManager.getInstance();
		if (file.isFile() && file.exists() && file.canRead()) {
			newCorpusFilenameLoadCancelButton.setEnabled(true);
            m.setProperty("COLLECTIONS.DIR", file.getParent());
		}
	}

    private void removeButtonActionPerformed(ActionEvent evt) {
        if (selectCorpusNameComboBox.getSelectedIndex() > 0) {
        	String collection = selectCorpusNameComboBox.getItemAt(selectCorpusNameComboBox.getSelectedIndex());
        	int result = JOptionPane.showConfirmDialog(this, "Collection removal", "Confirm removal of collection '" + collection + "'?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        	if (result == JOptionPane.OK_OPTION) {
        		collectionManager.removeCollection(collection);
        		updateCollections("");
        	}
        }
    }

    public void setStatus(String status, boolean running) {
        newCorpusProgressBar.setIndeterminate(running);
    }

    private void loadCancelButtonActionPerformed(ActionEvent evt) {
    	if ("Load".equalsIgnoreCase(newCorpusFilenameLoadCancelButton.getText())) {
	        String collectionName = newCorpusNameTextField.getText().trim();
	        String filename = newCorpusFilenameTextField.getText().trim();
	        File inputFile = new File(filename);
	        if (! filename.isEmpty() && inputFile.isFile() && inputFile.exists() && inputFile.canRead() && ! collectionName.isEmpty()) {
	            pdata.setSourceFile(filename);
	            String corpusType = filename.substring(filename.lastIndexOf(".") + 1);
	            int nrGrams = newCorpusNgramDropbox.getItemAt(newCorpusNgramDropbox.getSelectedIndex());
	            if (corpusType.equalsIgnoreCase("bib")) {
	                BibTeX2RIS bib = null;
	                bib = new BibTeX2RIS();
	                bib.setInputFile(new File(filename));
	                bib.readData();
	                bib.convert();
	                importer = new ISICorpusDatabaseImporter(bib.getOutputFile().getAbsolutePath(), collectionName, nrGrams, this, false);
	            } else if (corpusType.equalsIgnoreCase("isi")) {
	                importer = new ISICorpusDatabaseImporter(filename, collectionName, nrGrams, this, false);
	            } else if (corpusType.equalsIgnoreCase("enw")) {
	                importer = new EndnoteDatabaseImporter(filename, collectionName, nrGrams, this, false);
	            } else if (corpusType.equalsIgnoreCase("db")) {
	                importer = new DumpDatabaseImporter(filename, this, false);
	            }
	
	            if (importer != null) {
	            	newCorpusFilenameLoadCancelButton.setText("Cancel");
	                setStatus("Loading collection " + collectionName + "...", true);
	                loadingCollection();
	                importer.execute();
	            }
	
	        } else {
	            JOptionPane.showMessageDialog(this, "All parameters must be filled to load a new collection", "Warning", JOptionPane.WARNING_MESSAGE);
	        }
    	} else {
    		importer.cancel(true);	
            setStatus("", true);

    	}
    }

    private void corpusComboBoxActionPerformed(ActionEvent evt) {
    	if (selectCorpusNameComboBox.getSelectedIndex() > 0) {
    		String collectionName = selectCorpusNameComboBox.getItemAt(selectCorpusNameComboBox.getSelectedIndex());
    		getInformations(collectionName);
        }
    }
    
    private void loadingCollection() {
        newCorpusFilenameLoadCancelButton.setEnabled(false);
        newCorpusFilenameTextField.setEnabled(false);
        newCorpusNameTextField.setEnabled(false);
        newCorpusFilenameSearchButton.setEnabled(false);
        newCorpusNgramDropbox.setEnabled(false);
        selectCorpusRemoveButton.setEnabled(false);
    }

    public void finishedLoadingCollection(String collection, boolean canceled) {
        if (! canceled) {
            updateCollections(collection);
            newCorpusFilenameTextField.setText(null);
            newCorpusNameTextField.setText(null);
            newCorpusFilenameLoadCancelButton.setText("Load");
        }
        newCorpusFilenameTextField.setEnabled(true);
        newCorpusNameTextField.setEnabled(true);
        newCorpusFilenameSearchButton.setEnabled(true);
        newCorpusNgramDropbox.setEnabled(true);
        newCorpusFilenameLoadCancelButton.setEnabled(true);
        selectCorpusRemoveButton.setEnabled(true);
    }

    public DataSourceChoiceWizard reset() {
        return this;
    }

    @Override
    public void refreshData() {
    	/*
    	if (selectCorpusNameComboBox.getSelectedIndex() > 0) {
    		String collectionName = selectCorpusNameComboBox.getItemAt(selectCorpusNameComboBox.getSelectedIndex());
    		pdata.setCollectionName(collectionName);
    		pdata.setDatabaseCorpus(new DatabaseCorpus(collectionName));
        }
        */
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
    }

    private void getInformations(String collectionName) {
    	if (! collectionManager.isUnique(collectionName)) {
            DatabaseCorpus corpus = new DatabaseCorpus(collectionName);
            Integer ngrams = corpus.getNumberGrams();
            Integer numberDocs = corpus.getNumberOfDocuments();
            Integer numberRef = corpus.getNumberOfUniqueReferences();
            corpusNgramsTextField.setText(ngrams.toString());
            corpusNumberDocumentsTextField.setText(numberDocs.toString());
            corpusNumberReferencesTextField.setText(numberRef.toString());
    		pdata.setCollectionName(collectionName);
    		pdata.setDatabaseCorpus(corpus);
         }
    }

	@Override
	public boolean isNextStepTerminal() {
		return false;
	}

	@Override
	public boolean canGoToNextStep() {
		if (selectCorpusNameComboBox.getSelectedIndex() > 0) {
			String collectionName = selectCorpusNameComboBox.getItemAt(selectCorpusNameComboBox.getSelectedIndex());
	    	if (! collectionManager.isUnique(collectionName)) {
	    		return true;
	    	}
		}
		return false;
	}

	@Override
	public boolean canGoToPreviousStep() {
		return false;
	}

	@Override
	public boolean hasPreviousStep() {
		return false;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public void cancel() {
	}

	@Override
	public boolean canResetConfiguration() {
		return false;
	}

	@Override
	public void resetConfiguration() {
	}
}
