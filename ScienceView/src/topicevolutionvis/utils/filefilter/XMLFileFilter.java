/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package topicevolutionvis.utils.filefilter;

/**
 *
 * @author Aretha
 */
public class XMLFileFilter  extends MyFileFilter{

    @Override
    public String getDescription() {
        return "XML - Extensible Markup Language (*.xml)";
    }

    @Override
    public String getProperty() {
       return "XML.DIR";
    }

    @Override
    public String getFileExtension() {
        return "xml";
    }

}
