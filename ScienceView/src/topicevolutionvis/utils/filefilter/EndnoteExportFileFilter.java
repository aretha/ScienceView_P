/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package topicevolutionvis.utils.filefilter;

/**
 *
 * @author Aretha
 */
public class EndnoteExportFileFilter extends MyFileFilter {

    @Override
    public String getDescription() {
       return "EndNote Export Format File (*.enw)";
    }

    @Override
    public String getProperty() {
        return "ENW.DIR";
    }

    @Override
    public String getFileExtension() {
        return "enw";
    }

}
