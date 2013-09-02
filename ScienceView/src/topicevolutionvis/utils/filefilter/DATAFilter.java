package topicevolutionvis.utils.filefilter;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class DATAFilter extends MyFileFilter {

    @Override
    public String getDescription() {
        return "Multidimensional Points File (*.data)";
    }

    @Override
    public String getProperty() {
        return "POINTS.DIR";
    }

    @Override
    public String getFileExtension() {
        return "data";
    }

}
