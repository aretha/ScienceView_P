package topicevolutionvis.topic;

import com.vividsolutions.jts.algorithm.ConvexHull;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.awt.*;
import java.awt.Point;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import setvis.SetOutline;
import setvis.bubbleset.BubbleSet;
import setvis.shape.AbstractShapeGenerator;
import setvis.shape.BSplineShapeGenerator;
import signalprocesser.voronoi.VPoint;
import signalprocesser.voronoi.VoronoiAlgorithm;
import signalprocesser.voronoi.representation.RepresentationFactory;
import signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation;
import topicevolutionvis.database.DatabaseCorpus;
import topicevolutionvis.graph.TemporalGraph;
import topicevolutionvis.graph.Vertex;
import topicevolutionvis.projection.temporal.TemporalProjection;
import topicevolutionvis.topic.TopicData.TopicVisualization;
import topicevolutionvis.view.TemporalProjectionViewer;

/**
 *
 * @author Fernando Vieira Paulovich, Roberto Pinho
 */
public abstract class Topic {

    protected int id;
    protected double relation = 0;
    private Geometry geom = null;
    public GeneralPath polygon = null;
    private com.vividsolutions.jts.geom.Point centroid = null;
    private float alpha_polygon = 1.0f;
    private boolean used_convex = true;
    protected static boolean showTopics = true;
    protected boolean selected = false;
    protected ArrayList<ArrayList<TopicTag>> terms = new ArrayList<>();
    protected TDoubleArrayList topicsWeights = new TDoubleArrayList();
    private Double minvalue_topictag = Double.MAX_VALUE;
    private Double maxvalue_topictag = -1.0;
    protected TIntArrayList vertex_id = new TIntArrayList();
    protected ArrayList<Coordinate> fake_vertex = new ArrayList<>();
    protected TIntArrayList vertex_next_added = new TIntArrayList();
    protected TemporalGraph graph;
    protected DatabaseCorpus corpus;
    protected TemporalProjection tprojection;
    public static final int USE_CONCAVE = 1;
    public static final int USE_CONVEX = 2;
    public static final int TEST_BOTH = 3;
    private int draw_polygon_option = TEST_BOTH;
    private boolean dashed = false;
    private boolean useGrayColor = false;

    public boolean isUseGrayColor() {
        return useGrayColor;
    }

    public void setUseGrayColor(boolean useGrayColor) {
        this.useGrayColor = useGrayColor;
    }

    public Topic(TIntArrayList vertex, TemporalProjection tprojection, TemporalGraph graph) {
        this.vertex_id = vertex;
        this.tprojection = tprojection;
        this.corpus = tprojection.getDatabaseCorpus();
        this.graph = graph;
    }

    public Topic(TIntArrayList vertex, ArrayList<Coordinate> fake_vertex, TemporalProjection tprojection, TemporalGraph graph) {
        this.vertex_id = vertex;
        this.fake_vertex = fake_vertex;
        this.tprojection = tprojection;
        this.corpus = tprojection.getDatabaseCorpus();
        this.graph = graph;
    }

    public boolean isAnimation() {
        return this.terms.isEmpty();
    }

    @Override
    public abstract Object clone() throws CloneNotSupportedException;

    @Override
    public boolean equals(Object aThat) {
        return aThat instanceof Topic && (this.id == ((Topic) aThat).getId());
    }

    /**
     * Creates a new instance of Topic
     *
     * @param aux
     */
    public void cloneInfo(Topic aux) {
        aux.id = id;
        aux.terms = terms;
        aux.topicsWeights = topicsWeights;
        aux.minvalue_topictag = minvalue_topictag;
        aux.maxvalue_topictag = maxvalue_topictag;
        aux.relation = relation;
    }

    public void setDrawPolygonOption(int option) {
        this.draw_polygon_option = option;
    }

    public int getDrawPolygonOption() {
        return this.draw_polygon_option;
    }

    public void setUseDashedStroke(boolean dashed) {
        this.dashed = dashed;
    }

    public boolean usedConvex() {
        return this.used_convex;
    }

    public void newTopic(double weight_topic) {
        this.terms.add(new ArrayList<TopicTag>());
        this.topicsWeights.add(weight_topic);
    }

    protected void addTopicTag(String term, double value) {
        this.terms.get(this.terms.size() - 1).add(new TopicTag(term, value));
        if (value > this.maxvalue_topictag) {
            maxvalue_topictag = value;
        }
        if (value < this.minvalue_topictag) {
            minvalue_topictag = value;
        }
    }

