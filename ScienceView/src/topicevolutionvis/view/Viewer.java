/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.view;

import java.awt.Font;
import topicevolutionvis.graph.Connectivity;
import topicevolutionvis.graph.Scalar;
import topicevolutionvis.graph.TemporalGraph;
import topicevolutionvis.projection.temporal.listeners.VertexSelectionFactory.SelectionType;
import topicevolutionvis.view.color.ColorTable;

/**
 *
 * @author Aretha
 */
public abstract class Viewer extends javax.swing.JInternalFrame {

    protected boolean graphChanged = false;
    protected boolean vertexLabelVisible = true;
    protected ScienceViewMainFrame mainview;
    protected int id_viewer;
    protected static int avaiableId = 1;
    protected static SelectionType type = SelectionType.CREATE_TOPIC;
    protected static boolean highlightTopic = false;
    protected boolean highQualityRender = true;

    public Viewer(ScienceViewMainFrame topicevolview) {
        this.mainview = topicevolview;

        this.id_viewer = Viewer.avaiableId;
        Viewer.avaiableId++;

    }

    public abstract TemporalGraph getGraph();

    /**
     * Color the nodes according to a scalar.
     * @param scalar The scalar used to color the nodes.
     */
    public abstract void colorAs(Scalar scalar);

    public abstract void resizeAs(Scalar scalar);

    public void setGraphChanged(boolean graphChanged) {
        if (graphChanged) {
            if (!this.getTitle().endsWith("*")) {
                this.setTitle(this.getTitle() + "*");
            }
        } else {
            if (this.getTitle().endsWith("*")) {
                this.setTitle(this.getTitle().substring(0, this.getTitle().length() - 1));
            }
        }

        this.graphChanged = graphChanged;
    }

    public ScienceViewMainFrame getTopicEvolutionView() {
        return this.mainview;
    }

    /**
     * Updates the list of scalars on the graphical interface. Also
     * selects the scalar passed as argument. If null is passed as argument,
     * only the list of scalars is update.
     * @param scalar The scalar to be selected.
     */
    public abstract void updateScalars(Scalar scalar);

    public abstract void updateScalars();

    public abstract void updateConnectivities();

    public abstract Connectivity getCurrentConnectivity();

    /**
     * Clean the image and re-create it.
     */
    public abstract void updateImage();

    /**
     * Updates the list of tiles on the graphical interface.
     */
    public abstract void updateTitles();

    /**
     * Returns the scalar currently used on the graphical.
     * @return The current scalar.
     */
    public abstract Scalar getCurrentScalar();

    /**
     * Returns the color table associated with this window.
     * @return The color table.
     */
    public abstract ColorTable getColorTable();

    /**
     * Clean the topics on the projection.
     */
    public abstract void cleanTopics();

    /**
     * Clean the selected nodes.
     * @param cleanVertex Set if to true to clean the vertices topics,
     * and false otherwise.
     */
    public abstract void cleanSelection(boolean cleanVertex);

    public static SelectionType getType() {
        return type;
    }

    public static void setType(SelectionType type) {
        Viewer.type = type;
    }

    public static boolean isHighlightTopic() {
        return highlightTopic;
    }

    public static void setHighlightTopic(boolean highlightTopic) {
        Viewer.highlightTopic = highlightTopic;
    }

    public abstract Font getViewerFont();

    public abstract void setViewerFont(Font font);
}
