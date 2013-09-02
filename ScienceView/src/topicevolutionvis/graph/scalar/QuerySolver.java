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
 * of the original code is Roberto Pinho <robertopinho@yahoo.com.br>.
 *
 * Contributor(s): Fernando Vieira Paulovich <fpaulovich@gmail.com>
 *
 * You should have received a copy of the GNU General Public License along 
 * with PEx. If not, see <http://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ***** */
package topicevolutionvis.graph.scalar;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import topicevolutionvis.graph.Scalar;
import topicevolutionvis.graph.TemporalGraph;
import topicevolutionvis.graph.Vertex;
import topicevolutionvis.preprocessing.Ngram;
import topicevolutionvis.projection.temporal.TemporalProjection;

/**
 *
 * @author rpinho, Fernando Vieira Paulovich
 */
public class QuerySolver {

    /**
     * Creates a new instance of QuerySolver
     */
    public QuerySolver(TemporalProjection tproj) {
        this.tproj = tproj;
    }

    public boolean isoperand(String symb) {
        symb = symb.replaceAll(" ", "");
        if (!symb.equals("and") && !symb.equals("or") && !symb.equals("(") && !symb.equals(")")) {
            return true;
        }
        return false;
    }

    public boolean prdc(String one, String two) {
        if (one.equals("(")) {
            return false;
        }
        if (!one.equals(")") && two.equals("(")) {
            return false;
        }
        if (!one.equals("(") && two.equals(")")) {
            return true;
        }
        if (one.equals("and") && two.equals("or")) {
            return true;
        }
        if (one.equals("or") && two.equals("and")) {
            return false;
        }
        if (one.equals(two)) {
            return true;
        }
        return true;
    }

    public static String replaceCharAt(String s, int pos, char c) {
        return s.substring(0, pos) + c + s.substring(pos + 1);
    }

    public String postfix(String query) {

        boolean und;
        String topsymb = "or";
        StringBuilder postr = new StringBuilder();

        Stack<String> opstk = new Stack<>();

        query = query.trim();

        query = query.replaceAll("\\(", " \\( ");
        query = query.replaceAll("\\)", " \\) ");


        boolean inSet = false;
        for (int i = 0; i < query.length(); i++) {
            if (query.charAt(i) == '"') {
                inSet = !inSet;
            } else if (!inSet && query.charAt(i) == ' ') {
                query = replaceCharAt(query, i, splitSymbolChar);
            }
        }

        String[] symbs = query.split(splitSymbol);


        for (int i = 0; i < symbs.length; i++) {
            symbs[i] = symbs[i].replaceAll("\"", "");
            if (isoperand(symbs[i])) {
                postr.append(symbs[i]).append(splitSymbol);

            } else {
                if (!opstk.empty()) {
                    und = false;
                    topsymb = opstk.pop();
                } else {
                    und = true;
                }
                while (!und && prdc(topsymb, symbs[i])) {
                    postr.append(topsymb).append(splitSymbol);
                    if (!opstk.empty()) {
                        und = false;
                        topsymb = opstk.pop();
                    } else {
                        und = true;
                    }
                }
                if (!und) {
                    opstk.push(topsymb);
                }
                if (und || !symbs[i].equals(")")) {
                    opstk.push(symbs[i]);
                } else {
                    topsymb = opstk.pop();
                }
            }
        }
        while (!opstk.empty()) {
            postr.append(opstk.pop()).append(splitSymbol);
        }
        return postr.toString();
    }

    protected double[] oper(String opr, double[] cdata1, double[] cdata2, int size) {
        double[] cdata = new double[size];
        for (int i = 0; i < cdata1.length; i++) {
            cdata[i] = (cdata1[i] + cdata2[i]) / 2.0;
            if ((opr.equals("and")) && cdata1[i] == 0 || cdata2[i] == 0) {
                cdata[i] = 0;
            }
        }
        return cdata;
    }

    double[] value(String word, TIntObjectHashMap< Vertex> vertex) throws java.io.IOException {
        double[] cdata = new double[vertex.size()];
        Arrays.fill(cdata, 0.0f);

        //Getting the Cdata
        int i = 0;
        TIntObjectIterator<Vertex> iterator = vertex.iterator();
        while (iterator.hasNext()) {
            iterator.advance();
            Vertex v = iterator.value();
            cdata[i] = this.getWordFrequency(word, v.getId());
            i++;
        }

        return cdata;
    }

    double[] eval(String postFix, TIntObjectHashMap< Vertex> vertex) throws java.io.IOException {
        double[] opnd1, opnd2;
        double[] value;

        Stack<double[]> opndstk = new Stack<>();

        String[] symbs = postFix.split(splitSymbol);

        for (int i = 0; i < symbs.length; i++) {
            if (symbs[i].equals("")) {
                continue;
            }
            if (isoperand(symbs[i])) {
                opndstk.push(value(symbs[i], vertex));
            } else {
                opnd2 = opndstk.pop();
                opnd1 = opndstk.pop();
                value = oper(symbs[i], opnd1, opnd2, vertex.size());
                opndstk.push(value);

            }
        }
        return opndstk.pop();

    }

    public void createCdata(Scalar scalar, TIntIntHashMap search_result, int ndocuments) throws java.io.IOException {
        this.entry = false;
        for (ArrayList<TemporalGraph> intermediateProjections : tproj.getGraphs().values()) {
            for (TemporalGraph graph : intermediateProjections) {
                if (graph.getVertex().size() > 0) {
                    //Getting the Cdata
                    double max = 0.0, min = Double.MAX_VALUE;
                    TIntIntIterator it = search_result.iterator();
                    while (it.hasNext()) {
                        it.advance();
                        if (it.value() > max) {
                            max = it.value();
                        } else if (it.value() < min) {
                            min = it.value();
                        }
                    }
                    it = search_result.iterator();
                    while (it.hasNext()) {
                        it.advance();
                        double aux = it.value();
                        if (max > min) {
                            aux = (aux - min) / (max - min);
                        }

                        Vertex v = graph.getVertexById(it.key());
                        if (v != null) {
                            v.setScalar(scalar, aux);
                        }
                    }
                    TIntObjectIterator<Vertex> it2 = graph.getVertex().iterator();
                    while (it2.hasNext()) {
                        it2.advance();
                        if (!search_result.containsKey(it2.key())) {
                            graph.getVertexById(it2.key()).setScalar(scalar, 0.0d);
                        }
                    }
                }
            }
        }
    }

    private double getWordFrequency(String word, Integer filename) throws IOException {
        return getWordFrequencyFromFile(word, filename);
    }

    protected double getWordFrequencyFromFile(String word, Integer filename) throws java.io.IOException {
        double frequency = 0.0f;

        ArrayList<Ngram> ngrams = this.tproj.getDatabaseCorpus().getNgrams(filename);
        for (Ngram n : ngrams) {
            if (n.ngram.indexOf(word.toLowerCase()) != -1) {
                frequency += n.frequency;
            }
        }

        return frequency;
    }
    protected TemporalProjection tproj;
    private boolean entry = false;
    public static final String splitSymbol = "&";
    public static final char splitSymbolChar = '&';
}
