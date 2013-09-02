package topicevolutionvis.view.color;

import java.awt.Color;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class ColorTable {
/** Creates a new instance of ColorTable */
    public ColorTable() {
        this.colorScale = new UndefinedScale();
    }

    public ColorTable(ColorScale colorScale) {
        this.colorScale = colorScale;
    }

    public java.awt.Color getColor(double value) {
        return this.colorScale.getColor(value);
    }

    public int getNumberColors() {
        return this.colorScale.getNumberColors();
    }

    public ColorScale getColorScale() {
        return this.colorScale;
    }

    public void setColorAt(double value, Color color){
        this.colorScale.setColor(value, color);
    }

    public void setColorScale(ColorScale colorScale) {
        this.colorScale = colorScale;
    }

    public ColorScale colorScale;
}
