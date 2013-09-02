/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.graph;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import topicevolutionvis.projection.temporal.TemporalProjection;
import topicevolutionvis.topic.Topic;
import topicevolutionvis.view.TemporalProjectionViewer;

/**
 *
 * @author Aretha
 */
public class TemporalGraph implements Cloneable {

    private int year;
    private int intermediate_index = 0;
    private double min_x = Double.MAX_VALUE, max_x = Double.MIN_VALUE, min_y = Double.MAX_VALUE, max_y = Double.MIN_VALUE;
    private TemporalProjection projection;
    private ArrayList<Topic> topics = new ArrayList<>();
    private ArrayList<Topic> fake_topics = new ArrayList<>();
    protected ArrayList<String> titles = new ArrayList<>();
    protected TIntObjectHashMap<Vertex> vertex = new TIntObjectHashMap<>();
    public ArrayList<LineSegment> lines_segments = new ArrayList<>();
    private double zoom_rate = 1.0d;

    public TemporalGraph(TemporalProjection projection, int year, int intermediate_index) {
        this.projection = projection;
        this.year = year;
        this.intermediate_index = intermediate_index;
    }

    @Override
    public boolean equals(Object aThat) {
        if (aThat instanceof TemporalGraph && (this.year == ((TemporalGraph) aThat).getYear())
                && (this.intermediate_index == ((TemporalGraph) aThat).getYear())) {
            return true;
        }
        return false;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        TemporalGraph temporalGraph = new TemporalGraph(this.projection, this.year, this.intermediate_index);
        temporalGraph.min_x = this.min_x;
        temporalGraph.max_x = this.max_x;
        temporalGraph.min_y = this.min_y;
        temporalGraph.max_y = this.max_y;
        temporalGraph.titles = new ArrayList<>(this.titles);
        temporalGraph.topics = new ArrayList<>();
        for (Topic t : this.topics) {
            Topic newTopic = (Topic) t.clone();
            newTopic.setTemporalGraph(temporalGraph);
            temporalGraph.topics.add(newTopic);
        }
        TIntObjectIterator<Vertex> iterator = vertex.iterator();
        while (iterator.hasNext()) {
            iterator.advance();
            Vertex v = iterator.value();
            temporalGraph.vertex.put(v.getId(), (Vertex) v.clone());
        }
        return temporalGraph;
    }

    public void setZoomRate(double zoom_rate) {
        this.zoom_rate = zoom_rate;
    }

    public double getZoomRate() {
        return this.zoom_rate;
    }

    public synchronized void addTopic(Topic topic) {
        this.topics.add(topic);
    }

    public synchronized void addFakeTopic(Topic topic) {
        this.fake_topics.add(topic);
    }

    public synchronized ArrayList<Topic> getTopics() {
        Collections.sort(topics, new Comparator<Topic>() {
            @Override
            public int compare(Topic o1, Topic o2) {
                return Integer.compare(o1.getId(), o2.getId());
            }
        });
        return this.topics;
    }

    public void unselectAllTopics() {
        for (Topic t : topics) {
            t.setSelected(false);
        }
    }

    public void setVertex(TIntObjectHashMap<Vertex> vertex) {
        this.vertex = vertex;
        TIntObjectIterator<Vertex> iterator = vertex.iterator();
        while (iterator.hasNext()) {
            iterator.advance();
            Vertex v = iterator.value();
            if (projection.getMaxx() < v.getX()) {
                projection.setMaxx(v.getX());
            }
            if (min_x > v.getX()) {
                min_x = v.getX();
            }
            if (max_x < v.getX()) {
                max_x = v.getX();
            }
            if (min_y > v.getY()) {
                min_y = v.getY();
            }
            if (max_y < v.getY()) {
                max_y = v.getY();
            }

            if (projection.getMinx() > v.getX()) {
                projection.setMinx(v.getX());
            }

            if (projection.getMaxy() < v.getY()) {
                projection.setMaxy(v.getY());
            }

            if (projection.getMiny() > v.getY()) {
                projection.setMiny(v.getY());
            }
        }
    }

