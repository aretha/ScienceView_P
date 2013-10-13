/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.projection.temporal;

import gnu.trove.iterator.TIntObjectIterator;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.h2.tools.Script;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import topicevolutionvis.graph.*;
import topicevolutionvis.util.PExConstants;
import topicevolutionvis.view.SaveProjectionDialog;

/**
 *
 * @author aretha
 */
public class SaveTemporalProjection extends SwingWorker<Void, Void> {

    private String filename;
    private File db_file, xml_file;
    private SaveProjectionDialog view;
    private TemporalProjection tproj;

    public SaveTemporalProjection(String filename, SaveProjectionDialog view, TemporalProjection tproj) {
        this.filename = filename;
        this.view = view;
        this.tproj = tproj;
    }

    @Override
    protected Void doInBackground() throws Exception {
        this.save_database();
        this.save_projection();
        this.create_zip();
        this.db_file.deleteOnExit();
        this.xml_file.deleteOnExit();
        return null;
    }

    private void create_zip() throws Exception {
        BufferedInputStream origin;
        FileOutputStream dest = new FileOutputStream(this.filename);
        try (ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest))) {
            byte data[] = new byte[2048];

            FileInputStream fi = new FileInputStream(this.db_file);
            origin = new BufferedInputStream(fi, 2048);
            ZipEntry entry = new ZipEntry(this.db_file.getName());
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, 2048)) != -1) {
                out.write(data, 0, count);
            }
            origin.close();

            fi = new FileInputStream(this.xml_file);
            origin = new BufferedInputStream(fi, 2048);
            entry = new ZipEntry(this.xml_file.getName());
            out.putNextEntry(entry);
            while ((count = origin.read(data, 0, 2048)) != -1) {
                out.write(data, 0, count);
            }
            origin.close();
        }
    }

    private void save_projection() throws Exception {
        Attr attr;
        Element projection, vertexList, vertex, edges, edge, element, scalars, scalar;

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        //root element
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("temporalprojection");
        doc.appendChild(rootElement);

        //collection id
        element = doc.createElement("collection-id");
        attr = doc.createAttribute("value");
        attr.setValue(Integer.toString(tproj.getDatabaseCorpus().getCollectionId()));
        element.setAttributeNode(attr);
        rootElement.appendChild(element);

        //collection name
        element = doc.createElement("collection-name");
        attr = doc.createAttribute("value");
        attr.setValue(tproj.getDatabaseCorpus().getCollectionName());
        element.setAttributeNode(attr);
        rootElement.appendChild(element);

        //number of documents
        element = doc.createElement("number-documents");
        attr = doc.createAttribute("value");
        attr.setValue(Integer.toString(tproj.getDatabaseCorpus().getNumberOfDocuments()));
        element.setAttributeNode(attr);
        rootElement.appendChild(element);

        //distance type
        element = doc.createElement("distance-type");
        attr = doc.createAttribute("value");
        attr.setValue(tproj.getProjectionData().getDissimilarityType().toString());
        element.setAttributeNode(attr);
        rootElement.appendChild(element);

        //dimensionality reduction
        element = doc.createElement("dimensionality-reduction");
        attr = doc.createAttribute("value");
        attr.setValue(tproj.getProjectionData().getDimensionReductionType().toString());
        element.setAttributeNode(attr);
        rootElement.appendChild(element);

        //number of grams
        element = doc.createElement("number-grams");
        attr = doc.createAttribute("value");
        attr.setValue(Integer.toString(tproj.getProjectionData().getNumberGrams()));
        element.setAttributeNode(attr);
        rootElement.appendChild(element);

        //source file
        element = doc.createElement("source-file");
        attr = doc.createAttribute("value");
        attr.setValue(tproj.getProjectionData().getSourceFile());
        element.setAttributeNode(attr);
        rootElement.appendChild(element);

        //luhn lower and upper cut
        element = doc.createElement("luhn-lower-cut");
        attr = doc.createAttribute("value");
        attr.setValue(Integer.toString(tproj.getProjectionData().getLunhLowerCut()));
        element.setAttributeNode(attr);
        rootElement.appendChild(element);
        element = doc.createElement("luhn-upper-cut");
        attr = doc.createAttribute("value");
        attr.setValue(Integer.toString(tproj.getProjectionData().getLunhUpperCut()));
        element.setAttributeNode(attr);
        rootElement.appendChild(element);

        //projection technique
        element = doc.createElement("projection-technique");
        attr = doc.createAttribute("value");
        attr.setValue(tproj.getProjectionData().getProjectionType().toString());
        element.setAttributeNode(attr);
        rootElement.appendChild(element);
//
//        //initial number of control points
//        element = doc.createElement("initial-number-control-points");
//        attr = doc.createAttribute("value");
//        attr.setValue(Integer.toString(tproj.getProjectionData().getNumberControlPoints()));
//        element.setAttributeNode(attr);
//        rootElement.appendChild(element);
//
//        //number of neighbors
//        element = doc.createElement("number-neighbors");
//        attr = doc.createAttribute("value");
//        attr.setValue(Integer.toString(tproj.getProjectionData().getNumberNeighborsConnection()));
//        element.setAttributeNode(attr);
//        rootElement.appendChild(element);

        //static stress
        element = doc.createElement("static-stress");
        attr = doc.createAttribute("value");
        attr.setValue(Float.toString(tproj.getProjectionData().getStaticStress()));
        element.setAttributeNode(attr);
        rootElement.appendChild(element);

        //dynamic stress
        element = doc.createElement("dynamic-stress");
        attr = doc.createAttribute("value");
        attr.setValue(Float.toString(tproj.getProjectionData().getDynamicStress()));
        element.setAttributeNode(attr);
        rootElement.appendChild(element);


        TemporalGraph graph;
        for (ArrayList<TemporalGraph> intermediates : tproj.getGraphs().values()) {
            graph = intermediates.get(TemporalProjection.getN() - 1);
            projection = doc.createElement("projection");
            rootElement.appendChild(projection);

            attr = doc.createAttribute("year");
            attr.setValue(Integer.toString(graph.getYear()));
            projection.setAttributeNode(attr);

            vertexList = doc.createElement("vertex-list");
            projection.appendChild(vertexList);

            TIntObjectIterator<Vertex> iterator = graph.getVertex().iterator();
            while (iterator.hasNext()) {
                iterator.advance();
                Vertex v = iterator.value();
                vertex = doc.createElement("vertex");
                vertexList.appendChild(vertex);

                //set id attr
                attr = doc.createAttribute("id");
                attr.setValue(Integer.toString(v.getId()));
                vertex.setAttributeNode(attr);

                //set year attr
                attr = doc.createAttribute("year");
                attr.setValue(Integer.toString(v.getPublishedYear()));
                vertex.setAttributeNode(attr);

                //x coordinate
                element = doc.createElement("x-coordinate");
                attr = doc.createAttribute("value");
                attr.setValue(Double.toString(v.getX()));
                element.setAttributeNode(attr);
                vertex.appendChild(element);

                //xcoordinate
                element = doc.createElement("y-coordinate");
                attr = doc.createAttribute("value");
                attr.setValue(Double.toString(v.getY()));
                element.setAttributeNode(attr);
                vertex.appendChild(element);

                //title
                element = doc.createElement("label");
                attr = doc.createAttribute("title");
                attr.setValue(tproj.getTitleDocument(v.getId()));
                element.setAttributeNode(attr);
                vertex.appendChild(element);

                scalars = doc.createElement("scalars");
                for (Scalar s : tproj.getVertexScalars()) {
                    if (s.toString().compareTo(PExConstants.DOTS) != 0) {
                        scalar = doc.createElement("scalar");
                        attr = doc.createAttribute("name");
                        attr.setValue(s.toString());
                        scalar.setAttributeNode(attr);
                        attr = doc.createAttribute("value");
                        attr.setValue(Double.toString(v.getScalar(s, false)));
                        scalar.setAttributeNode(attr);
                        scalars.appendChild(scalar);
                    }
                }

                vertex.appendChild(scalars);
            }


            for (Connectivity con : tproj.getConnectivities(graph.getYear())) {
                if (con.getName().compareTo(PExConstants.DOTS) != 0) {
                    edges = doc.createElement("edges-list");
                    attr = doc.createAttribute("name");
                    attr.setValue(con.getName());
                    edges.setAttributeNode(attr);

                    attr = doc.createAttribute("weighted");
                    if (con.isWeighted()) {
                        attr.setValue("true");
                    } else {
                        attr.setValue("false");
                    }
                    edges.setAttributeNode(attr);

                    attr = doc.createAttribute("directed");
                    if (con.isDirected()) {
                        attr.setValue("true");
                    } else {
                        attr.setValue("false");
                    }
                    edges.setAttributeNode(attr);

                    projection.appendChild(edges);

                    for (Edge e : con.getEdges()) {
                        edge = doc.createElement("edge");
                        attr = doc.createAttribute("source");
                        attr.setValue(Integer.toString(e.getSource()));
                        edge.setAttributeNode(attr);
                        attr = doc.createAttribute("target");
                        attr.setValue(Integer.toString(e.getTarget()));
                        edge.setAttributeNode(attr);
                        attr = doc.createAttribute("weight");
                        attr.setValue(Float.toString(e.getWeight()));
                        edge.setAttributeNode(attr);
                        edges.appendChild(edge);
                    }
                }
            }
        }


        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        xml_file = File.createTempFile("projection", ".xml");
        StreamResult result = new StreamResult(this.xml_file);

        transformer.transform(source, result);

    }

    private void save_database() throws Exception {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("./resources/config/database.properties")) {
            props.load(in);
            db_file = File.createTempFile("database", ".db");
            Script.execute(props.getProperty("jdbc.url"), props.getProperty("jdbc.username"), props.getProperty("jdbc.password"), this.db_file.getAbsolutePath());
        }
    }

    @Override
    public void done() {
        this.view.setStatus(false);
        JOptionPane.showMessageDialog(view, "Temporal Projection and Database successfully saved", "Message", JOptionPane.INFORMATION_MESSAGE);
        this.view.dispose();
    }
}
