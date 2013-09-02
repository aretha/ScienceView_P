/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.utils.filefilter;

import java.io.File;
import javax.swing.filechooser.FileFilter;
import topicevolutionvis.util.Utils;

/**
 *
 * @author Aretha
 */
public class SupportedFormatsCollections extends FileFilter {
    
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        
        String extension = Utils.getExtension(f);
        if (extension != null) {
            if (extension.equals("bib") || extension.equals("isi") || extension.equals("enw") || extension.equals("db")) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    
    @Override
    public String getDescription() {
        return "Supported Formats (*.bib, *.isi, *.enw)";
    }
}
