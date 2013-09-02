/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.utils.filefilter;

/**
 *
 * @author Aretha
 */
public class ZipFileFilter extends MyFileFilter {

    @Override
    public String getDescription() {
         return "Zip File (*.zip)";
    }

    @Override
    public String getProperty() {
        return "ZIP.DIR";
    }

    @Override
    public String getFileExtension() {
        return "zip";
    }
}
