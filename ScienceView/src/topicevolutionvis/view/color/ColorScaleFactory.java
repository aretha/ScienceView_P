package topicevolutionvis.view.color;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class ColorScaleFactory {

    public static ColorScale getInstance(ColorScaleType type) {

        if (type == ColorScaleType.HEATEDOBJECTS) {
            return new HeatedObjectScale();
        } else if (type == ColorScaleType.GRAYSCALE) {
            return new GrayScale();
        } else if (type == ColorScaleType.LINEARGRAYSCALE) {
            return new LinearGrayScale();
        } else if (type == ColorScaleType.LOCSSCALE) {
            return new LocsScale();
        } else if (type == ColorScaleType.RAINBOWCALE) {
            return new RainbowScale();
        } else if (type == ColorScaleType.PSEUDORAINBOWCALE) {
            return new PseudoRainbowScale();
        }

        return null;
    }

}
