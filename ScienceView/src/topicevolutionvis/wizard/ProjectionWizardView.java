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
 * Contributor(s): Rosane Minghim <rminghim@icmc.usp.br>
 *
 * You should have received a copy of the GNU General Public License along 
 * with PEx. If not, see <http://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ***** */
package topicevolutionvis.wizard;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import topicevolutionvis.database.DatabaseCorpus;
import topicevolutionvis.projection.ProjectionData;
import topicevolutionvis.projection.temporal.TemporalProjection;
import topicevolutionvis.view.ScienceViewMainFrame;

/**
 *
 * @author  Fernando Vieira Paulovich
 */
public class ProjectionWizardView extends JDialog
{ 
	private ProjectionWizardController wizardController;

    private JPanel wizardPanel;

    private WizardPanel innerWizardPanel;
    
    private JPanel buttonPanel;
       
    private JButton cancelButton;
    
    private JButton resetButton;

    private JButton previousButton;

    private JButton nextButton; 
    
    public ProjectionWizardView(TemporalProjection project) {
        super(ScienceViewMainFrame.getInstance());
        initComponents();

        wizardController = new ProjectionWizardController(project);
        definePanel(ProjectionWizardController.NEXT_STATE);
        setLocationRelativeTo(getParent());
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    private void initComponents()
    {
    	initButtonPanel();
    	initProcessPanel();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Projection wizard");
        setModal(true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                cancelWizard();
            }
        });

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(640, 480));
        getContentPane().add(wizardPanel, BorderLayout.NORTH);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        pack();
    }
    
    private void initButtonPanel() {
        buttonPanel = new JPanel();
        cancelButton = new JButton();
        resetButton = new JButton();
        previousButton = new JButton();
        nextButton = new JButton();

        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cancelWizard();
            }
        });
        buttonPanel.add(cancelButton);

        resetButton.setText("Reset");
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                resetWizard();
            }
        });
        buttonPanel.add(resetButton);
        
        previousButton.setText("<< Back");
        previousButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                goToPreviousStep();
            }
        });
        buttonPanel.add(previousButton);

        nextButton.setText("Next >>");
        nextButton.setInheritsPopupMenu(true);
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                goToNextStep();
            }
        });
        buttonPanel.add(nextButton);
    }
    
    private void initProcessPanel() 
    {
        wizardPanel = new JPanel();
    }

    public void cancelWizard() {
    	if (innerWizardPanel.canCancel()) {
    		innerWizardPanel.cancel();
            if (wizardController != null) {
                wizardController.stopProcess();
                wizardController = null;
            }
            dispose();
    	}
    }

    public void resetWizard() {
    	if (innerWizardPanel.canResetConfiguration()) {
    		innerWizardPanel.resetConfiguration();
    	}
    }

    
    private void goToNextStep() {
    	if (innerWizardPanel.canGoToNextStep()) {
    		innerWizardPanel.refreshData();
        	if (! innerWizardPanel.isNextStepTerminal()) {
        		definePanel(ProjectionWizardController.NEXT_STATE);
        	}
    	}
    }

    private void goToPreviousStep() {
    	if (innerWizardPanel.canGoToPreviousStep()) {
    		definePanel(ProjectionWizardController.PREVIOUS_STATE);
    	}
    }

    public void definePanel(int direction) {
        WizardPanel newPanel = wizardController.getNextPanel(direction);
        
        if (newPanel != null) {
	        // Change the next/finish button label
	        if (! newPanel.isNextStepTerminal()) {
	            nextButton.setText("Next >>");
	        } else {
	            nextButton.setText("Finish");
	        }
	
	        // Activate/Deactivate the previous button
	        if (newPanel.hasPreviousStep()) {
	            previousButton.setEnabled(true);
	        } else {
	            previousButton.setEnabled(false);
	        }
	
	        // Remove the previous panel
	        wizardPanel.removeAll();
	       
	        //Add this panel to the frame
	        innerWizardPanel = newPanel;
	        wizardPanel.add(innerWizardPanel);
	        setLocationRelativeTo(getParent());
	        innerWizardPanel.repaint();
	        validate();
        }
    }
}
