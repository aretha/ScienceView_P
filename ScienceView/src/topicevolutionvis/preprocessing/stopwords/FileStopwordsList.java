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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a stopwords list from a file.
 * 
 * @author Fernando Vieira Paulovich
 */
public class FileStopwordsList extends StopwordsList implements Cloneable{

    /** 
     * Creates a new instance of FileStopwordsList
     * @param filename The name of the stopwords list file
     * @throws java.io.IOException Throws an exception if something goes wrong.
     */
    public FileStopwordsList(String filename) throws IOException {
        this.filename = filename;
        this.fill();
    }

    @Override
    public void fill() throws IOException {
        BufferedReader in = null;

        //clear the old list
        this.stopwords.clear();

        try {
            in = new BufferedReader(new FileReader(filename));
            String line; //armazena a linha lida

            //the first line is the stopwords list name
            while ((line = in.readLine()) != null) {
                if (line.trim().length() > 0) {
                    this.name = line.trim();
                    break;
                }
            }

            //the remaining words are the stopwords
            while ((line = in.readLine()) != null) {
                if (line.trim().length() > 0) {
                    this.stopwords.add(line.trim().toLowerCase());
                }
            }

        } catch (FileNotFoundException e) {
            throw new IOException("File \"" + filename + "\" was not found!");
        } catch (IOException e) {
            throw new IOException("Problems reading the file \"" + filename + "\".");
        } finally {
            //fechar o arquivo
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new IOException("Problems closing the file \"" + filename + "\".");
                }
            }
        }

        Collections.sort(this.stopwords);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        try {
            StopwordsList clone = new FileStopwordsList(this.filename);
            
            clone.name = this.name;
            clone.stopwords = (ArrayList<String>) this.stopwords.clone();

            return clone;
        } catch (IOException ex) {
            Logger.getLogger(FileStopwordsList.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    private String filename;
}
