package topicevolutionvis.graph;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class Scalar {

    public Scalar(String name) {
        this.name = name;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int id) {
        this.index = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Scalar) {
            if (this.name != null && obj != null) {
                return this.name.equals(((Scalar) obj).name);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return this.name;
    }
    private String name = "";
    private double max = Double.NEGATIVE_INFINITY;
    private double min = Double.POSITIVE_INFINITY;
    private int index = 0;
}
