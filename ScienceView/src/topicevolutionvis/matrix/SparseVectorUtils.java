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

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class SparseVectorUtils {

    public static SparseVector mean(ArrayList<SparseVector> vectors) {
        assert (!vectors.isEmpty()) : "More than zero vectors must be used!";

        double[] mean = new double[vectors.get(0).size()];
        Arrays.fill(mean, 0.0f);

        int size = vectors.size();
        for (int i = 0; i < vectors.size(); i++) {
            assert (vectors.get(0).size() == vectors.get(i).size()) : "Vectors of diferent sizes!";

            int[] index = vectors.get(i).getIndex();
            double[] values = vectors.get(i).getValues();

            int length = index.length;
            for (int j = 0; j < length; j++) {
                mean[index[j]] += values[j];
            }
        }

        int length = mean.length;
        for (int j = 0; j < length; j++) {
            mean[j] = mean[j] / size;
        }

        return new SparseVector(mean);
    }
}