    public void drawTopicTerm(Image image, Graphics2D g2, java.awt.Font font, TemporalProjectionViewer.ViewPanel viewer, boolean highquality) {
        if (image != null) {
            g2 = (Graphics2D) image.getGraphics();
        }

        if (highquality) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        Rectangle2D bounds = polygon.getBounds2D();
        if (viewer.getTopicData().getTypeOfTopicVisualization() == TopicVisualization.SIMPLE) {
            g2.setFont(font);
            if (selected || showTopics) {
                DecimalFormat form = new DecimalFormat("###,###.##");
                //draw the first label
                if (this.terms.size() > 0) {
                    Point position = new Point();
                    position.x = (int) (bounds.getX() + bounds.getWidth() * 0.8);
                    position.y = (int) bounds.getY();
                    StringBuilder msg = new StringBuilder("(");
                    TopicTag tag;
                    for (int i = 0; i < terms.get(0).size(); i++) {
                        tag = terms.get(0).get(i);
                        if (i != terms.get(0).size() - 1) {
                            msg.append(tag.term).append(",");
                        } else {
                            msg.append(tag.term).append(")[").append(form.format(this.topicsWeights.get(0))).append("]");
                        }
                    }

                    Rectangle rect = (new StringBox(msg.toString())).draw(g2, position, font, selected, useGrayColor);

                    int n_topics = 1;
                    //draw all the other ones
                    for (int j = 1; j < this.terms.size(); j++) {
                        msg = new StringBuilder("(");
                        position = new Point();
                        position.x = (int) (bounds.getX() + bounds.getWidth() * 0.8);
                        position.y = rect.y + rect.height + 6;
                        if (n_topics >= 5 && !selected) {
                            (new StringBox("[...]")).draw(g2, position, font, selected, useGrayColor);
                            break;
                        }
                        for (int i = 0; i < terms.get(j).size(); i++) {
                            tag = terms.get(j).get(i);
                            if (i != terms.get(j).size() - 1) {
                                msg.append(tag.term).append(",");
                            } else {
                                msg.append(tag.term).append(")[").append(form.format(this.topicsWeights.get(j))).append("]");
                            }
                        }
                        rect = (new StringBox(msg.toString())).draw(g2, position, font, selected, useGrayColor);
                        n_topics++;
                    }
                }
            }
        } else { //TAGCLOUD
            g2.setColor(java.awt.Color.GRAY);
            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));
            if (selected || showTopics) {
                double sum_weights = 0.0d;
                for (int i = 0; i < this.topicsWeights.size(); i++) {
                    sum_weights += this.topicsWeights.get(i);
                }

                if (this.terms.size() > 0) {
                    Point position = new Point();
                    position.x = (int) (bounds.getX());
                    position.y = (int) (bounds.getY() + bounds.getHeight() * 0.2);

                    int begin_index, end_index;
                    double value, distribution = (maxvalue_topictag - minvalue_topictag) / 5;
                    Font f;
                    String msgString;
                    StringBuilder msg = new StringBuilder("");
                    AttributedString ats;
                    ArrayList<TopicTag> aux = new ArrayList<>();
                    for (ArrayList<TopicTag> term : terms) {
                        for (int i = 0; i < term.size(); i++) {
                            aux.add(term.get(i));
                        }
                    }

                    Collections.sort(aux, new Comparator<TopicTag>() {
                        @Override
                        public int compare(TopicTag o1, TopicTag o2) {
                            return Double.compare(o2.value, o1.value);
                        }
                    });

                    int count;
                    for (int n = 0; n < terms.size(); n++) {
                        count = 0;
                        for (int i = 0; i < terms.get(n).size(); i++) {
                            if (aux.indexOf(terms.get(n).get(i)) <= viewer.getTopicData().getMaxNumberOfTermsForTagCloud() - 1) {
                                msg.append(terms.get(n).get(i).term).append(" ");
                                count++;
                            }
                        }
                        if (count > 0 && n != terms.size() - 1) {
                            msg.append("| ");
                        }
                    }

                    msgString = msg.toString().trim();
                    begin_index = 0;
                    end_index = msgString.indexOf(" ");
                    if (end_index == -1) {
                        end_index = msgString.length();
                    }
                    ats = new AttributedString(msgString);
                    double weight;
                    for (int n = 0; n < terms.size(); n++) {
                        weight = this.topicsWeights.get(n);
                        for (int i = 0; i < terms.get(n).size(); i++) {
                            if (aux.indexOf(terms.get(n).get(i)) <= viewer.getTopicData().getMaxNumberOfTermsForTagCloud() - 1) {
                                value = terms.get(n).get(i).value;
                                if (value >= minvalue_topictag + (distribution * 4)) {
                                    f = new Font("Helvetica", Font.BOLD, (int) Math.ceil(20 + (weight * 1) / sum_weights + this.relation * 15));
                                } else if (value >= minvalue_topictag + (distribution * 3)) {
                                    f = new Font("Helvetica", Font.BOLD, (int) Math.ceil(18 + (weight * 1) / sum_weights + this.relation * 15));
                                } else if (value >= minvalue_topictag + (distribution * 2)) {
                                    f = new Font("Helvetica", Font.BOLD, (int) Math.ceil(16 + (weight * 1) / sum_weights + this.relation * 15));
                                } else if (value >= minvalue_topictag + (distribution)) {
                                    f = new Font("Helvetica", Font.BOLD, (int) Math.ceil(14 + (weight * 1) / sum_weights + this.relation * 25));
                                } else {
                                    f = new Font("Helvetica", Font.BOLD, (int) Math.ceil(13 + (weight * 1) / sum_weights + this.relation * 35));
                                }
                                try {
                                    ats.addAttribute(TextAttribute.FONT, f, begin_index, end_index);
                                } catch (Exception ex) {
                                    Logger.getLogger(Topic.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                begin_index = end_index + 1;
                                end_index = msgString.indexOf(" ", begin_index);
                                if (end_index == -1) {
                                    end_index = msg.length() - 1;
                                }
                            }
                        }
                    }

                    AttributedCharacterIterator iter = ats.getIterator();
                    int paragraphStart = iter.getBeginIndex();
                    int paragraphEnd = iter.getEndIndex();
                    FontRenderContext frc = g2.getFontRenderContext();
                    LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(iter, frc);
                    lineMeasurer.setPosition(paragraphStart);

                    //sroke branco no texto
                    AffineTransform textAt = new AffineTransform();
                    Shape shape;
                    g2.setStroke(new BasicStroke(0.05f));

                    double drawPosY = position.y, drawPosX;
                    TextLayout layout;
                    float formatWidth;
                    if (aux.size() < 5) {
                        formatWidth = (float) 110;
                    } else {
                        formatWidth = 250;
                    }

                    g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));
                    while (lineMeasurer.getPosition() < paragraphEnd) {
                        layout = lineMeasurer.nextLayout(formatWidth);
                        drawPosY += layout.getAscent();
                        if (layout.isLeftToRight()) {
                            drawPosX = position.x;
                        } else {
                            drawPosX = position.x + formatWidth - layout.getAdvance();
                        }

                        textAt.setToIdentity();
                        textAt.translate(drawPosX, drawPosY);
                        shape = layout.getOutline(textAt);

                        if (!this.useGrayColor) {
                            g2.setPaint(Color.BLACK);
                            g2.fill(shape);
                            g2.setPaint(Color.white);
                            g2.setStroke(new BasicStroke(0.3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1));
                            g2.draw(shape);
                        } else {
                            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.3f));
                            g2.setPaint(Color.DARK_GRAY);
                            g2.fill(shape);
                            g2.setPaint(Color.white);
                            g2.setStroke(new BasicStroke(0.3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1));
                            g2.draw(shape);
                        }
                    }
                }
            }
        }
    }

    public void drawTopic(Image image, Graphics2D g2, boolean highquality) {
        if (image != null) {
            g2 = (Graphics2D) image.getGraphics();
        }

        if (highquality) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        if (this.alpha_polygon != 1.0f) {
            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha_polygon));
        }

        g2.setPaint(new Color(100, 100, 100));
        if (!dashed) {
            if (selected) {
                g2.setColor(Color.yellow);
                g2.setStroke(new BasicStroke(3.0f));
            } else {
                g2.setStroke(new BasicStroke(1.5f));
            }

        } else {
            float[] dash = {10.0f, 10.0f};
            if (selected) {
                g2.setColor(Color.yellow);
                g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, dash, 0.0f));
            } else {
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, dash, 0.0f));
            }
        }
        g2.draw(polygon);
        g2.setColor(new Color(180, 180, 180));
        g2.fill(polygon);
