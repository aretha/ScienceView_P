package topicevolutionvis.view.color;

import java.awt.Color;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public abstract class ColorScale {

    public Color getColor(double value) {
        double maxlength = ((this.colors.length - 1) / 2) + (max * ((this.colors.length - 1) / 2));
        double minlength = (min * ((this.colors.length - 1) / 2));

        if (reverse) {
            value = 1 - value;
            maxlength = ((this.colors.length - 1) / 2) + ((1 - min) * ((this.colors.length - 1) / 2));
            minlength = ((1 - max) * ((this.colors.length - 1) / 2));
        }

        int index = (int) ((value * (maxlength - minlength)) + minlength);

        return (this.colors.length >= index) ? this.colors[index] : this.colors[this.colors.length - 1];
    }

    public void setColor(double value, Color color) {
        double maxlength = ((this.colors.length - 1) / 2) + (max * ((this.colors.length - 1) / 2));
        double minlength = (min * ((this.colors.length - 1) / 2));
        
        if (reverse) {
            value = 1 - value;
            maxlength = ((this.colors.length - 1) / 2) + ((1 - min) * ((this.colors.length - 1) / 2));
            minlength = ((1 - max) * ((this.colors.length - 1) / 2));
        }

        int index = (int) ((value * (maxlength - minlength)) + minlength);

        if (this.colors.length >= index) {
            this.colors[index] = color;
        } else {
            this.colors[this.colors.length - 1] = color;
        }
    }

    public int getNumberColors() {
        return this.colors.length;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }
    private double min = 0.0f;
    private double max = 1.0f;
    private boolean reverse = false;
    protected java.awt.Color[] colors;
}
