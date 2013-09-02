/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.utils.filefilter;

/**
 *
 * @author Aretha
 */
public class DmatFileFilter extends MyFileFilter {


    @Override
    public String getDescription() {
        return "Distance Matrix File (*.dmat)";
    }

    @Override
    public String getFileExtension() {
        return "dmat";
    }

    @Override
    public String getProperty() {
        return "DMAT.DIR";
    }
}
