/* ***** BEGIN LICENSE BLOCK *****
 *
 * Copyright (c) 2005-2007 Universidade de Sao Paulo, Sao Carlos/SP, Brazil.
 * All Rights Reserved.
 *
 * This file is part of Hierarchical Projection Explorer (H-PEx).
 *
 * H-PEx is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 *
 * H-PEx is distributed in the hope that it will be useful, but WITHOUT 
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
 * with H-PEx. If not, see <http://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ***** */

package topicevolutionvis.preprocessing.stopwords;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import topicevolutionvis.utils.filefilter.StopwordsFileFilter;

/**
 * A stoprwords lists manager.
 * 
 * @author Fernando Vieira Paulovich
 */
public class StopwordsListManager {

    public static final StopwordsList ENGLISH = new EnglishStopwordsList();
    /** Creates a new instance of StopwordsListManager */
    private StopwordsListManager() {
        this.updateStopwordslists();
    }

    /**
     * Returns an instance of the stopwords list manager.
     * @return The stopwords list manager.
     */
    public static StopwordsListManager getInstance() {
        if (StopwordsListManager.instance == null) {
            StopwordsListManager.instance = new StopwordsListManager();
        }
        return StopwordsListManager.instance;
    }

    /**
     * Returns all available stopwords lists.
     * @return The stopwords lists.
     */
    public ArrayList<StopwordsList> getStopwordsLists() {
        return this.stopwords;
    }

    /**
     * Load all stopwords files from the .\config directory, and add a default
     * english stopwords list.
     */
    public final void updateStopwordslists() {
        //clear the previous stopwords
        this.stopwords = new ArrayList<>();

        //add the default english stopwords list
        this.stopwords.add(StopwordsListManager.ENGLISH);

        File dir = new File(directory);
        File[] listFiles = dir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return (!pathname.isDirectory() && new StopwordsFileFilter().accept(pathname));
            }

        });

        for (File f : listFiles) {
            try {
                stopwords.add(new FileStopwordsList(f.getAbsolutePath()));
            } catch (IOException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private String directory = ".//config";
    private ArrayList<StopwordsList> stopwords = new ArrayList<>();
    private static StopwordsListManager instance;
}
