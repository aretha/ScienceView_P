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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Represents a stopwords list.
 * 
 * @author Fernando Vieira Paulovich
 */
public abstract class StopwordsList implements Serializable {

    /** 
     * Creates a new instance of StopwordsList
     */
    public StopwordsList() {
    }

    /**
     * Fills the stopword list with the stopwords.
     * @throws java.io.IOException Throws an exception if somenthing goes wrong.
     */
    public abstract void fill() throws IOException;

    /**
     * Changes the stopwords list name.
     * @param name The new name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the stopwords list.
     * @return The stopwords list.
     */
    public ArrayList<String> getStopwords() {
        return this.stopwords;
    }

    /**
     * Refreshes the stopwords list, returning it to its initial state.
     * @throws java.io.IOException Throws an exception if somenthing goes wrong.
     */
    public void refresh() throws IOException {
        this.fill();
    }

    /**
     * Adds new stopwords to the stopwords list.
     * @param stopwords The new stopwords.
     */
    public void addStopwords(ArrayList<String> stopwords) {
        for (String stopword : stopwords) {
            if (!this.stopwords.contains(stopword.toLowerCase())) {
                this.stopwords.add(stopword.toLowerCase());
            }
        }
        Collections.sort(this.stopwords);
    }

    /**
     * Removes one stopword from the stopwords list.
     * @param stopword The stopword to be removed.
     */
    public void removeStopword(String stopword) {
        this.stopwords.remove(stopword);
    }

    /**
     * Checks if a word is a stopword.
     * @param word The word to be checked.
     * @return Returns true if the word is a stopword, false otherwise.
     */
    public boolean isStopword(String word) {
        return (Collections.binarySearch(this.stopwords, word) >= 0);
    }

    /**
     * Saves the stopwords list to a file.
     * @param filename The name of the file.
     * @throws java.io.IOException Throws an exception is something goes wrong.
     */
    public void save(String filename) throws IOException {
        BufferedWriter out = null;

        try {
            out = new BufferedWriter(new FileWriter(filename));

            //writing the stopwords list name
            out.write(name);
            out.write("\r\n");

            //writting the stopwords
            for (String stp : stopwords) {
                out.write(stp);
                out.write("\r\n");
            }

        } catch (IOException ex) {
            throw new IOException("Problems saving \"" + filename + "\" file!");
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    @Override
    public abstract Object clone() throws CloneNotSupportedException;

    @Override
    public String toString() {
        return this.name;
    }

    protected String name = "stopwords list";
    protected ArrayList<String> stopwords = new ArrayList<>();
    protected static final long serialVersionUID = 27L;
}