//        //Draw fake vertex
//        g2.setColor(Color.BLACK);
//        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_IN, 1.0f));
//        g2.setStroke(new BasicStroke(2.0f));
//        for (Coordinate c : this.fake_vertex) {
//            g2.drawOval((int) c.x - 6, (int) c.y - 6, 12, 12);
//        }
    }

    public double weightDistance(java.awt.Point point) {
        if (this.polygon.contains(point)) {
            Rectangle2D bounds = this.polygon.getBounds2D();
            double cx = bounds.getCenterX();
            double cy = bounds.getCenterY();
            return (Math.sqrt((cx - point.x) * (cx - point.x) + (cy - point.y)
                    * (cy - point.y)) * (bounds.getWidth()));
        } else {
            return -1;
        }
    }

    public float getAlphaPolygon() {
        return alpha_polygon;
    }

    public void setAlphaPolygon(float alpha_polygon) {
        this.alpha_polygon = alpha_polygon;
    }

    public static boolean isShowTopics() {
        return showTopics;
    }

    public static void setShowTopics(boolean aShowTopics) {
        showTopics = aShowTopics;
    }

    public boolean isSelected() {
        return selected;
    }

    public com.vividsolutions.jts.geom.Point getCentroid() {
        return this.centroid;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setTemporalGraph(TemporalGraph graph) {
        this.graph = graph;
    }

    public void setNextAddedVertex(TIntArrayList vertex_next_added) {
        this.vertex_next_added = vertex_next_added;
    }

    public void addToNextAddedVertex(TIntArrayList param) {
        this.vertex_next_added.addAll(param);
    }

    public TIntArrayList getNextAddedVertex() {
        return this.vertex_next_added;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(50);
        for (ArrayList<TopicTag> t : this.terms) {
            s = s.append("(").append(t.get(0).term);
            for (int i = 1; i < t.size(); i++) {
                s = s.append(", ").append(t.get(i).term);
            }
            s = s.append(") ");
        }
        return s.toString();
    }

    public Geometry getGeometry() {
        return this.geom;
    }

    public void calcPolygon() {
        if (!vertex_id.isEmpty()) {
            GeneralPath convex_polygon = this.calcConvexPolygon();
            if (geom.getArea() < 1500 || vertex_id.size() < 10) {
                polygon = convex_polygon;
                this.used_convex = true;
            } else {
                if (this.draw_polygon_option == TEST_BOTH) {
                    if (!this.containsVertex(convex_polygon)) {
                        this.polygon = calcConvexPolygon();
                        this.used_convex = true;
                    } else {
                        this.polygon = this.calcBubbleSet();
                        this.used_convex = false;
                    }
                } else if (this.draw_polygon_option == USE_CONVEX) {
                    this.polygon = calcConvexPolygon();
                    this.used_convex = true;
                } else if (this.draw_polygon_option == USE_CONCAVE) {
                    this.polygon = this.calcBubbleSet();
                    this.used_convex = false;
                }
            }
        }
    }

    private boolean containsVertex(GeneralPath polygon) {
        //checking if the convex hull included vertices that do not belong to the cluster
        TIntObjectHashMap<Vertex> vertex_clone = new TIntObjectHashMap<>();
        TIntObjectIterator<Vertex> iterator = graph.getVertex().iterator();
        while (iterator.hasNext()) {
            iterator.advance();
            vertex_clone.put(iterator.key(), iterator.value());
        }
        for (int i = 0; i < this.vertex_id.size(); i++) {
            vertex_clone.remove(vertex_id.get(i));
        }
        for (TIntIterator it = vertex_next_added.iterator(); it.hasNext();) {
            vertex_clone.remove(it.next());
        }

        iterator = vertex_clone.iterator();
        while (iterator.hasNext()) {
            iterator.advance();
            Vertex v = iterator.value();
            if (polygon.contains(new Point2D.Double(v.getX(), v.getY()))) {
                return true;
            }
        }
        return false;
    }

    private GeneralPath calcConcavePolygon() {
        //in case, the convex hull contains vertices that do not belong to the cluster, build a concave hull        
        ArrayList<VPoint> points = new ArrayList<>(this.vertex_id.size() + this.fake_vertex.size());
        for (int i = 0; i < this.vertex_id.size(); i++) {
            Vertex v = graph.getVertexById(vertex_id.get(i));
            points.add(new VPoint((int) v.getX(), (int) v.getY())); // canto esquerdo superior
        }

        for (Coordinate p : this.fake_vertex) {
            points.add(new VPoint((int) p.x, (int) p.y)); // canto esquerdo superior
        }
        TriangulationRepresentation representation = new TriangulationRepresentation(new TriangulationRepresentation.CalcCutOff() {
            @Override
            public int calculateCutOff(TriangulationRepresentation rep) {
                return rep.getMaxLengthOfMinimumSpanningTree();
            }
        });

        // Convert points to the right form
        points = RepresentationFactory.convertPointsToTriangulationPoints(points);

        VoronoiAlgorithm.generateVoronoi(representation, points);

        ArrayList<VPoint> outterpoints = representation.getPointsFormingOutterBoundary();
        if (outterpoints != null) {
            Coordinate[] coords = new Coordinate[outterpoints.size()];
            for (int i = 0; i < outterpoints.size(); i++) {
                coords[i] = new Coordinate(outterpoints.get(i).x, outterpoints.get(i).y);
            }

            com.vividsolutions.jts.geom.Polygon poly = new com.vividsolutions.jts.geom.Polygon(new LinearRing(new CoordinateArraySequence(coords), new GeometryFactory()), null, new GeometryFactory());
            geom = poly.buffer(10, 8);
            this.centroid = geom.getCentroid();
            coords = geom.getCoordinates();

            GeneralPath concave_polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, coords.length);
            concave_polygon.moveTo(coords[0].x, coords[0].y);
            for (int i = 1; i < coords.length; i++) {
                concave_polygon.lineTo(coords[i].x, coords[i].y);
            }
            concave_polygon.closePath();
            return concave_polygon;
        }
        return null;
    }

    private GeneralPath calcBubbleSet() {
        int ray, index = 0, type;
        Vertex v;
        Coordinate coord;

        //calculating members
        Rectangle2D.Double[] members = new Rectangle2D.Double[vertex_id.size() + this.fake_vertex.size()];
        for (int i = 0; i < this.vertex_id.size(); i++) {
            v = graph.getVertexById(vertex_id.get(i));
            ray = v.getRay();
            members[i] = new Rectangle2D.Double(v.getX() - ray, v.getY() - ray, ray * 2, ray * 2);
        }
        ray = Vertex.getRayBase();
        for (int i = 0; i < this.fake_vertex.size(); i++) {
            coord = fake_vertex.get(i);
            members[vertex_id.size() + i] = new Rectangle2D.Double(coord.x, coord.y, ray * 2, ray * 2);
        }

        //calculating non-members
        TIntObjectHashMap<Vertex> vertex_clone = new TIntObjectHashMap<>();
        TIntObjectIterator<Vertex> iterator = graph.getVertex().iterator();
        while (iterator.hasNext()) {
            iterator.advance();
            vertex_clone.put(iterator.key(), iterator.value());
        }
        for (int i = 0; i < this.vertex_id.size(); i++) {
            vertex_clone.remove(vertex_id.get(i));
        }
        for (TIntIterator it = vertex_next_added.iterator(); it.hasNext();) {
            vertex_clone.remove(it.next());
        }
        Rectangle2D.Double[] nonmembers = new Rectangle2D.Double[vertex_clone.size()];
        iterator = vertex_clone.iterator();
        while (iterator.hasNext()) {
            iterator.advance();
            v = iterator.value();
            ray = v.getRay();
            nonmembers[index] = new Rectangle2D.Double(v.getX() - ray, v.getY() - ray, ray * 2, ray * 2);
            index++;
        }

        // using bubble set outlines
        SetOutline setOutline = new BubbleSet(BubbleSet.DEFAULT_MAX_ROUTING_ITERATIONS, BubbleSet.DEFAULT_MAX_MARCHING_ITERATIONS,
                BubbleSet.DEFAULT_PIXEL_GROUP, BubbleSet.DEFAULT_EDGE_R0, BubbleSet.DEFAULT_EDGE_R1, 4,
                BubbleSet.DEFAULT_NODE_R1, 4, BubbleSet.DEFAULT_SKIP);
        AbstractShapeGenerator shapeGenerator = new BSplineShapeGenerator(setOutline);
        shapeGenerator.setRadius(0);
        Shape shape = shapeGenerator.createShapeFor(members, nonmembers);

        //calculating the JTS geometry
        double[] coords = new double[6];
        ArrayList<Coordinate> aux = new ArrayList<>();
        GeometryFactory geometryFactory = new GeometryFactory();
        PathIterator pathIterator = shape.getPathIterator(null);
        while (!pathIterator.isDone()) {
            type = pathIterator.currentSegment(coords);
            if (type != PathIterator.SEG_CLOSE) {
                aux.add(new Coordinate(coords[0], coords[1]));
            }
            pathIterator.next();
        }

        this.geom = geometryFactory.createPolygon(geometryFactory.createLinearRing(aux.toArray(new Coordinate[aux.size()])), null);

        return new GeneralPath(shape);
    }

    private GeneralPath calcConvexPolygon() {
        Coordinate[] coords = new Coordinate[vertex_id.size() + this.fake_vertex.size()];
        for (int i = 0; i < this.vertex_id.size(); i++) {
            coords[i] = new Coordinate(graph.getVertexById(vertex_id.get(i)).getX(), graph.getVertexById(vertex_id.get(i)).getY());

        }
        for (int i = 0; i < this.fake_vertex.size(); i++) {
            coords[vertex_id.size() + i] = new Coordinate(fake_vertex.get(i).x, fake_vertex.get(i).y);
        }

        ConvexHull convexHull = new ConvexHull(coords, new GeometryFactory());
        geom = convexHull.getConvexHull().buffer(10, 8);
        this.centroid = geom.getCentroid();
        coords = geom.getCoordinates();
        GeneralPath convex_polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, coords.length);
        convex_polygon.moveTo(coords[0].x, coords[0].y);
        for (int i = 1; i < coords.length; i++) {
            convex_polygon.lineTo(coords[i].x, coords[i].y);
        }
        convex_polygon.closePath();
        return convex_polygon;
    }

    public ArrayList<Vertex> getVertexList() {
        ArrayList<Vertex> vertex_list = new ArrayList<>(vertex_id.size());
        for (int i = 0; i < this.vertex_id.size(); i++) {
            vertex_list.add(graph.getVertexById(this.vertex_id.get(i)));
        }
        return vertex_list;
    }

    public ArrayList<Coordinate> getFakeVertexList() {
        return this.fake_vertex;
    }

    public TIntArrayList getVertexIdList() {
        return this.vertex_id;
    }

    public int getAverageYearOfDocuments() {
        float sum = 0f;
        for (Vertex v : this.getVertexList()) {
            sum += v.getPublishedYear();
        }
        return Math.round(sum / this.vertex_id.size());
    }

    public int size() {
        return this.vertex_id.size();
    }

    public TemporalGraph getTemporalGraph() {
        return this.graph;
    }

    public double getTopicWeight(int index) {
        return this.topicsWeights.get(index);
    }

    public void clear() {
        this.vertex_id.clear();
        this.id = -1;
        this.relation = 0;
        this.polygon = null;
        this.selected = false;
        this.terms.clear();
        this.topicsWeights.clear();
        this.minvalue_topictag = Double.MAX_VALUE;
        this.maxvalue_topictag = -1.0;
        this.graph = null;
        this.corpus = null;
    }

    public boolean contains(com.vividsolutions.jts.geom.Point p) {
        return this.geom.contains(p);
    }

    public double distanceTo(Vertex aux) {
        double result = Double.MAX_VALUE;
        double value;
        for (int i = 0; i < this.vertex_id.size(); i++) {
            value = graph.getVertexById(vertex_id.get(i)).distanceTo(aux);
            if (value < result) {
                result = value;
            }
        }
        return result;
    }

