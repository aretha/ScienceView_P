/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.projection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import topicevolutionvis.graph.TemporalGraph;
import topicevolutionvis.projection.temporal.TemporalProjection;

/**
 *
 * @author Aretha
 */
public class XMLProjectionParser {

    public TemporalProjection parse(String filename) {
        try {
            Element proj_node;
            int year;
            ArrayList<TemporalGraph> graphs= new ArrayList<>();
            TemporalProjection projection = new TemporalProjection();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(filename));
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("projection");
            for (int i = 0; i < nodeLst.getLength(); i++) {
                proj_node = (Element) nodeLst.item(i);
                Integer.parseInt(proj_node.getAttribute("year"));

            }
            return projection;
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(XMLProjectionParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
