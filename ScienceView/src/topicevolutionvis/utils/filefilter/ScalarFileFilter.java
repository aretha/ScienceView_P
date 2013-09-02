/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.utils.filefilter;

/**
 *
 * @author Aretha
 */
public class ScalarFileFilter extends MyFileFilter {

    @Override
    public String getDescription() {
        return "Scalar File (*.scalar)";
    }

    @Override
    public String getProperty() {
        return "SCALAR.DIR";
    }

    @Override
    public String getFileExtension() {
        return "scalar";
    }
}
