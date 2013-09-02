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

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class SparseVector implements Serializable, Cloneable {

    /**
     * Creates a new instance of SparseVector
     *
     * @param vector The dense vector
     */
    public SparseVector(double[] vector) {
        this(vector, -1, 0.0f);
    }

    public SparseVector(double[] vector, int id) {
        this(vector, id, 0.0f);
    }

    public SparseVector(double[] vector, float klass) {
        this(vector, -1, klass);
    }

    public SparseVector mean(SparseVector vector2) {
        HashSet<Integer> mean_index = new HashSet<>(this.index.length);
        for (int i : this.index) {
            mean_index.add(i);
        }
        for (int i : vector2.index) {
            mean_index.add(i);
        }
        int index_vector;
        double[] newVector = new double[this.size];
        Arrays.fill(newVector, 0.0f);
        Iterator<Integer> iterator = mean_index.iterator();
        while (iterator.hasNext()) {
            index_vector = iterator.next();
            newVector[index_vector] = (this.getValue(index_vector) + vector2.getValue(index_vector)) / 2;
        }
        return new SparseVector(newVector);
    }

    public SparseVector(double[] vector, int id, float klass) {
        assert (vector != null) : "ERROR: vector can not be null!";

        this.id = id;
        this.klass = klass;
        this.size = vector.length;

        TIntArrayList index_aux = new TIntArrayList();
        TDoubleArrayList values_aux = new TDoubleArrayList();

        for (int i = 0; i < vector.length; i++) {
            if (vector[i] > 0.0f) {
                index_aux.add(i);
                values_aux.add(vector[i]);
            }
        }

        this.index = new int[index_aux.size()];
        this.values = new double[values_aux.size()];

        int length = this.index.length;
        for (int i = 0; i < length; i++) {
            this.index[i] = index_aux.get(i);
            this.values[i] = values_aux.get(i);
            this.norm += this.values[i] * this.values[i];
        }

        this.norm = Math.sqrt(this.norm);
    }

    public void addValue(int[] index, double[] values) {
    }

    public double dot(SparseVector svector) {
        assert (this.size == svector.size) : "ERROR: vectors of different sizes!";

        double innerprod = 0.0d;

        int length = this.index.length;
        int vlength = svector.index.length;
        int[] vindex = svector.index;
        double[] vvalues = svector.values;

        if (length > 0 && index[0] <= vindex[vlength - 1]) {
            for (int i = 0, j = 0; i < length; i++) {
                while (j + 1 <= vlength && vindex[j] < this.index[i]) {
                    j++;
                }

                if (j >= vlength) {
                    break;
                } else if (this.index[i] == vindex[j]) {
                    innerprod += this.values[i] * vvalues[j];
                    j++;
                }
            }
        }

        return innerprod;
    }

    public double sum() {
        double sum = 0;
        for (int i = 0; i < values.length; i++) {
            sum += values[i];
        }
        return sum;
    }

    public double norm() {
        return this.norm;
    }

    public void normalize() {
        assert (this.norm != 0.0f) : "ERROR: it is not possible to normalize a null vector!";

        if (this.norm > DELTA) {
            int length = this.values.length;
            double aux = 0.0d;
            for (int i = 0; i < length; i++) {
                values[i] = values[i] / this.norm;
                aux += values[i] * values[i];
            }
            this.norm = aux;
        } else {
            this.norm = 0.0f;
        }
    }

    public int size() {
        return this.size;
    }

    public double sparsity() {
        if (this.size > 0) {
            return 1.0f - (this.index.length / this.size);
        } else {
            return 1.0f;
        }
    }

    public int[] getIndex() {
        return index;
    }

    public double[] getValues() {
        return values;
    }

    public void setValue(int index, double value) {
        for (int i = 0; i < this.index.length; i++) {
            if (this.index[i] == index) {
                this.values[i] = value;
            } else if (this.index[i] > index) {
                break;
            }
        }
    }

    public double getValue(int index) {
        for (int i = 0; i < this.index.length; i++) {
            if (this.index[i] == index) {
                return this.values[i];
            } else if (this.index[i] > index) {
                return 0.0d;
            }
        }
        return 0.0d;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getKlass() {
        return klass;
    }

    public void setKlass(float klass) {
        this.klass = klass;
    }

    public double[] toDenseVector() {
        double[] vector = new double[this.size];
        Arrays.fill(vector, 0.0d);

        int length = this.index.length;
        for (int i = 0; i < length; i++) {
            vector[this.index[i]] = this.values[i];
        }

        return vector;
    }

    @Override
    public Object clone() {
        SparseVector clone = new SparseVector(new double[]{0});
        clone.norm = this.norm;
        clone.size = this.size;
        clone.id = this.id;
        clone.klass = this.klass;

        clone.index = new int[this.index.length];
        System.arraycopy(this.index, 0, clone.index, 0, this.index.length);

        clone.values = new double[this.values.length];
        System.arraycopy(this.values, 0, clone.values, 0, this.values.length);

        return clone;
    }
    private static final float DELTA = 0.00001f;
    private double norm = 0.0d;
    private int size = 0;
    private int[] index;
    private double[] values;
    private int id;
    private String title;
    private float klass;
    private static final long serialVersionUID = 27L;

    void write(BufferedWriter out) throws IOException {
        out.write(String.valueOf(this.id));
        out.write(";");

        for (int i = 0; i < this.values.length; i++) {
            out.write(Integer.toString(this.index[i]));
            out.write(":");
            out.write(Double.toString(this.values[i]));
            out.write(";");
        }

        out.write(Float.toString(this.klass));
    }
}