    public int getYear() {
        return this.year;
    }

    public void normalizeVertex(double begin, double end) {
        double endX = (projection.getMaxx() - projection.getMinx()) * end;


        if (projection.getMaxy() != projection.getMiny()) {
            endX = ((projection.getMaxx() - projection.getMinx()) * end) / (projection.getMaxy() - projection.getMiny());
        }
        TIntObjectIterator<Vertex> iterator = vertex.iterator();
        while (iterator.hasNext()) {
            iterator.advance();
            Vertex v = iterator.value();
            if (projection.getMaxx() != projection.getMinx()) {
                v.setX((((v.getX() - projection.getMinx()) / (projection.getMaxx() - projection.getMinx()))
                        * (endX - begin)) + begin);
            } else {
                v.setX(begin);
            }

            if (projection.getMaxy() != projection.getMiny()) {
                v.setY(((((v.getY() - projection.getMiny()) / (projection.getMaxy() - projection.getMiny()))
                        * (end - begin)) + begin));

            } else {
                v.setY(begin);
            }
        }
    }

    public void normalizeVertexIndependent(double begin, double end) {
        double maxX = vertex.get(0).getX();
        double minX = vertex.get(0).getX();
        double maxY = vertex.get(0).getY();
        double minY = vertex.get(0).getY();

        //Encontra o maior e menor valores para X e Y
        for (TIntObjectIterator<Vertex> it = vertex.iterator(); it.hasNext();) {
            it.advance();
            Vertex v = it.value();
            if (maxX < v.getX()) {
                maxX = v.getX();
            } else {
                if (minX > v.getX()) {
                    minX = v.getX();
                }
            }

            if (maxY < v.getY()) {
                maxY = v.getY();
            } else {
                if (minY > v.getY()) {
                    minY = v.getY();
                }
            }
        }

        ///////Fazer a largura ficar proporcional a altura
        double endX = ((maxX - minX) * end);
        if (maxY != minY) {
            endX = ((maxX - minX) * end) / (maxY - minY);
        }
        //////////////////////////////////////////////////

        //Normalizo
        for (TIntObjectIterator<Vertex> it = vertex.iterator(); it.hasNext();) {
            it.advance();
            Vertex v = it.value();
            if (maxX != minX) {
                v.setX((((v.getX() - minX) / (maxX - minX))
                        * (endX - begin)) + begin);
            } else {
                v.setX(begin);
            }

            if (maxY != minY) {
                v.setY(((((v.getY() - minY) / (maxY - minY))
                        * (end - begin)) + begin));
            } else {
                v.setY(begin);
            }

        }
    }

    /**
     * Return the size of the graph, i.e., the maximum height and width.
     *
     * @return The size of the graph
     */
    public java.awt.Dimension getSize() {
        if (this.vertex.size() > 0) {
            int w = (int) (max_x + (Vertex.getRayBase() * 5)) + 350;
            int h = (int) (max_y + (Vertex.getRayBase() * 5));
            return new Dimension(w, h);
        } else {
            return new Dimension(0, 0);
        }
    }

    public void setMaxX(double max_x) {
        this.max_x = max_x;
    }

    public void setMaxY(double max_y) {
        this.max_y = max_y;
    }

    /**
     * Returns the vertex which occupies the (x,y) position. If this position is
     * not ocuppied by any vertex, returns null.
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @return The v which occupy the (x,y) position
     */
    public Vertex getVertexByPosition(int x, int y) {
        TIntObjectIterator<Vertex> iterator = vertex.iterator();
        while (iterator.hasNext()) {
            iterator.advance();
            Vertex v = iterator.value();
            if (v.isInside(x, y)) {
                return v;
            }
        }
        return null;
    }

