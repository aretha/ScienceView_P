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
 * of the original code is Fernando Vieira Paulovich <fpaulovich@gmail.com>,
 * Roberto Pinho <robertopinho@yahoo.com.br>.
 *
 * Contributor(s): Rosane Minghim <rminghim@icmc.usp.br>
 *
 * You should have received a copy of the GNU General Public License along
 * with PEx. If not, see <http://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ***** */
package topicevolutionvis.util;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.THashMap;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import topicevolutionvis.graph.Vertex;
import topicevolutionvis.topic.Topic;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class Utils {

    /*
     * Get the extension of a file.
     */
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

//    public static String getIpAddress() {
//        try {
//            InetAddress thisIp = InetAddress.getLocalHost();
//            return thisIp.getHostAddress();
//        } catch (UnknownHostException ex) {
//            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
//
//    public static void detectLanguage() {
//        try {
//            URL url = new URL("https://ajax.googleapis.com/ajax/services/language/detect?" + "v=1.0&q=Hola,%20mi%20amigo!&key=ABQIAAAAwEpr46RbTZ3lmRJ7gIMhBhTNu7K0IgEz3SQZlRnk-9cwJGEd0BRI_7UAHOvebCz9vYqX4ZoG0M_AJw&userip=" + getIpAddress());
//            URLConnection connection = url.openConnection();
//            connection.addRequestProperty("Referer", "http://www.ironiacorp.com");
//
//            String line;
//            StringBuilder builder = new StringBuilder();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            while ((line = reader.readLine()) != null) {
//                builder.append(line);
//            }
//
//          //  JSONObject json = new JSONObject(builder.toString());
//
//
//        } catch (Exception ex) {
//            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }
    public static double[] dispersion(double[][] projection) {
        double[] values_x = new double[projection.length],
                values_y = new double[projection.length],
                result = new double[2];
        for (int i = 0; i < values_x.length; i++) {
            values_x[i] = projection[i][0];
            values_y[i] = projection[i][1];
        }
        StandardDeviation std = new StandardDeviation();
        result[0] = std.evaluate(values_x);
        result[1] = std.evaluate(values_y);
        return result;
    }

    public static double[] dispersion(Collection<Vertex> vertex) {
        Iterator<Vertex> it = vertex.iterator();
        double[] values_x = new double[vertex.size()],
                values_y = new double[vertex.size()],
                result = new double[2];
        for (int i = 0; i < values_x.length; i++) {
            Vertex v = it.next();
            values_x[i] = v.getX();
            values_y[i] = v.getY();
        }

        StandardDeviation std = new StandardDeviation();
        result[0] = std.evaluate(values_x);
        result[1] = std.evaluate(values_y);
        return result;
    }

    public static int indexOf(int[] array, int value) {
        return Arrays.binarySearch(array, value);
    }

    public static TIntArrayList toArrayList(int[] array) {
        TIntArrayList aux = new TIntArrayList(array.length);
        for (int i = 0; i < array.length; i++) {
            aux.add(array[i]);
        }
        return aux;
    }


    public static TIntArrayList unionValues(THashMap<Topic, TIntArrayList> entry) {
        TIntArrayList aux = new TIntArrayList();
        for (TIntArrayList t : entry.values()) {
            aux.addAll(t);
        }
        return aux;
    }
    /**
     * Import a graph connectivity from a file. The first line is the
     * connectivity's name, and the remaining are the edges. Each line
     * represents an edge and it is composed by tree fields, separated by a
     * semicoloumn. The first two are the urls of the vertex linked by the edge
     * and the third is the edge's lentgh.
     *
     * @param filename The fie name.
     * @param vertex The vertex which composed this connectivity.
     * @return The read connectivity.
     * @throws IOException
     */
//    public static Connectivity importConnectivity(HashMap<Integer,Vertex> vertex,
//            String filename) throws IOException {
//        Connectivity con = null;
//
//        //creating an index
//        HashMap<Integer, Integer> index = new HashMap<>();
//        for (Vertex v : vertex.values()) {
//            index.put(v.getId(), (int) v.getId());
//        }
//
//        //creating the neighborhood
//        ArrayList<ArrayList<Pair>> neigh_aux = new ArrayList<>();
//
//        BufferedReader in = null;
//        try {
//            in = new BufferedReader(new java.io.FileReader(filename));
//
//            //first line is the connectivity title
//            String line = null;
//            while ((line = in.readLine()) != null) {
//                //ignore comments
//                if (line.trim().length() > 0 && line.lastIndexOf('#') == -1) {
//                    break;
//                }
//            }
//
//            con = new Connectivity(line.trim(), false, false);
//
//            for (int i = 0; i < index.size(); i++) {
//                neigh_aux.add(new ArrayList<Pair>());
//            }
//
//            //the remaining lines are the edges
//            while ((line = in.readLine()) != null) {
//                //ignore comments
//                if (line.trim().length() > 0 && line.lastIndexOf('#') == -1) {
//                    StringTokenizer t = new StringTokenizer(line, ";");
//
//                    Integer from = index.get(t.nextToken());
//                    Integer to = index.get(t.nextToken());
//                    float len = Float.parseFloat(t.nextToken());
//
//                    if (from != null && to != null) {
//                        neigh_aux.get(from).add(new Pair(to, len));
//                        neigh_aux.get(to).add(new Pair(from, len));
//                    }
//                }
//            }
//
//            Pair[][] neighborhood = new Pair[index.size()][];
//
//            for (int i = 0; i < neigh_aux.size(); i++) {
//                neighborhood[i] = new Pair[neigh_aux.get(i).size()];
//
//                for (int j = 0; j < neigh_aux.get(i).size(); j++) {
//                    neighborhood[i][j] = neigh_aux.get(i).get(j);
//                }
//            }
//
//            //creating the connectivity with the read neighborhood
//            con.create(vertex, neighborhood);
//
//        } catch (IOException e) {
//            throw new IOException(e.getMessage());
//        } finally {
//            if (in != null) {
//                try {
//                    in.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//
//        return con;
//    }
    /**
     * Export a graph connectivity to a file.
     *
     * @param connectivity The connectivity to be exported.
     * @param filename The file name.
     * @throws IOException
     */
//    public static void exportConnectivity(Connectivity connectivity,
//            String filename) throws IOException {
//
//        BufferedWriter out = null;
//        try {
//            out = new BufferedWriter(new FileWriter(filename));
//
//            //Writting the connectivity name
//            out.write(connectivity.getName());
//            out.write("\r\n");
//
//            for (Edge e : connectivity.getEdges()) {
//                out.write(e.getSource().getId());
//                out.write(";");
//                out.write(e.getTarget().getId());
//                out.write(";");
//                out.write(Float.toString(e.getWeight()));
//                out.write("\r\n");
//            }
//        } catch (IOException e) {
//            throw new IOException(e.getMessage());
//        } finally {
//            if (out != null) {
//                try {
//                    out.flush();
//                    out.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//    }
    //cdata;year
    //filename1.txt;1.3;0.70
    //filename2.txt;4.0;0.06
    //filename3.txt;6.7;0.40
    //filename4.txt;3.0;0.12
    //filename5.txt;8.9;0.11
//    public static void importScalars(Graph graph, String filename) throws IOException {
//        BufferedReader in = null;
//
//        try {
//            in = new BufferedReader(new java.io.FileReader(filename));
//            ArrayList<String> scalars = new ArrayList<String>();
//
//            //Capturing the scalar names
//            int linenumber = 0;
//            String line = null;
//            while ((line = in.readLine()) != null) {
//                linenumber++;
//
//                //ignore comments
//                if (line.trim().length() > 0 && line.lastIndexOf('#') == -1) {
//                    StringTokenizer t = new StringTokenizer(line, ";");
//
//                    while (t.hasMoreTokens()) {
//                        scalars.add(t.nextToken().trim());
//                    }
//
//                    break;
//                }
//            }
//
//            //index for the vertex
//            HashMap<Integer, Vertex> index = new HashMap<Integer, Vertex>();
//            for (Vertex v : graph.getVertex()) {
//                index.put(v.getId(), v);
//            }
//
//            //reading the scalars
//            while ((line = in.readLine()) != null) {
//                linenumber++;
//                ArrayList<Float> values = new ArrayList<Float>();
//
//                //ignore comments
//                if (line.trim().length() > 0 && line.lastIndexOf('#') == -1) {
//                    StringTokenizer t = new StringTokenizer(line, ";", false);
//
//                    //Capturing the filename
//                    String fname = t.nextToken().trim();
//
//                    //Capturing the scalar values
//                    while (t.hasMoreTokens()) {
//                        float value = Float.parseFloat(t.nextToken().trim());
//                        values.add(value);
//                    }
//
//                    //checking the data
//                    if (scalars.size() != values.size()) {
//                        throw new IOException("The number of values for one scalar "
//                                + "does not match with the number of declared scalars.\r\n"
//                                + "Check line " + linenumber + " of the file.");
//                    }
//
//                    //Adding the scalar values to the vertex
//                    Vertex v = index.get(fname);
//
//                    if (v != null) {
//                        for (int i = 0; i < scalars.size(); i++) {
//                            Scalar s = graph.addVertexScalar(scalars.get(i));
//                            v.setScalar(s, values.get(i));
//                        }
//                    }
//                }
//            }
//        } catch (FileNotFoundException ex) {
//            throw new IOException(ex.getMessage());
//        } finally {
//            if (in != null) {
//                try {
//                    in.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//    }
//    public static void exportScalars(Graph graph, String filename) throws IOException {
//        BufferedWriter out = null;
//        try {
//            out = new BufferedWriter(new FileWriter(filename));
//
//            //writing the scalar names
//            for (int i = 0; i < graph.getVertexScalars().size(); i++) {
//                if (!graph.getVertexScalars().get(i).getName().equals(PExConstants.DOTS)) {
//                    out.write(graph.getVertexScalars().get(i).getName().replaceAll(";", "_"));
//                    if (i < graph.getVertexScalars().size() - 1) {
//                        out.write(";");
//                    }
//                }
//            }
//
//            out.write("\r\n");
//
//            //writing the scalar values
//            for (Vertex v : graph.getVertex()) {
//                if (v.isValid()) {
//                    out.write(v.getId() + ";");
//
//                    for (int i = 0; i < graph.getVertexScalars().size(); i++) {
//                        if (!graph.getVertexScalars().get(i).getName().equals(PExConstants.DOTS)) {
//                            float scalar = v.getScalar(graph.getVertexScalars().get(i),false);
//                            out.write(Float.toString(scalar).replaceAll(";", "_"));
//                            if (i < graph.getVertexScalars().size() - 1) {
//                                out.write(";");
//                            }
//                        }
//                    }
//
//                    out.write("\r\n");
//                }
//            }
//
//        } catch (IOException e) {
//            throw new IOException(e.getMessage());
//        } finally {
//            if (out != null) {
//                try {
//                    out.flush();
//                    out.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//    }
//    public static Graph importProjection(String filename) throws IOException {
//        Graph graph = new Graph();
//
//        Matrix matrix = MatrixFactory.getInstance(filename);
//
//        Scalar sdots = graph.addVertexScalar(PExConstants.DOTS);
//        Scalar scdata = graph.addVertexScalar(PExConstants.CDATA);
//
////        int index = graph.addTitle("file name");
//
//        ArrayList<Vertex> vertex = graph.getVertex();
//
//        for (int id = 0; id < matrix.getRowCount(); id++) {
//            Vector vec = matrix.getRow(id);
//
//            Vertex v = new Vertex(id, vec.getValue(0), vec.getValue(1));
//            v.setScalar(sdots, 0.0f);
//            v.setScalar(scdata, vec.getKlass());
//            v.setUrl(vec.getId());
//
//            //  v.setTitle(index, vec.getId());
//            vertex.add(v);
//        }
//
//        graph.setVertex(vertex);
//
//        Connectivity dotsCon = new Connectivity(PExConstants.DOTS);
//        graph.addConnectivity(dotsCon);
//
//        graph.getProjectionData().setSourceFile(filename);
//        graph.getProjectionData().setNumberObjects(vertex.size());
//
//        return graph;
//    }
//    public static Matrix exportProjection(Graph graph, Scalar scalar) throws IOException {
//        Matrix matrix = new DenseMatrix();
//
//        if (scalar == null) {
//            scalar = graph.getVertexScalarByName(PExConstants.DOTS);
//        }
//
//        for (int i = 0; i < graph.getVertex().size(); i++) {
//            float[] point = new float[2];
//            point[0] = graph.getVertex().get(i).getX();
//            point[1] = graph.getVertex().get(i).getY();
//
//            float cdata = graph.getVertex().get(i).getScalar(scalar);
//            Integer id = graph.getVertex().get(i).getUrl();
//
//            matrix.addRow(new DenseVector(point, id, cdata));
//        }
//
//        ArrayList<String> attributes = new ArrayList<String>();
//        attributes.add("x");
//        attributes.add("y");
//
//        matrix.setAttributes(attributes);
//
//        return matrix;
//    }
//    public static void exportVTKFile(Graph graph, String filename,
//            Connectivity connectivity) throws IOException {
//        BufferedWriter out = null;
//
//        //saving the VTK file
//        try {
//            out = new BufferedWriter(new FileWriter(filename));
//
//            //Writting the file header
//            out.write("# vtk DataFile Version 2.0\r\n");
//            out.write("output\r\n");
//            out.write("ASCII\r\n");
//            out.write("DATASET POLYDATA\r\n");
//            out.write("POINTS ");
//            out.write(Integer.toString(graph.getVertex().size()));
//            out.write(" float\r\n");
//
//            //Writting the points coordinates
//            for (Vertex v : graph.getVertex()) {
//                Scalar s = graph.getVertexScalarByName(PExConstants.CDATA);
//
//                out.write(Float.toString(v.getX()));
//                out.write(" ");
//                out.write(Float.toString(v.getY()));
//                out.write(" ");
//                out.write(Float.toString(v.getScalar(s,false)));
//                out.write("\r\n");
//            }
//
//            ArrayList<Edge> edges = connectivity.getEdges();
//
//            //Writting the Delaunay triangulation
//            int numberEdges = edges.size();
//            out.write("\r\nLINES ");
//            out.write(Integer.toString(numberEdges));
//            out.write(" ");
//            out.write(Integer.toString(numberEdges * 3));
//            out.write("\r\n");
//            for (Edge e : edges) {
//                out.write("2 ");
//                out.write(Long.toString(e.getSource().getId()));
//                out.write(" ");
//                out.write(Long.toString(e.getTarget().getId()));
//                out.write("\r\n");
//            }
//
//            out.write("\r\nPOINT_DATA ");
//            out.write(Integer.toString(graph.getVertex().size()));
//            out.write("\r\n");
//
//            //Writting the scalar values
//            for (Scalar s : graph.getVertexScalars()) {
//                out.write("SCALARS ");
//                out.write(s.getName().replace("\'", " ").trim().replace(" ", "+"));
//                out.write(" float\r\n");
//                out.write("LOOKUP_TABLE default\r\n");
//
//                for (Vertex v : graph.getVertex()) {
//                    out.write(Float.toString(v.getScalar(s, false)));
//                    out.write("\r\n");
//                }
//                out.write("\r\n");
//            }
//        } catch (FileNotFoundException e) {
//            throw new IOException("File \"" + filename + "\" was not found!");
//        } catch (IOException e) {
//            throw new IOException("Problems reading the file \"" + filename + "\"");
//        } finally {
//            if (out != null) {
//                try {
//                    out.flush();
//                    out.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//
//        //saving the names file
//        try {
//            out = new BufferedWriter(new FileWriter(filename + ".names"));
//            for (Vertex v : graph.getVertex()) {
//                String title = v.toString();
//                if (title.length() > 125) {
//                    title = title.substring(0, 124);
//                }
//                out.write(title);
//                out.write("\r\n");
//            }
//        } catch (FileNotFoundException e) {
//            throw new IOException("File \"" + filename + ".names" + "\" was not found!");
//        } catch (IOException e) {
//            throw new IOException("Problems reading the file \"" + filename + ".names" + "\"");
//        } finally {
//            if (out != null) {
//                try {
//                    out.flush();
//                    out.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//    }
//    public static void exportTitles(TemporalGraph graph, String filename) throws IOException {
//        BufferedWriter out = null;
//
//        try {
//            out = new BufferedWriter(new FileWriter(filename));
//
//            //Writting the names of the titles
//            for (int i = 0; i < graph.getTitles().size() - 1; i++) {
//                out.write(graph.getTitles().get(i).replaceAll(";", "_"));
//                out.write(";");
//            }
//
//            out.write(graph.getTitles().get(graph.getTitles().size() - 1));
//            out.write("\r\n");
//
//            //getting the title indexes
//            int[] titleIndex = new int[graph.getTitles().size()];
//            for (int i = 0; i < titleIndex.length; i++) {
//                titleIndex[i] = graph.getTitleIndex(graph.getTitles().get(i));
//            }
//
//            //writing the titles
//            for (Vertex v : graph.getVertex().values()) {
//                if (v.isValid()) {
//                    out.write(v.getId());
//                    out.write(";");
//
//                    for (int i = 0; i < titleIndex.length - 1; i++) {
//                        v.changeTitle(titleIndex[i]);
//                        out.write(v.toString().replaceAll(";", "_"));
//                        out.write(";");
//                    }
//
//                    v.changeTitle(titleIndex[titleIndex.length - 1]);
//                    out.write(v.toString().replaceAll(";", "_"));
//                    out.write("\r\n");
//                }
//            }
//        } catch (FileNotFoundException ex) {
//            throw new IOException("File \"" + filename + "\" was not found!");
//        } catch (IOException ex) {
//            throw new IOException("Problems writing the file \"" + filename + "\"");
//        } finally {
//            if (out != null) {
//                try {
//                    out.flush();
//                    out.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//    }
//    public static double log(double value, int base) {
//        if (value != 0) {
//            return Math.log(value) / Math.log(base);
//        }
//        return 0.0;
//    }
//    public static void normalize(float[][] result) {
//        int lvdimensions = result[0].length;
//        int lvinstances = result.length;
//
//        //for normalization
//        float[] lvlowrange = new float[lvdimensions];
//        float[] lvhighrange = new float[lvdimensions];
//
//        //for each instance
//        for (int lvins = 0; lvins < lvinstances; lvins++) {
//            //for each attribute
//            for (int lvfield = 0; lvfield < lvdimensions; lvfield++) {
//                //if it is the first instance then assign the first value
//                if (lvins == 0) {
//                    lvlowrange[lvfield] = result[lvins][lvfield];
//                    lvhighrange[lvfield] = result[lvins][lvfield];
//                } //otherwise compare
//                else {
//                    lvlowrange[lvfield] = lvlowrange[lvfield] > result[lvins][lvfield] ? result[lvins][lvfield] : lvlowrange[lvfield];
//                    lvhighrange[lvfield] = lvhighrange[lvfield] < result[lvins][lvfield] ? result[lvins][lvfield] : lvhighrange[lvfield];
//                }
//            }
//        }
//
//        //for each instance
//        for (int lvins = 0; lvins < lvinstances; lvins++) {
//            //for each attribute
//            for (int lvfield = 0; lvfield < lvdimensions; lvfield++) {
//                if ((lvhighrange[lvfield] - lvlowrange[lvfield]) > 0.0) {
//                    result[lvins][lvfield] = (result[lvins][lvfield] - lvlowrange[lvfield])
//                            / (lvhighrange[lvfield] - lvlowrange[lvfield]);
//                } else {
//                    result[lvins][lvfield] = 0;
//                }
//            }
//        }
//    }
//    public static float[][] transpose(float[][] matrix) {
//        float[][] transpMatrix = new float[matrix[0].length][];
//
//        for (int i = 0; i < matrix[0].length; i++) {
//            transpMatrix[i] = new float[matrix.length];
//
//            for (int j = 0; j < matrix.length; j++) {
//                transpMatrix[i][j] = matrix[j][i];
//            }
//        }
//
//        return transpMatrix;
//    }
//
//    public static void savePrefuseGraph(Graph graph, String filename,
//            Connectivity connectivity) {
//        BufferedWriter out = null;
//
//        try {
//            out = new BufferedWriter(new FileWriter(filename));
//
//            //Writting the file header
//            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
//            out.write("<!--  An excerpt of an egocentric social network  -->\n");
//            out.write("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\">\n");
//            out.write("<graph edgedefault=\"undirected\">");
//
//            out.write("<!-- data schema -->\n");
//            out.write("<key id=\"label\" for=\"node\" attr.name=\"label\" attr.type=\"string\"/>\n");
//            out.write("<key id=\"cdata\" for=\"node\" attr.name=\"cdata\" attr.type=\"float\"/>\n");
//
//            out.write("<!-- nodes -->\n");
//
//            for (int i = 0; i
//                    < graph.getVertex().size(); i++) {
//                Vertex v = graph.getVertex().get(i);
//
//                out.write("<node id=\"");
//                out.write(Integer.toString(i));
//                out.write("\">\n");
//                out.write("<data key=\"label\">");
//
//                if (v.isValid()) {
//                    if (v.toString().length() <= 5) {
//                        out.write(v.toString());
//                    } else {
//                        out.write(v.toString().substring(0, 5));
//                    }
//
//                } else {
//                    out.write("_");
//                }
//
//                out.write("</data>\n");
//
//                out.write("<data key=\"cdata\">");
//
//                if (v.isValid()) {
//                    Scalar s = graph.getVertexScalarByName(PExConstants.CDATA);
//                    out.write(Float.toString(v.getScalar(s, false)));
//                } else {
//                    out.write("-1.0");
//                }
//
//                out.write("</data>\n");
//                out.write("</node>\n");
//            }
//
//            if (connectivity != null) {
//                for (Edge e : connectivity.getEdges()) {
//                    out.write("<edge source=\"" + Long.toString(e.getSource().getId())
//                            + "\" target=\"" + Long.toString(e.getTarget().getId()) + "\"></edge>\n");
//                }
//
//            }
//
//            out.write("</graph>\n");
//            out.write("</graphml>\n");
//
//        } catch (IOException ex) {
//            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            if (out != null) {
//                try {
//                    out.flush();
//                    out.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//    }
//    public static Legend readLegend(ColorTable table, String filename) throws IOException {
//        BufferedReader in = null;
//        Legend legend = null;
//
//        try {
//            in = new BufferedReader(new java.io.FileReader(filename));
//
//            //capturing the legend title
//            String title = in.readLine();
//
//            //creating the legend
//            legend = new Legend(table, title);
//
//            //capturing the legend items
//            String line = null;
//            while ((line = in.readLine()) != null) {
//                //ignore comments
//                if (line.trim().length() > 0 && line.lastIndexOf('#') == -1) {
//                    StringTokenizer t = new StringTokenizer(line, ";");
//
//                    String name = t.nextToken();
//                    float value = Float.parseFloat(t.nextToken());
//
//                    legend.addItem(name, value);
//                }
//            }
//        } catch (IOException ex) {
//            throw new IOException(ex.getMessage());
//        } finally {
//            if (in != null) {
//                try {
//                    in.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//
//        return legend;
//    }
}
