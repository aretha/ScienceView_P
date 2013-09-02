/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.utils.filefilter;

/**
 *
 * @author Aretha
 */
public class StopwordsFileFilter extends MyFileFilter {

    @Override
    public String getDescription() {
        return "Stopwords File";
    }

    @Override
    public String getProperty() {
        return "SPW.DIR";
    }

    @Override
    public String getFileExtension() {
        return "spw";
    }
}
