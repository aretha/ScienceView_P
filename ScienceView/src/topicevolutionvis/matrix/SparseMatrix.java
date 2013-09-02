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
package topicevolutionvis.matrix;

import gnu.trove.map.hash.TIntIntHashMap;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class SparseMatrix implements Serializable, Cloneable {

    private static final long serialVersionUID = 27L;
    int ndimensions = 0;
    private ArrayList<String> attributes = new ArrayList<>();
    private ArrayList<SparseVector> rows;
    private TIntIntHashMap id_to_index;

    public SparseMatrix() {
        rows = new ArrayList<>();
        id_to_index = new TIntIntHashMap();
    }

    public SparseMatrix(int initialCapacity) {
        rows = new ArrayList<>(initialCapacity);
        id_to_index = new TIntIntHashMap(initialCapacity);
    }

    public int[] getIds() {
        int[] ids = this.id_to_index.keys();
        Arrays.sort(ids);
        return ids;
    }

    public SparseMatrix(String filename) throws IOException {
        FileInputStream fis = null;
        ObjectInputStream ois = null;

        try {
            fis = new FileInputStream(new File(filename));
            ois = new ObjectInputStream(fis);
            SparseMatrix aux = (SparseMatrix) ois.readObject();
            this.rows = aux.rows;
        } catch (IOException | ClassNotFoundException e) {
            throw new IOException("Problems reading \"" + filename + "\"!");
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void setRow(int index, SparseVector vector) {
        assert (rows.size() > index && this.ndimensions == vector.size()) :
                "ERROR: wrong index or vector of wrong size!";

        this.rows.set(index, vector);
    }

    public void save(String filename) throws IOException {
        BufferedWriter out = null;

        try {
            out = new BufferedWriter(new FileWriter(filename));

            //Writting the file header
            out.write("SY\r\n");
            out.write(Integer.toString(this.getRowsCount()));
            out.write("\r\n");
            out.write(Integer.toString(this.getDimensions()));
            out.write("\r\n");

            //Writting the attributes
            if (attributes != null) {
                for (int i = 0; i < attributes.size(); i++) {
                    out.write(attributes.get(i).replaceAll("<>", " ").trim());

                    if (i < attributes.size() - 1) {
                        out.write(";");
                    }
                }
                out.write("\r\n");
            } else {
                out.write("\r\n");
            }

            //writting the vectors
            for (int i = 0; i < this.getRowsCount(); i++) {
                this.rows.get(i).write(out);
                out.write("\r\n");
            }

        } catch (IOException ex) {
            throw new IOException("Problems written \"" + filename + "\"!");
        } finally {
            //close the file
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException ex) {
                    Logger.getLogger(SparseMatrix.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void addRow(SparseVector vector) {
        assert (rows.isEmpty() || this.rows.get(0).size() == vector.size()) :
                "ERROR: vector of wrong size!";
        this.rows.add(vector);
        int id = vector.getId();
        if (id != -1) {
            this.id_to_index.put(id, rows.size() - 1);
        }
    }

    public void addRow(double[] vector, int id) {
        SparseVector v = new SparseVector(vector, id, 0.0f);
        this.addRow(v);
        if (id != -1) {
            this.id_to_index.put(id, rows.size() - 1);
        }
    }

    public int getRowsCount() {
        return this.rows.size();
    }

    public double getValueWithId(int id, int column) {
        int index = this.id_to_index.get(id);
        assert (index != id_to_index.getNoEntryValue()) : "ERROR: this row does not exists in the matrix!";
        return this.rows.get(index).getValue(column);
    }

    public int getIndexWithId(int id) {
        int index = this.id_to_index.get(id);
        assert (index != id_to_index.getNoEntryValue()) : "ERROR: this row does not exists in the matrix!";
        return index;
    }

    public double getValue(int row, int column) {
        assert (rows.size() > row) : "ERROR: this row does not exists in the matrix!";
        return this.rows.get(row).getValue(column);
    }

    public SparseVector getRowWithIndex(int row) {
        assert (rows.size() > row) : "ERROR: this row does not exists in the matrix!";
        return this.rows.get(row);
    }

    public SparseVector getRowWithId(int id) {
        int index = this.id_to_index.get(id);
        assert (index != id_to_index.getNoEntryValue()) : "ERROR: this row does not exists in the matrix!";
        return this.rows.get(index);
    }

    public void normalize() {
        int size = this.rows.size();
        for (int i = 0; i < size; i++) {
            this.rows.get(i).normalize();
        }
    }

    public double[][] toDenseMatrix() {
        double[][] matrix = new double[this.rows.size()][];

        int size = this.rows.size();
        for (int i = 0; i < size; i++) {
            matrix[i] = this.rows.get(i).toDenseVector();
        }
        return matrix;
    }

    public ArrayList<String> getAttributes() {
        return this.attributes;
    }
    
    public String getAttributeWithIndex(int index){
        return this.attributes.get(index);
    }

    public void setAttributes(ArrayList<String> attributes) {
        assert (rows.isEmpty() || this.rows.get(0).size() == attributes.size()) :
                "ERROR: attributes and vectors of different sizes!";

        this.attributes = attributes;
        this.ndimensions = this.attributes.size();
    }

    public int getDimensions() {
        return ndimensions;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        SparseMatrix clone = new SparseMatrix();
        clone.ndimensions = this.ndimensions;

        for (String attr : this.attributes) {
            clone.attributes.add(attr);
        }

        for (SparseVector v : this.rows) {
            clone.rows.add((SparseVector) v.clone());
        }

        return clone;
    }

    public void setDimensions(int ndimensions) {
        this.ndimensions = ndimensions;
    }
}
