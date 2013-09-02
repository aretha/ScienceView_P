package topicevolutionvis.graph;

import java.io.IOException;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class XMLGraphWriter {

    public static void save(TemporalGraph graph, String description, String filename) throws IOException {
//        BufferedWriter out = null;
//
//        try {
//            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "ISO-8859-1"));
//
//            //writting the header
//            out.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n");
//            out.write("<graph description=\"");
//            out.write(description);
//            out.write("\">\r\n");
//
//            ////////////////////////////////////
//            //writting the projection data
//            out.write("<source-type value=\"");
//            out.write(graph.getProcessingData().getCorpusType());
//            out.write("\"/>\r\n");
//
//            out.write("<projection-technique value=\"");
//            out.write(graph.getProcessingData().getProjectionType().toString());
//            out.write("\"/>\r\n");
//
//            out.write("<distance-type value=\"");
//            out.write(graph.getProcessingData().getDissimilarityType().toString());
//            out.write("\"/>\r\n");
//
//            out.write("<source-file value=\"");
//            out.write(graph.getProcessingData().getSourceFile());
//            out.write("\"/>\n");
//
//            out.write("<number-iterations value=\"");
//            out.write(Integer.toString(graph.getProcessingData().getNumberIterations()));
//            out.write("\"/>\r\n");
//
//            out.write("<fraction-delta value=\"");
//            out.write(Float.toString(graph.getProcessingData().getFractionDelta()));
//            out.write("\"/>\r\n");
//
//            out.write("<projection-type value=\"");
//            out.write(graph.getProcessingData().getProjectorType().toString());
//            out.write("\"/>\r\n");
//
//            out.write("<luhn-lower-cut value=\"");
//            out.write(Integer.toString(graph.getProcessingData().getLunhLowerCut()));
//            out.write("\"/>\r\n");
//
//            out.write("<luhn-upper-cut value=\"");
//            out.write(Integer.toString(graph.getProcessingData().getLunhUpperCut()));
//            out.write("\"/>\r\n");
//
//            out.write("<number-grams value=\"");
//            out.write(Integer.toString(graph.getProcessingData().getNumberGrams()));
//            out.write("\"/>\r\n");
//
//            out.write("<compressor-type value=\"");
//            out.write(graph.getProcessingData().getCompressorType().toString());
//            out.write("\"/>\r\n");
//
//            out.write("<cluster-factor value=\"");
//            out.write(Float.toString(graph.getProcessingData().getClusterFactor()));
//            out.write("\"/>\n");
//
//            out.write("<number-neighbors value=\"");
//            out.write(Integer.toString(graph.getProcessingData().getNumberNeighborsConnection()));
//            out.write("\"/>\r\n");
//
//            out.write("<number-control-points value=\"");
//            out.write(Integer.toString(graph.getProcessingData().getNumberControlPoints()));
//            out.write("\"/>\r\n");
//
//            out.write("<number-objects value=\"");
//            out.write(Integer.toString(graph.getProcessingData().getNumberObjects()));
//            out.write("\"/>\r\n");
//
//            out.write("<number-dimensions value=\"");
//            out.write(Integer.toString(graph.getProcessingData().getNumberDimensions()));
//            out.write("\"/>\r\n");
//
//            out.write("<dimensionality-reduction value=\"");
//            out.write(graph.getProcessingData().getDimensionReductionType().toString());
//            out.write("\"/>\r\n");
//
//            out.write("<resulting-dimensions value=\"");
//            out.write(Integer.toString(graph.getProcessingData().getTargetDimension()));
//            out.write("\"/>\r\n");
//
//            ////////////////////////////////////
//            //writting the graph
//
//            //writting the vertex
//            out.write("<!--   vertex   -->\r\n");
//
//            for (Vertex v : graph.getVertex()) {
//                out.write("<vertex id=\"");
//                out.write(Long.toString(v.getId()));
//                out.write("\">\r\n");
//
//                out.write("<valid value=\"");
//                if (v.isValid()) {
//                    out.write("1");
//                } else {
//                    out.write("0");
//                }
//                out.write("\"/>\r\n");
//
//                out.write("<x-coordinate value=\"");
//                out.write(Float.toString(v.getX()));
//                out.write("\"/>\r\n");
//
//                out.write("<y-coordinate value=\"");
//                out.write(Float.toString(v.getY()));
//                out.write("\"/>\r\n");
//
//                out.write("<url value=\"");
//                out.write(convert(deConvert(encodeToValidChars(v.getUrl()))));
//                out.write("\"/>\r\n");
//
//                out.write("<scalars>\r\n");
//                for (Scalar s : graph.getScalars()) {
//                    out.write("<scalar name=\"");
//                    out.write(s.getName().replaceAll("\"", "'"));
//                    out.write("\" value=\"");
//                    out.write(Float.toString(v.getScalar(s)));
//                    out.write("\"/>\r\n");
//                }
//                out.write("</scalars>\r\n");
//
//                out.write("<labels>\r\n");
//                for (String t : graph.getTitles()) {
//                    int index = graph.getTitleIndex(t);
//                    v.changeTitle(index);
//
//                    out.write("<label name=\"");
//                    out.write(t.replaceAll("\"", "'"));
//                    out.write("\" value=\"");
//                    out.write(convert(deConvert(encodeToValidChars(v.toString()))));
//                    out.write("\"/>\r\n");
//                }
//                out.write("</labels>\r\n");
//
//                out.write("</vertex>\r\n");
//            }
//
//            //writting the edges
//            out.write("<!--   edges   -->\r\n");
//            for (Connectivity con : graph.getConnectivities()) {
//                out.write("<edges name=\"");
//                out.write(con.getName());
//                out.write("\">\r\n");
//
//                for (Edge e : con.getEdges()) {
//                    out.write("<edge source=\"");
//                    out.write(Long.toString(e.getSource().getId()));
//                    out.write("\" target=\"");
//                    out.write(Long.toString(e.getTarget().getId()));
//                    out.write("\" length=\"");
//                    out.write(Float.toString(e.getLength()));
//                    out.write("\"/>\r\n");
//                }
//                out.write("</edges>\r\n");
//            }
//
//            out.write("</graph>\r\n");
//
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
//                    Logger.getLogger(XMLGraphWriter.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
    }

    public static String convert(String value) {
        if (value != null) {
            value = value.replaceAll("&", "&amp;");
            value = value.replaceAll("<", "&lt;");
            value = value.replaceAll(">", "&gt;");
            value = value.replaceAll("\"", "&quot;");
            value = value.replaceAll("\'", "&#39;");
        } else {
            return "";
        }
        return value;
    }

    public static String deConvert(String value) {
        if (value != null) {
            value = value.replaceAll("&amp;", "&");
            value = value.replaceAll("&lt;", "<");
            value = value.replaceAll("&gt;", ">");
            value = value.replaceAll("&quot;", "\"");
            value = value.replaceAll("&#39;", "\'");
        } else {
            return "";
        }
        return value;
    }

    public static String encodeToValidChars(String pData) {
        StringBuilder encodedData = new StringBuilder();

        for (int i = 0; i < pData.length(); i++) {
            char ch = pData.charAt(i);
            int chVal = (int) ch;

            if (chVal >= 32 && chVal <= 255) {
                encodedData.append((char) chVal);
            } else {
                encodedData.append(" ");
            }
        }

        return encodedData.toString();
    }

}
