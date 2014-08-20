package topicevolutionvis.graph;

import gnu.trove.list.array.TDoubleArrayList;
import java.awt.*;
import topicevolutionvis.projection.temporal.TemporalProjection;
import topicevolutionvis.view.color.ColorTable;

/**
 * This class represents a vertex on the map.
 *
 * @author Fernando Vieira Paulovich
 */
public class Vertex implements Comparable<Vertex>, Cloneable {

    private static final double EPSILON = 0.00001f;
    private int id = -1; //The vertex identification
    private TDoubleArrayList scalars = new TDoubleArrayList();  //The scalars associated with this vertex
    private int publishedYear;
    private TemporalProjection tprojection;
    private boolean showLabel = false;
    private static Font font = new Font("Tahoma", Font.PLAIN, 12);
    private Color color = Color.RED; //The vertex color
    private double x = 0; //The x-coodinate of the vertex
    private double y = 0;  //The y-coodinate of the vertex
    private static int rayBase = 4; //The rayFactor of the vertex
    private double rayFactor = 0.0;  //The size of vertex ray (it must stay between 0.0 and 1.0)
    private boolean selected = false;
    private boolean inneighborhood = false;
    private static boolean drawAsCircles = true;
    private static Color strokeColor = Color.BLACK;
    private float alpha = 1.0f;
    
        @Override
    public Object clone() throws CloneNotSupportedException {
        Vertex newVertex = new Vertex(this.id, this.x, this.y);
        newVertex.scalars = this.scalars;
        newVertex.publishedYear = this.publishedYear;
        newVertex.tprojection = this.tprojection;
        newVertex.showLabel = this.showLabel;
        newVertex.color = this.color;
        newVertex.rayFactor = this.rayFactor;
        newVertex.selected = this.selected;
        Vertex.setDrawAsCircles(drawAsCircles);
        newVertex.inneighborhood = this.inneighborhood;
        newVertex.alpha = this.alpha;
        return newVertex;
    }

