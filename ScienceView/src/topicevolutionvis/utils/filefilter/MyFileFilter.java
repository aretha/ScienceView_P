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
public abstract class MyFileFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        return f.getName().toLowerCase().endsWith("." + this.getFileExtension()) || f.isDirectory();
    }

    @Override
    public abstract String getDescription();

    public abstract String getProperty();

    public abstract String getFileExtension();
}
