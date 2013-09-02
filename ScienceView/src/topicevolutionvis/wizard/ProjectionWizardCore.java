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

import topicevolutionvis.projection.ProjectionData;
import topicevolutionvis.projection.lsp.LSPProjectionParametersView;
import topicevolutionvis.projection.temporal.TemporalGraphBuilder;
import topicevolutionvis.projection.temporal.TemporalProjection;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class ProjectionWizardCore {

    public ProjectionData pdata;
    private TemporalProjection tproj;
    //Possible directions to follow in the wizard
    public static final int NEXT_STATE = 0;
    public static final int PREVIOUS_STATE = 1;
    //States of create projection wizard
    private int currentState = INITIAL_STATE;
    private static final int INITIAL_STATE = 0;
    private static final int SOURCE_STATE = 1;
    private static final int PRE_PROC_STATE = 5;
    private static final int DIMEN_RED_STATE = 6;
    private static final int PROJ_DIST_STATE = 7;
    private static final int PROJECT_STATE = 8;
    //Views of each state
    private DataSourceChoice sourceView;
    private Preprocessing preprocessView;
    private ProjectionDistanceChoice projDistView;
    private DimensionReduction dimensionReductionView;
    private TemporalGraphBuilder builder;
    private ProjectionView projView;

    public ProjectionWizardCore(TemporalProjection tproj) {
        this.tproj = tproj;
        this.pdata = new ProjectionData();
        this.tproj.setProjectionData(pdata);
        this.sourceView = new DataSourceChoice(pdata);
        this.preprocessView = new Preprocessing(pdata);
        this.projDistView = new ProjectionDistanceChoice(pdata);
        this.dimensionReductionView = new DimensionReduction(pdata);
    }

    public WizardPanel getNextPanel(int direction) {

        switch (this.currentState) {
            case ProjectionWizardCore.INITIAL_STATE:
                //initial -> source
                currentState = ProjectionWizardCore.SOURCE_STATE;
                return sourceView.reset();

            case ProjectionWizardCore.SOURCE_STATE:
                //source -> projection+distance
                if (direction == ProjectionWizardCore.NEXT_STATE) {
                    currentState = ProjectionWizardCore.PRE_PROC_STATE;
                    return preprocessView;
                }
                break;
            case ProjectionWizardCore.PRE_PROC_STATE:
                //pre-processing -> projection+distance
                if (direction == ProjectionWizardCore.PREVIOUS_STATE) {
                    currentState = ProjectionWizardCore.SOURCE_STATE;
                    return sourceView.reset();
                } else {
                    this.currentState = ProjectionWizardCore.DIMEN_RED_STATE;
                    return this.dimensionReductionView.reset();
                }
            case ProjectionWizardCore.DIMEN_RED_STATE:
                if (direction == ProjectionWizardCore.PREVIOUS_STATE) {
                    this.currentState = ProjectionWizardCore.PRE_PROC_STATE;
                    return this.preprocessView.reset();
                } else {
                    this.currentState = ProjectionWizardCore.PROJ_DIST_STATE;
                    return this.projDistView.reset();
                }
            case ProjectionWizardCore.PROJ_DIST_STATE:
                if (direction == ProjectionWizardCore.PREVIOUS_STATE) {
                    this.currentState = ProjectionWizardCore.DIMEN_RED_STATE;
                    return this.dimensionReductionView.reset();
                } else {
                    //  projView = ProjectionFactory.getInstance(pdata.getProjectionType()).getProjectionView(pdata);
                    projView = new LSPProjectionParametersView(pdata);
                    projView.reset();
                    this.currentState = ProjectionWizardCore.PROJECT_STATE;
                    return projView;
                }
            case ProjectionWizardCore.PROJECT_STATE:
                if (direction == ProjectionWizardCore.PREVIOUS_STATE) {
                    this.currentState = ProjectionWizardCore.PROJ_DIST_STATE;
                    return this.projDistView.reset();
                } else {
                    builder = new TemporalGraphBuilder(projView, tproj, pdata.getDatabaseCorpus());
                    builder.start(pdata);
                }
                break;
        }
        return null;
    }

    public TemporalProjection getTemporalProjection() {
        return this.tproj;
    }

    public void stopProcess() {
        this.currentState = INITIAL_STATE;
    }
}