    /**
     * A vertex constructor
     *
     * @param id The identification of the vertex
     * @param x The x-coordinate of the vertex
     * @param y The y-coordinate of the vertex
     */
    public Vertex(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    /**
     * A vertex constructor
     *
     * @param id The identification of the vertex
     */
    public Vertex(int id) {
        this.id = id;
    }



    /**
     * Draw the vertex on a graphical device
     *
     * @param image
     * @param g2 The graphical device
     * @param globalsel Indicates if there is at least one selected vertex on
     * the graph this vertex belongs to
     * @param highquality
     */
    
    
    public void draw(Image image, java.awt.Graphics2D g2, boolean globalsel, boolean highquality) {

        if (image != null) {
            g2 = (Graphics2D) image.getGraphics();
        }

        if (highquality) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        if (Vertex.drawAsCircles) {
            if (this.alpha != 1.0f) {
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha));
            }

            g2.setColor(this.color);
            g2.fillOval(((int) this.x) - this.getRay(), ((int) this.y)
                    - this.getRay(), this.getRay() * 2, this.getRay() * 2);

            int previousRayBase = Vertex.getRayBase();
            if (this.selected) {
                g2.setStroke(new BasicStroke(2.0f));
                g2.setColor(Color.yellow);
                Vertex.setRayBase(previousRayBase + 1);
            } else if (this.inneighborhood) {
                g2.setColor(getStrokeColor());
                g2.setStroke(new BasicStroke(2.0f));
            } else {
                g2.setColor(getStrokeColor());
            }
            g2.drawOval(((int) this.x) - this.getRay(), ((int) this.y)
                    - this.getRay(), this.getRay() * 2, this.getRay() * 2);

            if (this.selected || this.inneighborhood) {
                rayBase = 4;
                g2.setColor(getStrokeColor());
                g2.setStroke(new BasicStroke(1.0f));
                Vertex.setRayBase(previousRayBase);
            }
            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));

            //show the label associated to this vertex
            if (this.showLabel) {
                g2.setFont(Vertex.font);
                java.awt.FontMetrics metrics = g2.getFontMetrics(g2.getFont());

                int width = metrics.stringWidth(this.toString().trim());
                int height = metrics.getAscent();

                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.75f));
                g2.setPaint(Color.WHITE);
                g2.fill(new Rectangle(((int) this.x) + this.getRay() + 5 - 2,
                        ((int) this.y) - 1 - height, width + 4, height + 4));
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));

                g2.setColor(Color.DARK_GRAY);
                g2.drawRect(((int) this.x) + this.getRay() + 5 - 2, ((int) this.y) - 1 - height,
                        width + 4, height + 4);

                g2.drawString(this.toString().trim(), ((int) this.x) + this.getRay() + 5, ((int) this.y));
            }
        } else { //draw as points
            if (!globalsel || this.selected) {
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));
            } else {
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.2f));
            }

            g2.setColor(this.color);
            g2.drawLine((int) this.x - 1, (int) this.y - 1, (int) this.x + 1, (int) this.y - 1);
            g2.drawLine((int) this.x - 1, (int) this.y, (int) this.x + 1, (int) this.y);
            g2.drawLine((int) this.x - 1, (int) this.y + 1, (int) this.x + 1, (int) this.y + 1);

            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));

            //show the label associated to this vertex
            if (this.showLabel) {
                g2.setFont(Vertex.font);
                java.awt.FontMetrics metrics = g2.getFontMetrics(g2.getFont());

                int width = metrics.stringWidth(this.toString().trim());
                int height = metrics.getAscent();

                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.75f));
                g2.setPaint(Color.WHITE);
                g2.fill(new Rectangle(((int) this.x) + this.getRay() + 5 - 2,
                        ((int) this.y) - 1 - height, width + 4, height + 4));
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));

                g2.setColor(Color.DARK_GRAY);
                g2.drawRect(((int) this.x) + this.getRay() + 5 - 2, ((int) this.y) - 1 - height,
                        width + 4, height + 4);

                g2.drawString(this.toString().trim(), ((int) this.x) + this.getRay() + 5, ((int) this.y));
            }

        }
    }

    /**
     * Check if the point (x,y) is inside this vertex
     *
     * @param x The x-coordinate of the point
     * @param y The y-coordinate of the point
     * @return Return true if the ponint (x,y) is inside the vertex; false
     * otherwise
     */
    public boolean isInside(int x, int y) {
        return (Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2)) <= this.getRay());
    }

    /**
     * Check if the vertex inside on a rectangle
     *
     * @param rectangle The rectangle
     * @return Return true if the vertex inside the rectangle; false otherwise
     */
    public boolean isInside(java.awt.Rectangle rectangle) {
        return ((this.x >= rectangle.x) && (this.x - rectangle.x < rectangle.width))
                && ((this.y >= rectangle.y) && (this.y - rectangle.y < rectangle.height));
    }

    /**
     * Return the color of the vertex
     *
     * @return The color of the vertex
     */
    public Color getColor() {
        return color;
    }

    /**
     * Changes the color of the vertex
     *
     * @param color The new color of the vertex
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Changes the color of the vertex according to a scalar.
     *
     * @param scalar The scalar.
     * @param colorTable The color table used to color the vertex.
     * @param logscale
     */
    public void setColor(Scalar scalar, ColorTable colorTable, boolean logscale) {
        if (colorTable != null) {
            if (scalar.getMin() >= 0.0f && scalar.getMax() <= 1.0f) {
                this.color = colorTable.getColor(this.getScalar(scalar, logscale));
            } else {
                this.color = colorTable.getColor(this.getNormalizedScalar(scalar, logscale));
            }
        }
    }

    /**
     * Return the x-coordinate of the vertex
     *
     * @return The x-coordinate of the vertex
     */
    public double getX() {
        return x;
    }

    /**
     * Changes the x-coordinate of the vertex
     *
     * @param x The new x-coordinate of the vertex
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Return the y-coordinate of the vertex
     *
     * @return The y-coordinate of the vertex
     */
    public double getY() {
        return y;
    }

    /**
     * Changes the y-coordinate of the vertex
     *
     * @param y The new y-coordinate of the vertex
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Return the rayBase of the vertex
     *
     * @return The rayBase of the vertex
     */
    public static int getRayBase() {
        return rayBase;
    }

    /**
     * Changes the rayBase of all vertices.
     *
     * @param aRay The new rayBase of the vertex
     */
    public static void setRayBase(int aRay) {
        rayBase = aRay;
    }

    public void setRayFactor(double ray) {
        assert (ray >= 0.0 && ray <= 1.0) : "Out of range ray factor.";

        this.rayFactor = ray;
    }

    public double getRayFactor() {
        return this.rayFactor;
    }

    public int getRay() {
        return (int) (rayBase + (this.rayFactor * rayBase));
    }

    public double distanceTo(Vertex aux) {
        return Math.sqrt(Math.pow(this.x - aux.x, 2) + Math.pow(this.y - aux.y, 2));
    }

    public void setRayFactor(Scalar scalar, boolean logscale) {
        if (scalar.getMin() >= 0.0f && scalar.getMax() <= 1.0f) {
            this.rayFactor = this.getScalar(scalar, logscale);
        } else {
            this.rayFactor = this.getNormalizedScalar(scalar, logscale);
        }
    }

    public void setPublishedYear(Integer year) {
        this.publishedYear = year;
    }

    public Integer getPublishedYear() {
        return this.publishedYear;
    }

    /**
     * Returns the vertex identification
     *
     * @return The vertex identification
     */
    public int getId() {
        return id;
    }

    /**
     * Change the vertex identification
     *
     * @param id The identification of the vertex
     */
    public void setId(int id) {
        this.id = id;
    }

    public void setTemporalProjection(TemporalProjection tprojection) {
        this.tprojection = tprojection;
    }

    /**
     * Return a string representing the vertex
     *
     * @return A String representing the vertex
     */
    @Override
    public String toString() {
        return this.getPublishedYear() + " - " + this.tprojection.getTitleDocument(id);

    }

    public void setScalar(Scalar scalar, double value) {
        assert (scalar.getIndex() >= 0) : "Error scalar created outside the method Graph.addScalar(...).";

        if (scalar != null) {
            if (this.scalars.size() > scalar.getIndex()) {
                this.scalars.set(scalar.getIndex(), value);
            } else {
                int size = this.scalars.size();
                for (int i = 0; i < scalar.getIndex() - size; i++) {
                    this.scalars.add(0.0f);
                }
//                if (scalar.toString().compareTo("dbscan") == 0) {
//                    System.out.println(scalar.toString() + ": " + value);
//                }
                this.scalars.add(value);
            }

            if (scalar.getMin() > value) {
                scalar.setMin(value);
            }

            if (scalar.getMax() < value) {
                scalar.setMax(value);
            }
        }
    }

    public double getScalar(Scalar scalar, boolean logscale) {

        if (scalar != null && this.scalars.size() > scalar.getIndex() && scalar.getIndex() > -1) {
            if (!logscale) {
                return this.scalars.get(scalar.getIndex());
            } else {

                double min_log = 0;
                if (scalar.getMin() > 0) {
                    min_log = Math.log(scalar.getMin());
                }
                double value_log = 0;
                if (this.scalars.get(scalar.getIndex()) > 0) {
                    value_log = Math.log(this.scalars.get(scalar.getIndex()));
                }
                return (value_log - min_log) / (Math.log(scalar.getMax()) - min_log);
            }

        } else {
            return 0.0f;
        }
    }

    public double getNormalizedScalar(Scalar scalar, boolean logscale) {
        if (scalar != null && this.scalars.size() > scalar.getIndex() && scalar.getIndex() > -1) {
            if (scalar.getMax() > scalar.getMin()) {
                double value = this.scalars.get(scalar.getIndex());
                if (!logscale) {
                    return (value - scalar.getMin()) / (scalar.getMax() - scalar.getMin());
                } else {
                    double min_log = 0;
                    if (scalar.getMin() != 0) {
                        min_log = Math.log(scalar.getMin());
                    }
                    double max_log = 0;
                    if (scalar.getMax() != 0) {
                        max_log = Math.log(scalar.getMax());
                    }
                    double value_log = 0;
                    if (value != 0) {
                        value_log = Math.log(value);
                    }
                    return ((value_log - min_log) / (max_log - min_log));
                }
            } else {
                return 0.0;
            }
        } else {
            return 0.0;
        }
    }

    public void removeScalar(Scalar scalar) {
        if (scalar != null && this.scalars.size() > scalar.getIndex()) {
            this.scalars.remove(scalar.getIndex());
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setInNeighborhood(boolean neighbor) {
        this.inneighborhood = neighbor;
    }

    public static boolean isDrawAsCircles() {
        return drawAsCircles;
    }

    public static void setDrawAsCircles(boolean aDrawAsCircles) {
        drawAsCircles = aDrawAsCircles;
    }

    public static Color getStrokeColor() {
        return strokeColor;
    }

    public static void setStrokeColor(Color color) {
        strokeColor = color;
    }

    public static Font getFont() {
        return font;
    }

    public static void setFont(Font aFont) {
        font = aFont;
    }

    public boolean isShowLabel() {
        return this.showLabel;
    }

    public void setShowLabel(boolean showTitle) {
        this.showLabel = showTitle;
    }

    /**
     * @return the alpha
     */
    public float getAlpha() {
        return alpha;
    }

    /**
     * @param aAlpha the alpha to set
     */
    public void setAlpha(float aAlpha) {
        alpha = aAlpha;
    }

    //C = (alpha * (A-B)) + B
//    private void simulateAlpha(BufferedImage image, float alpha, int x, int y, int rgb) {
//        int oldrgb = image.getRGB(x, y);
//        int oldr = (oldrgb >> 16) & 0xFF;
//        int oldg = (oldrgb >> 8) & 0xFF;
//        int oldb = oldrgb & 0xFF;
//
//        int newr = (int) ((alpha * (((rgb >> 16) & 0xFF) - oldr)) + oldr);
//        int newg = (int) ((alpha * (((rgb >> 8) & 0xFF) - oldg)) + oldg);
//        int newb = (int) ((alpha * ((rgb & 0xFF) - oldb)) + oldb);
//
//        int newrgb = newb | (newg << 8) | (newr << 16);
//        image.setRGB(x, y, newrgb);
//    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Vertex)) {
            return false;
        }
        return this.id == ((Vertex) obj).id;
    }

    @Override
    public int compareTo(Vertex o) {
        if (Math.abs(this.x - o.x) == EPSILON) {
            if (Math.abs(this.y - o.y) == EPSILON) {
                return 0;
            } else if (Math.abs(this.y - o.y) > EPSILON) {
                return 1;
            } else {
                return -1;
            }
        } else if (Math.abs(this.x - o.x) > EPSILON) {
            return 1;
        } else {
            return -1;
        }
    }
}