    public void drawFakeTopic(Image image, Graphics2D g2, TemporalProjectionViewer.ViewPanel viewer, boolean highquality) {
        if (image != null) {
            g2 = (Graphics2D) image.getGraphics();
        }

        if (highquality) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        //Draw line segments
        g2.setColor(Color.YELLOW);
        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_IN, 1.0f));
        g2.setStroke(new BasicStroke(2.0f));
        for (LineSegment line_segment : this.lines_segments) {
            g2.drawLine((int) line_segment.p0.x, (int) line_segment.p0.y, (int) line_segment.p1.x, (int) line_segment.p1.y);
        }

        g2.setColor(Color.BLACK);
        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_IN, 1.0f));
        g2.setStroke(new BasicStroke(2.0f));
        for (Topic t : this.topics) {
            for (Coordinate c : t.getFakeVertexList()) {
                g2.drawOval((int) c.x - 6, (int) c.y - 6, 12, 12);
            }
        }

        float[] dash = {10.0f, 10.0f};
        g2.setColor(Color.YELLOW);
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, dash, 0.0f));
        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_IN, 1.0f));
        for (Topic t : this.fake_topics) {

            g2.draw(t.polygon);
        }
    }

    /**
     * Draw the graph on a graphical device.
     *
     * @param connectivity The connectivity to be drawn
     * @param g2 The graphical device
     */
    public void draw(Connectivity connectivity, Image image, boolean highquality, float low_value_edge, float high_value_edges, TIntArrayList selected_ids) {
//        boolean globalsel = false;
        TIntObjectIterator<Vertex> iterator = vertex.iterator();
//        while (iterator.hasNext()) {
//            iterator.advance();
//            if (iterator.value().isSelected()) {
//                globalsel = true;
//            }
//        }

//        if (connectivity != null) {
//            ArrayList<Edge> edges;
//            if (!connectivity.isWeighted()) {
//                edges = connectivity.getEdges();
//            } else {
//                edges = connectivity.getEdgesWithinRange(low_value_edge, high_value_edges);
//            } //Draw each edges of the graph
//            for (Edge edge : edges) {
//                edge.draw(image, null, globalsel, highquality, connectivity.getName(), this);
//            }
//        }

        //Draw each vertice of the graph
        iterator = vertex.iterator();
        if (selected_ids != null) {
            while (iterator.hasNext()) {
                iterator.advance();
                Vertex v = iterator.value();
                if (!selected_ids.contains(v.getId())) {
                    if (!v.isSelected()) {
                        v.draw(image, null, false, highquality);
                    }
                }
            }
        } else {
            while (iterator.hasNext()) {
                iterator.advance();
                iterator.value().draw(image, null, false, highquality);
            }
        }
    }

    /**
     * Returns the v which composes the graph.
     *
     * @return The v of the graph
     */
    public TIntObjectHashMap<Vertex> getVertex() {
        return this.vertex;
    }

    public Vertex getVertexById(int id) {
        return this.vertex.get(id);
    }

    public void getNeighbors(ArrayList<Vertex> neighborsVertex, ArrayList<Edge> neighborsEdges, Connectivity connectivity, Vertex vertex, int depth, float low_weight, float high_weight) {

        if (connectivity != null) {
            ArrayList<Edge> edges;

            if (!connectivity.isWeighted()) {
                edges = connectivity.getEdges();
            } else {
                edges = connectivity.getEdgesWithinRange(low_weight, high_weight);
            }

            ArrayList<Vertex> visitedVertex = new ArrayList<>();
            ArrayList<Vertex> vertexToVisit = new ArrayList<>();
            vertexToVisit.add(vertex);

            for (int i = 0; i < depth; i++) {
                for (int j = 0; j < vertexToVisit.size(); j++) {
                    Vertex v = vertexToVisit.get(j);
                    for (Edge e : edges) {
                        Vertex v_target = this.getVertexById(e.getTarget()), v_source = this.getVertexById(e.getSource());
                        if (e.getSource() == v.getId() && !neighborsVertex.contains(v_target)) {
                            neighborsVertex.add(v_target);
                            visitedVertex.add(v_target);
                            if (neighborsEdges != null && !neighborsEdges.contains(e)) {
                                neighborsEdges.add(e);
                            }
                        }
                        if (e.getTarget() == v.getId() && !neighborsVertex.contains(v_source)) {
                            neighborsVertex.add(v_source);
                            visitedVertex.add(v_source);
                            if (neighborsEdges != null && !neighborsEdges.contains(e)) {
                                neighborsEdges.add(e);
                            }
                        }
                    }
                }
                vertexToVisit = visitedVertex;
                visitedVertex = new ArrayList<>();
            }
            neighborsVertex.remove(vertex);
        }
    }

    public int getTitleIndex(String name) {
        return this.titles.indexOf(name);
    }

    public int addTitle(String name) {
        if (!this.titles.contains(name)) {
            this.titles.add(name);
        }
        return this.titles.indexOf(name);
    }

    public void exportCorpus(String newCorpusName, ArrayList<Vertex> vertex) {
        if (this.projection.getDatabaseCorpus() != null) {
            ZipOutputStream zout = null;



            try {
                FileOutputStream dest = new FileOutputStream(newCorpusName);
                zout = new ZipOutputStream(new BufferedOutputStream(dest));
                zout.setMethod(ZipOutputStream.DEFLATED);



                for (Vertex v : vertex) {
                    if (v.getId() != -1) {
                        ZipEntry entry = new ZipEntry(String.valueOf(v.getId()));
                        zout.putNextEntry(entry);

                        String filecontent = this.projection.getDatabaseCorpus().getFullContent(v.getId());
                        zout.write(filecontent.getBytes(), 0, filecontent.length());


                    }
                }

            } catch (IOException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            } finally {

                if (zout != null) {
                    try {
                        zout.flush();
                        zout.finish();
                        zout.close();



                    } catch (IOException ex) {
                        Logger.getLogger(TemporalGraph.class.getName()).log(Level.SEVERE, null, ex);
                    }


                }
            }
        }
    }

