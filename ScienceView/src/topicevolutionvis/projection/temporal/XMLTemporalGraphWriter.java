/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.projection.temporal;

/**
 *
 * @author Aretha
 */
public class XMLTemporalGraphWriter {

//    public static void save(TemporalProjectionViewer tprojViewer, String description, String filename) throws IOException {
//        try {
//            Element child, child2, child3;
//            ProjectionData pdata = tprojViewer.projection.getProjectionData();
//
//            //Creating an empty XML Document
//            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
//            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
//            Document doc = docBuilder.newDocument();
//
//            //Creating the XML tree
//            Element root = doc.createElement("projection");
//            root.setAttribute("description", description);
//            doc.appendChild(root);
//
//            child = doc.createElement("corpus-type");
//            child.setAttribute("value", pdata.getCorpusType().toString());
//            root.appendChild(child);
//
//            child = doc.createElement("projection-technique");
//            child.setAttribute("value", pdata.getProjectionType().toString());
//            root.appendChild(child);
//
//            child = doc.createElement("distance-type");
//            child.setAttribute("value", pdata.getDissimilarityType().toString());
//            root.appendChild(child);
//
//            child = doc.createElement("source-file");
//            child.setAttribute("value", pdata.getSourceFile());
//            root.appendChild(child);
//
//            if (pdata.isUseStopword()) {
//                child = doc.createElement("stopwords-file");
//                child.setAttribute("value", SystemPropertiesManager.getInstance().getProperty("SPW.FILE"));
//                root.appendChild(child);
//            }
//
//            child = doc.createElement("luhn-lower-cut");
//            child.setAttribute("value", Integer.toString(pdata.getLunhLowerCut()));
//            root.appendChild(child);
//
//            child = doc.createElement("luhn-upper-cut");
//            child.setAttribute("value", Integer.toString(pdata.getLunhUpperCut()));
//            root.appendChild(child);
//
//            child = doc.createElement("number-grams");
//            child.setAttribute("value", Integer.toString(pdata.getNumberGrams()));
//            root.appendChild(child);
//
//            child = doc.createElement("number-objects");
//            child.setAttribute("value", Integer.toString(pdata.getNumberObjects()));
//            root.appendChild(child);
//
//            child = doc.createElement("number-dimensions");
//            child.setAttribute("value", Integer.toString(pdata.getNumberDimensions()));
//            root.appendChild(child);
//
//            for (Entry<Integer, TemporalGraph> entry : tprojViewer.projection.getGraphs().entrySet()) {
//                child = doc.createElement("graph");
//                child.setAttribute("year", Integer.toString(entry.getKey()));
//                root.appendChild(child);
//
//                for (Vertex vertex : entry.getValue().getVertex()) {
//                    child2 = doc.createElement("vertex");
//                    child2.setAttribute("id", Integer.toString(vertex.getUrl()));
//                    child.appendChild(child2);
//
//                    child3 = doc.createElement("x-coordinate");
//                    child3.setAttribute("value", Float.toString(vertex.getX()));
//                    child2.appendChild(child3);
//
//                    child3 = doc.createElement("y-coordinate");
//                    child3.setAttribute("value", Float.toString(vertex.getY()));
//                    child2.appendChild(child3);
//
//                    child3 = doc.createElement("alpha");
//                    child3.setAttribute("value", Float.toString(vertex.getAlpha()));
//                    child2.appendChild(child3);
//                }
//            }
//
//            //Output the XML
//            XMLSerializer serializer = new XMLSerializer();
//            serializer.setOutputCharStream(new java.io.FileWriter(filename));
//            serializer.serialize(doc);
//
//
//        } catch (Exception ex) {
//            Logger.getLogger(XMLTemporalGraphWriter.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }
}
