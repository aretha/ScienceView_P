/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.utils.filefilter;

/**
 *
 * @author barbosaa
 */
public class PNGFileFilter extends MyFileFilter {

    @Override
    public String getDescription() {
        return "PNG (Portable Network Graphics ) Image (*.png)";
    }

    @Override
    public String getProperty() {
        return "IMAGES.DIR";
    }

    @Override
    public String getFileExtension() {
        return "png";
    }
}