//    public void removeVertex(ArrayList<Vertex> vertex) {
//        for (int i = 0; i
//                < vertex.size(); i++) {
//            Vertex v = vertex.get(i);
//
//            //remove the v
//            this.vertex.remove(v.getId());
//            for (Connectivity con : this.connectivities) {
//                if (con != null) {
//                    ArrayList<Edge> edges = con.getEdges();
//                    if (edges != null) {
//                        for (int j = 0; j < edges.size(); j++) {
//                            if (edges.get(j).getSource() == v.getId()
//                                    || edges.get(j).getTarget() == v.getId()) {
//                                con.getEdges().remove(j);
//                                j--;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        //adjust the v ids
//        for (int i = 0; i
//                < this.vertex.size(); i++) {
//            this.vertex.get(i).setId(i);
//        }
//    }
    public void perturb() {
        Random rand = new Random(7);
        double maxx = Double.NEGATIVE_INFINITY;
        double minx = Double.POSITIVE_INFINITY;
        double maxy = Double.NEGATIVE_INFINITY;
        double miny = Double.POSITIVE_INFINITY;

        TIntObjectIterator<Vertex> iterator = vertex.iterator();
        while (iterator.hasNext()) {
            iterator.advance();
            Vertex v = iterator.value();
            if (maxx < v.getX()) {
                maxx = v.getX();
            }
            if (minx > v.getX()) {
                minx = v.getX();
            }
            if (maxy < v.getY()) {
                maxy = v.getY();
            }
            if (miny > v.getY()) {
                miny = v.getY();
            }
        }

        double diffx = (maxx - minx) / 1000;
        double diffy = (maxy - miny) / 1000;

        for (int i = 0; i
                < vertex.size(); i++) {
            vertex.get(i).setX(vertex.get(i).getX() + diffx * rand.nextFloat());
            vertex.get(i).setY(vertex.get(i).getY() + diffy * rand.nextFloat());

        }
    }
}