//    protected Rectangle calcRect() {
//        Rectangle rect = new Rectangle();
//
//        if (vertex.size() > 0) {
//            int maxX = (int) vertex.get(0).getX();
//            int minX = (int) vertex.get(0).getX();
//            int maxY = (int) vertex.get(0).getY();
//            int minY = (int) vertex.get(0).getY();
//
//            for (int v = 1; v < vertex.size(); v++) {
//                int x = (int) vertex.get(v).getX();
//                int y = (int) vertex.get(v).getY();
//
//                if (x > maxX) {
//                    maxX = x;
//                } else if (x < minX) {
//                    minX = x;
//                }
//
//                if (y > maxY) {
//                    maxY = y;
//                } else if (y < minY) {
//                    minY = y;
//                }
//            }
//
//            rect.x = minX - Vertex.getRayBase() - 2;
//            rect.y = minY - Vertex.getRayBase() - 2;
//            rect.width = maxX - minX + Vertex.getRayBase() * 2 + 4;
//            rect.height = maxY - minY + Vertex.getRayBase() * 2 + 4;
//            return rect;
//        } else {
//            rect.x = -1;
//            rect.y = -1;
//            rect.width = 0;
//            rect.height = 0;
//            return rect;
//        }
//    }
    public abstract void createTopic();
}
