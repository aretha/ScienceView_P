/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package topicevolutionvis.utils.filefilter;

/**
 *
 * @author barbosaa
 */
public class ProjectionPointsFileFilter extends MyFileFilter {

    @Override
    public String getDescription() {
        return "Projection File(*.prj)";
    }

    @Override
    public String getProperty() {
        return "PROJECTION.DIR";
    }

    @Override
    public String getFileExtension() {
        return "prj";
    }
}
