package topicevolutionvis.wizard;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

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

    private JPanel existingCollectionPanel;
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
    private JButton newCorpusFilenameLoadButton;
    private JButton newCorpusFilenameCancelLoadingButton;
    private JProgressBar newCorpusProgressBar;
       
    private JPanel corpusInformationPanel;
    private JLabel corpusNgramsLabel;
    private JTextField corpusNgramsTextField;
    private JLabel corpusNumberDocumentsLabel;
    private JTextField corpusNumberDocumentsTextField;
    private JLabel corpusNumberReferencesLabel;
    private JTextField corpusNumberReferencesTextField;
    
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
        selectCorpusNameComboBox.removeAllItems();
        ArrayList<String> collections = collectionManager.getCollections();
        selectCorpusNameComboBox.addItem("Select...");
        for (String col : collections) {
            this.selectCorpusNameComboBox.addItem(col);
            if (collection.equalsIgnoreCase(col)) {
                index = col;
            }
        }
        if (collection.equalsIgnoreCase("")) {
            selectCorpusNameComboBox.setSelectedIndex(0);
        }
        if (index != null) {
            selectCorpusNameComboBox.setSelectedItem(index);
        }
    }

    private void initComponents() {
    	initExistingCollectionPanel();
    	initNewCorpusPanel();
    	add(existingCollectionPanel);
    	add(newCorpusPanel);
    }
   
    private void initExistingCollectionPanel() {
    	existingCollectionPanel = new JPanel();
    	existingCollectionPanel.setLayout(new BorderLayout());
    	initSelectCorpusPanel();
    	initCorpusInformationPanel();
    	existingCollectionPanel.add(selectCorpusPanel, BorderLayout.LINE_START);
    	existingCollectionPanel.add(corpusInformationPanel, BorderLayout.LINE_END);
    }

    private void initSelectCorpusPanel() {
    	GridBagConstraints labelBag = new GridBagConstraints();
		labelBag.fill = GridBagConstraints.HORIZONTAL;
		labelBag.anchor = GridBagConstraints.LINE_START;
		labelBag.insets = new Insets(1, 1, 1, 1);
		labelBag.weightx = 0.0;
		labelBag.gridwidth = 1;
		
	    GridBagConstraints contentBag = new GridBagConstraints();
	    contentBag.fill = GridBagConstraints.HORIZONTAL;
		contentBag.anchor = GridBagConstraints.LINE_START;
		contentBag.weightx = 1.0;
		contentBag.gridwidth = GridBagConstraints.REMAINDER;
		contentBag.insets = new Insets(1, 1, 1, 1);
		
		GridBagConstraints extraBag = new GridBagConstraints();
		labelBag.fill = GridBagConstraints.HORIZONTAL;
		labelBag.anchor = GridBagConstraints.LINE_END;
		labelBag.insets = new Insets(1, 1, 1, 1);
		labelBag.weightx = 0.0;
		labelBag.gridwidth = 1;

        selectCorpusPanel = new JPanel();
        selectCorpusPanel.setBorder(BorderFactory.createTitledBorder("Selected collection"));
        selectCorpusPanel.setLayout(new GridBagLayout());

        selectCorpusNameLabel = new JLabel();
        selectCorpusNameLabel.setText("Name:");
        selectCorpusPanel.add(selectCorpusNameLabel, labelBag);
        
        selectCorpusNameComboBox = new JComboBox<String>();
        selectCorpusNameComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                corpusComboBoxActionPerformed(evt);
            }
        });
        selectCorpusPanel.add(selectCorpusNameComboBox, contentBag);

        selectCorpusRemoveButton = new JButton();
        selectCorpusRemoveButton.setText("Remove");
        selectCorpusRemoveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });
        selectCorpusPanel.add(selectCorpusRemoveButton, extraBag);
        
        
        selectCorpusDescriptionLabel = new JLabel();
        selectCorpusDescriptionLabel.setText("Description:");
        selectCorpusPanel.add(selectCorpusDescriptionLabel, labelBag);
        
        selectCorpusDescriptionTextField = new JTextField();
        selectCorpusDescriptionTextField.setText("");
        selectCorpusPanel.add(selectCorpusDescriptionTextField, contentBag);
    }

    
    private void initCorpusInformationPanel() {
		GridBagConstraints labelBag = new GridBagConstraints();
		labelBag.fill = GridBagConstraints.HORIZONTAL;
		labelBag.anchor = GridBagConstraints.NORTHWEST;
		labelBag.insets = new Insets(1, 1, 1, 1);
		labelBag.weightx = 0.0;
		labelBag.gridwidth = 1;
		
	    GridBagConstraints contentBag = new GridBagConstraints();
	    contentBag.fill = GridBagConstraints.HORIZONTAL;
		contentBag.anchor = GridBagConstraints.NORTHWEST;
		contentBag.weightx = 1.0;
		contentBag.gridwidth = GridBagConstraints.REMAINDER;
		contentBag.insets = new Insets(1, 1, 1, 1);
    	
		corpusInformationPanel = new JPanel();
		corpusInformationPanel.setBorder(BorderFactory.createTitledBorder("Collection information"));
		corpusInformationPanel.setLayout(new GridBagLayout());
		   
		corpusNumberDocumentsLabel = new JLabel();
		corpusNumberDocumentsLabel.setText("Documents");
		corpusInformationPanel.add(corpusNumberDocumentsLabel, labelBag);
		   
		corpusNumberDocumentsTextField = new JTextField();
		corpusNumberDocumentsTextField.setEditable(false);
		corpusNumberDocumentsTextField.setText("");
		corpusInformationPanel.add(corpusNumberDocumentsTextField, contentBag);
		
		corpusNumberReferencesLabel = new JLabel();
		corpusNumberReferencesLabel.setText("References");
		corpusInformationPanel.add(corpusNumberReferencesLabel, labelBag);
		   
		corpusNumberReferencesTextField = new JTextField();
		corpusNumberReferencesTextField.setEditable(false);
		corpusNumberReferencesTextField.setText("");
		corpusInformationPanel.add(corpusNumberReferencesTextField, contentBag);
		
		corpusNgramsLabel = new JLabel();
		corpusNgramsLabel.setText("NGrams");
		corpusInformationPanel.add(corpusNgramsLabel, labelBag);
		   
		corpusNgramsTextField = new JTextField();
		corpusNgramsTextField.setEditable(false);
		corpusNgramsTextField.setText("");
		corpusInformationPanel.add(corpusNgramsTextField, contentBag);
    }
    
    
    private void initNewCorpusPanel() {
    	GridBagConstraints labelBag = new GridBagConstraints();
		labelBag.fill = GridBagConstraints.HORIZONTAL;
		labelBag.anchor = GridBagConstraints.LINE_START;
		labelBag.insets = new Insets(1, 1, 1, 1);
		labelBag.weightx = 0.0;
		labelBag.gridwidth = 1;
		
	    GridBagConstraints contentBag = new GridBagConstraints();
	    contentBag.fill = GridBagConstraints.HORIZONTAL;
		contentBag.anchor = GridBagConstraints.LINE_START;
		contentBag.weightx = 1.0;
		contentBag.gridwidth = GridBagConstraints.REMAINDER;
		contentBag.insets = new Insets(1, 1, 1, 1);
		
		GridBagConstraints extraBag = new GridBagConstraints();
		labelBag.fill = GridBagConstraints.HORIZONTAL;
		labelBag.anchor = GridBagConstraints.LINE_END;
		labelBag.insets = new Insets(1, 1, 1, 1);
		labelBag.weightx = 0.0;
		labelBag.gridwidth = 1;
		
	    newCorpusPanel = new JPanel();
	    newCorpusPanel.setBorder(BorderFactory.createTitledBorder("Create new collection"));
	    newCorpusPanel.setLayout(new GridBagLayout());

	    // Name
        newCorpusNameLabel = new JLabel();
	    newCorpusNameLabel.setText("New name:");
	    newCorpusPanel.add(newCorpusNameLabel, labelBag);
	
        newCorpusNameTextField = new JTextField();
	    newCorpusNameTextField.setColumns(40);
	    newCorpusPanel.add(newCorpusNameTextField, contentBag);

	    // Description
        newCorpusDescriptionLabel = new JLabel();
	    newCorpusDescriptionLabel.setText("Description:");
	    newCorpusPanel.add(newCorpusDescriptionLabel, labelBag);
	
        newCorpusDescriptionTextField = new JTextField();
	    newCorpusDescriptionTextField.setColumns(60);
	    newCorpusPanel.add(newCorpusDescriptionTextField, contentBag);
	    
	    
	    // Input file
	    newCorpusInputFilenameLabel = new JLabel();
	    newCorpusInputFilenameLabel.setText("Input file:");
	    newCorpusPanel.add(newCorpusInputFilenameLabel, labelBag);
	
        newCorpusFilenameTextField = new JTextField();
	    newCorpusFilenameTextField.setColumns(60);
	    newCorpusPanel.add(newCorpusFilenameTextField, contentBag);
	
        newCorpusFilenameSearchButton = new JButton();
	    newCorpusFilenameSearchButton.setText("Select file");
	    newCorpusFilenameSearchButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent evt) {
	            searchButtonActionPerformed(evt);
	        }
	    });
	    newCorpusPanel.add(newCorpusFilenameSearchButton, extraBag);

	    
	    // N-grams
        newCorpusNgramLabel = new JLabel();
	    newCorpusNgramLabel.setText("Number of grams:");
	    newCorpusPanel.add(newCorpusNgramLabel, labelBag);
	
        newCorpusNgramDropbox = new JComboBox<Integer>();
	    newCorpusNgramDropbox.setModel(new DefaultComboBoxModel<Integer>(new Integer[] { 1, 2, 3, 4, 5 }));
	    newCorpusPanel.add(newCorpusNgramDropbox, contentBag);

	    
	    // Load, cancel, progress bar
        newCorpusFilenameLoadButton = new JButton();
	    newCorpusFilenameLoadButton.setText("Load");
	    newCorpusFilenameLoadButton.setEnabled(false);
	    newCorpusFilenameLoadButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent evt) {
	            loadButtonActionPerformed(evt);
	        }
	    });
	    newCorpusPanel.add(newCorpusFilenameLoadButton, labelBag);
	
        newCorpusFilenameCancelLoadingButton = new JButton();
	    newCorpusFilenameCancelLoadingButton.setText("Cancel");
	    newCorpusFilenameCancelLoadingButton.setEnabled(false);
	    newCorpusFilenameCancelLoadingButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent evt) {
	            cancelButtonActionPerformed(evt);
	        }
	    });
	    newCorpusPanel.add(newCorpusFilenameCancelLoadingButton, labelBag);
	
	
        newCorpusProgressBar = new JProgressBar();
	    newCorpusProgressBar.setPreferredSize(new Dimension(300, 14));
	    newCorpusPanel.add(newCorpusProgressBar, contentBag);
    }
   
    
    private void searchButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        final JFileChooser fc = new JFileChooser();
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
        SupportedFormatsCollections generalFilter = new SupportedFormatsCollections();
        fc.addChoosableFileFilter(generalFilter);
        fc.addChoosableFileFilter(new BibTeXFileFilter());
        fc.addChoosableFileFilter(new ISIFileFilter());
        fc.addChoosableFileFilter(new EndnoteExportFileFilter());
        fc.addChoosableFileFilter(new DatabaseFileFilter());
        fc.setFileFilter(generalFilter);
        int result = fc.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            newCorpusFilenameTextField.setText(file.getAbsolutePath());
            newCorpusFilenameTextField.setCaretPosition(0);
            this.newCorpusNameTextField.setText(file.getName().substring(0, file.getName().indexOf(".")));
            m.setProperty("COLLECTIONS.DIR", fc.getSelectedFile().getParent());
        }
}//GEN-LAST:event_searchButtonActionPerformed

    private void removeButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        if (this.selectCorpusNameComboBox.getSelectedIndex() > 0) {
           String collection = (String) this.selectCorpusNameComboBox.getSelectedItem();
            collectionManager.removeCollection(collection);
            this.selectCorpusNameComboBox.removeItem(collection);
        } else {
            JOptionPane.showMessageDialog(this, "A collection must be selected.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    public void setStatus(String status, boolean running) {
        this.statusLabel.setText(status);
        this.newCorpusProgressBar.setIndeterminate(running);
    }

    private void loadButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_loadButtonActionPerformed
        String collectionName = this.newCorpusNameTextField.getText().trim();
        String filename = this.newCorpusFilenameTextField.getText().trim();
        if (filename.compareTo("") != 0 && collectionName.compareTo("") != 0) {
            this.pdata.setSourceFile(filename);
            String corpusType = filename.substring(filename.lastIndexOf(".") + 1);
            int nrGrams = Integer.valueOf((String) this.newCorpusNgramDropbox.getSelectedItem()).intValue();
            if (corpusType.compareTo("bib") == 0) {
                BibTeX2RIS bib = null;
                bib = new BibTeX2RIS();
                bib.setInputFile(new File(filename));
                bib.readData();
                bib.convert();
                importer = new ISICorpusDatabaseImporter(bib.getOutputFile().getAbsolutePath(), collectionName, nrGrams, this, newCorpusApplyStopwords.isSelected());
            } else if (corpusType.compareTo("isi") == 0) {
                importer = new ISICorpusDatabaseImporter(filename, collectionName, nrGrams, this, newCorpusApplyStopwords.isSelected());
            } else if (corpusType.compareTo("enw") == 0) {
                importer = new EndnoteDatabaseImporter(filename, collectionName, nrGrams, this, newCorpusApplyStopwords.isSelected());
            } else if (corpusType.compareTo("db") == 0) {
                importer = new DumpDatabaseImporter(filename, this, newCorpusApplyStopwords.isSelected());
            }

            if (importer != null) {
                this.newCorpusFilenameCancelLoadingButton.setEnabled(true);
                this.setStatus("Loading collection " + collectionName + "...", true);
                this.loadingCollection();
                importer.execute();
            }

        } else {
            JOptionPane.showMessageDialog(this, "All parameters must be filled to load a new collection", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_loadButtonActionPerformed

    private void cancelButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.importer.cancel(true);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void corpusComboBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_corpusComboBoxActionPerformed
        String selected_item;
        selected_item = (String) this.selectCorpusNameComboBox.getSelectedItem();
        if (selected_item != null){
            try {
                getInformations(selected_item);
            } catch (IOException ex) {
                Logger.getLogger(DataSourceChoiceWizard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    private void loadingCollection() {
        this.newCorpusFilenameLoadButton.setEnabled(false);
        this.newCorpusFilenameTextField.setEnabled(false);
        this.newCorpusNameTextField.setEnabled(false);
        this.newCorpusFilenameSearchButton.setEnabled(false);
        this.newCorpusNgramDropbox.setEnabled(false);
        this.selectCorpusRemoveButton.setEnabled(false);
    }

    public void finishedLoadingCollection(String collection, boolean canceled) {
        if (! canceled) {
            this.updateCollections(collection);
            this.newCorpusFilenameTextField.setText(null);
            this.newCorpusNameTextField.setText(null);
            this.newCorpusFilenameCancelLoadingButton.setEnabled(false);
        }
        this.newCorpusFilenameTextField.setEnabled(true);
        this.newCorpusNameTextField.setEnabled(true);
        this.newCorpusFilenameSearchButton.setEnabled(true);
        this.newCorpusNgramDropbox.setEnabled(true);
        this.newCorpusFilenameLoadButton.setEnabled(true);
        this.selectCorpusRemoveButton.setEnabled(true);
    }

    public DataSourceChoiceWizard reset() {
        return this;
    }

    @Override
    public void refreshData() {
        String collectionName = (String) this.selectCorpusNameComboBox.getSelectedItem();
        if (collectionName.compareToIgnoreCase("Select...") != 0) {
            this.pdata.setCollectionName(collectionName);

        }

    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void getInformations(String collectionName) throws IOException {
        if (collectionName != null && (collectionName.equals("Select...") || collectionName.isEmpty())){
            this.corpusNgramsTextField.setText("");
            this.corpusNumberDocumentsTextField.setText("");
            this.corpusNumberReferencesTextField.setText("");
         } else {
            DatabaseCorpus corpus = new DatabaseCorpus(collectionName); 
            Integer ngrams = corpus.getNumberGrams();
            Integer numberDocs = corpus.getNumberOfDocuments();
            Integer numberRef = corpus.getNumberOfUniqueReferences();
            this.corpusNgramsTextField.setText(ngrams.toString());
            this.corpusNumberDocumentsTextField.setText(numberDocs.toString());
            this.corpusNumberReferencesTextField.setText(numberRef.toString());
            pdata.setCollectionName(collectionName);
         }
    }

	@Override
	public boolean isNextStepTerminal() {
		return false;
	}

	@Override
	public boolean canGoToNextStep() {
       if (pdata.getCollectionName() != null) {
            pdata.setDatabaseCorpus(new DatabaseCorpus(pdata.getCollectionName()));
            return true;
       }
       return false;
	}

	@Override
	public boolean canGoToPreviousStep() {
		return true;
	}

	@Override
	public boolean hasPreviousStep() {
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
	public boolean canResetConfiguration() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resetConfiguration() {
		// TODO Auto-generated method stub
		
	}
}
