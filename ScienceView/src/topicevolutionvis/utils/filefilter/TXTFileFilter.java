/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.utils.filefilter;

/**
 *
 * @author USER
 */
public class TXTFileFilter extends MyFileFilter {
    @Override
    public String getDescription() {
         return "Text File (*.txt)";
    }

    @Override
    public String getProperty() {
        return "SAVEPARAM.DIR";
    }

    @Override
    public String getFileExtension() {
        return "txt";
    }
}
