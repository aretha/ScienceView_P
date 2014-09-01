package topicevolutionvis.graph;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.util.GeometricShapeFactory;
import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * @author Fernando Vieira Paulovich
 *
 * This class represents a edge on the map.
 */
public class Arrow extends Edge {

    /**
     * Constructor of the edge
     *
     * @param weight
     * @param source The first vertex
     * @param target The second vertex
     */
    public Arrow(float weight, int source, int target) {
        this(source, target);
        this.weight = weight;
    }

    /**
     * Constructor of the edge
     *
     * @param source The first vertex
     * @param target The second vertex
     */
    public Arrow(int source, int target) {
        super(source, target);
    }

    /**
     * Drawn a edge on a graphical device
     *
     * @param image
     * @param g2 The graphical device
     * @param globalsel Indicates if there is at least one selected vertex on
     * the graph this vertex belongs to
     * @param highquality
     * @param connectityName
     * @param graph
     */
    @Override
    public void draw(Image image, Graphics2D g2, boolean globalsel, boolean highquality, String connectityName, TemporalGraph graph) {

        if (image != null) {
            g2 = (Graphics2D) image.getGraphics();
        }
        if (highquality) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        Vertex v_source = graph.getVertexById(source), v_target = graph.getVertexById(target);

        //Combines the color of the two vertex to paint the edge
        if (!globalsel || (v_target.isSelected() || v_source.isSelected())) {
            float alpha = Math.min(v_source.getAlpha(), v_target.getAlpha());
            if (alpha != 1.0f) {
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha));
            }
        } else {
            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.2f));
        }


        this.color = new Color((v_source.getColor().getRed() + v_target.getColor().getRed()) / 2,
                (v_source.getColor().getGreen() + v_target.getColor().getGreen()) / 2,
                (v_source.getColor().getBlue() + v_target.getColor().getBlue()) / 2);

        g2.setColor(this.color);
        g2.setStroke(new BasicStroke(1.3f));
//        if (v_target.isSelected() || v_source.isSelected()) {
//            g2.setStroke(new BasicStroke(3.0f));
//        } else {
        g2.setStroke(new BasicStroke(this.getWidth()));
//        }

        GeneralPath path = new GeneralPath();

        double p1x = v_target.getX(), p1y = v_target.getY();   // P1
        double p2x = v_source.getX(), p2y = v_source.getY();  // P2
        Coordinate aux = new Coordinate(p1x, p1y);
        LineSegment line = new LineSegment(new Coordinate(p2x, p2y), aux);
        GeometricShapeFactory gsf = new GeometricShapeFactory();
        gsf.setSize(v_target.getRay()+3);
        gsf.setNumPoints(100);
        gsf.setCentre(aux);
        Coordinate intersection = gsf.createCircle().intersection(line.toGeometry(new GeometryFactory())).getCoordinate();
        p1x = intersection.x;
        p1y = intersection.y;
        double arrSize = 8;          // Size of the arrow segments

        double prop = v_target.getRay() / Math.sqrt(Math.pow(p2x - p1x, 2) + Math.pow(p2y - p1y, 2));
        double a = (p2y - p1y) / (p2x - p1x);
        if (p2x > p1x) {
            p2x = (1 - prop) * (p2x - p1x) + p1x;
        } else if (p1x > p2x) {
            p2x = prop * (p1x - p2x) + p2x;
        } else {
            p2x = p1x;
        }
        p2y = a * p2x + (p1y - a * p1x);
        double adjSize = (arrSize / Math.sqrt(2));
        double ex = p2x - p1x;
        double ey = p2y - p1y;
        double abs_e = Math.sqrt(ex * ex + ey * ey);
        ex /= abs_e;
        ey /= abs_e;

        // Creating  arrow
        path.moveTo(p1x, p1y);
        path.lineTo(p2x, p2y);
        path.lineTo(p2x + (ey - ex) * adjSize, p2y - (ex + ey) * adjSize);
        path.moveTo(p2x, p2y);
        path.lineTo(p2x - (ey + ex) * adjSize, p2y + (ex - ey) * adjSize);
        g2.draw(path);
        g2.setStroke(new BasicStroke(1.5f));

        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));
        if (Arrow.showLength) {
            String label = Float.toString(this.weight);
            double x = 5 + Math.abs(v_source.getX() - v_target.getX()) / 2
                    + Math.min(v_source.getX(), v_target.getX());
            double y = Math.abs(v_source.getY() - v_target.getY()) / 2
                    + Math.min(v_source.getY(), v_target.getY());

            //Getting the font information
            java.awt.FontMetrics metrics = g2.getFontMetrics(g2.getFont());

            //Getting the label size
            int width = metrics.stringWidth(label);
            int height = metrics.getAscent();

            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.75f));
            g2.setPaint(Color.WHITE);
            g2.fill(new Rectangle((int) x - 2, (int) y - height, width + 4, height + 4));
            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));

            g2.setColor(Color.BLACK);
            g2.drawRect((int) x - 2, (int) y - height, width + 4, height + 4);

            g2.drawString(label, (float) x, (float) y);
        }
    }
}
