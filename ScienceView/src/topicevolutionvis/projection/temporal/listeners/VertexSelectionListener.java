/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.projection.temporal.listeners;

import gnu.trove.list.array.TIntArrayList;
import topicevolutionvis.graph.TemporalGraph;
import topicevolutionvis.view.TemporalProjectionViewer;

/**
 *
 * @author Aretha
 */
public abstract class VertexSelectionListener {

    /** Creates a new instance of VertexSelectionListener
     * @param panel */
    public VertexSelectionListener(TemporalProjectionViewer panel) {
        this.panel = panel;
    }

    public java.awt.Color getColor() {
        return this.color;
    }

    public abstract void vertexSelected(TemporalGraph graph,Object param, TIntArrayList vertex);
    protected TemporalProjectionViewer panel;
    protected java.awt.Color color = java.awt.Color.BLUE;
}
