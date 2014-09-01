package topicevolutionvis.graph;

import java.awt.*;

/**
 * @author Fernando Vieira Paulovich
 *
 * This class represents a edge on the map.
 */
public class Edge implements Comparable<Edge> {

    public static final float NO_SIZE = -1;

    /**
     * Constructor of the edge
     *
     * @param length The edge's lenght
     * @param source The first vertex
     * @param target The second vertex
     */
    public Edge(float length, int source, int target) {
        this(source, target);
        this.weight = length;
    }

    /**
     * Constructor of the edge
     *
     * @param source The first vertex
     * @param target The second vertex
     */
    public Edge(int source, int target) {
        this.source = source;
        this.target = target;

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
    public void draw(Image image, Graphics2D g2, boolean globalsel, boolean highquality, String connectityName, TemporalGraph graph) {

        if (image != null) {
            g2 = (Graphics2D) image.getGraphics();
        }

        if (highquality) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        Vertex v_source = graph.getVertexById(source), v_target = graph.getVertexById(target);

        //Combines the color of the two vertex to paint the edge
        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.7f));
        if (!globalsel || (v_target.isSelected() || v_source.isSelected())) {
            float alpha = Math.min(v_source.getAlpha(), v_target.getAlpha());
            if (alpha != 1.0f) {
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha));
            }
        } else {
            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.1f));
        }

        this.color = new Color((v_source.getColor().getRed() + v_target.getColor().getRed()) / 2,
                (v_source.getColor().getGreen() + v_target.getColor().getGreen()) / 2,
                (v_source.getColor().getBlue() + v_target.getColor().getBlue()) / 2);

        g2.setColor(this.color);
        g2.setStroke(new BasicStroke(this.getWidth()));

        g2.drawLine(((int) v_source.getX()), ((int) v_source.getY()), ((int) v_target.getX()), ((int) v_target.getY()));
        g2.setStroke(new BasicStroke(1.0f));

        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));
        if (Edge.showLength) {
            String label = Float.toString(this.weight);
            float x = (float) (5 + Math.abs(v_source.getX() - v_target.getX()) / 2
                    + Math.min(v_source.getX(), v_target.getX()));
            float y = (float) (Math.abs(v_source.getY() - v_target.getY()) / 2
                    + Math.min(v_source.getY(), v_target.getY()));

            //Getting the font information
            java.awt.FontMetrics metrics = g2.getFontMetrics(g2.getFont());

            //Getting the label size
            int width = metrics.stringWidth(label);
            int height = metrics.getAscent();

            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.75f));
            g2.setPaint(Color.WHITE);
            g2.fill(new Rectangle((int) x - 2, (int) y - height, width + 4, height + 4));
            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));

            g2.setColor(java.awt.Color.BLACK);
            g2.drawRect((int) x - 2, (int) y - height, width + 4, height + 4);

            g2.drawString(label, x, y);
        }
    }

    /**
     * Return the color of the edge
     *
     * @return The color of the edge
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Changes the color of the edge
     *
     * @param color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    public int getSource() {
        return this.source;
    }

    public int getTarget() {
        return this.target;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Edge) {
            Edge e = (Edge) obj;
            return (((this.source == e.source) && (this.target == e.target))
                    || ((this.source == e.target) && (this.target == e.source)));
        }
        return false;
    }

    @Override
    public int compareTo(Edge o) {
        long source_aux, target_aux;

        if (this.source < this.target) {
            source_aux = this.source;
            target_aux = this.target;
        } else {
            source_aux = this.target;
            target_aux = this.source;
        }

        long sourceComp, targetComp;
        if (o.source < o.target) {
            sourceComp = o.source;
            targetComp = o.target;
        } else {
            sourceComp = o.target;
            targetComp = o.source;
        }

        if (source_aux - sourceComp < 0) {
            return -1;
        } else if (source_aux - sourceComp > 0) {
            return 1;
        } else {
            if (target_aux - targetComp < 0) {
                return -1;
            } else if (target_aux - targetComp > 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public float getWeight() {
        return weight;
    }

    public static boolean isShowLength() {
        return showLength;
    }

    public static void setShowLength(boolean aShowLength) {
        showLength = aShowLength;
    }

    public float getWidth() {
        return (float) (this.witdhFactor * (this.max_widht - this.min_widht) + this.min_widht);
    }

    public void setWidthFactor(float width) {
        assert (width >= 0.0f && width <= 1.0f) : "Out of range ray factor.";
        this.witdhFactor = width;
    }
    private float min_widht = 0.3f;
    private float max_widht = 0.5f;
    protected float weight = Edge.NO_SIZE;
    protected Color color = Color.RED; //Color of the edge
    protected int source; //The first vertex of the edge
    protected int target; //The second vertex of the edge
    protected static boolean showLength = false; //to indicate if the lenght is shown
    private double witdhFactor = 0.8;  //The width of the edge (it must stay between 0.0 and 1.0)
}
