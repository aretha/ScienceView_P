/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.utils.filefilter;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Aretha
 */
public class ISIFileFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        return f.getName().toLowerCase().endsWith(".isi") || f.isDirectory();
    }

    @Override
    public String getDescription() {
        return "ISI File (*.isi)";
    }
}
