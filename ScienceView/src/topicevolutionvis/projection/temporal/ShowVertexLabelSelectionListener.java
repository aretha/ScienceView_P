/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.projection.temporal;

import gnu.trove.list.array.TIntArrayList;
import topicevolutionvis.graph.TemporalGraph;
import topicevolutionvis.projection.temporal.listeners.VertexSelectionListener;
import topicevolutionvis.view.TemporalProjectionViewer;

/**
 *
 * @author barbosaa
 */
public class ShowVertexLabelSelectionListener extends VertexSelectionListener {

    public ShowVertexLabelSelectionListener(TemporalProjectionViewer panel) {
        super(panel);
        this.color = java.awt.Color.BLUE;
    }

    @Override
    public void vertexSelected(TemporalGraph graph, Object param, TIntArrayList vertex) {
        for (int i = 0; i < vertex.size(); i++) {
            graph.getVertexById(vertex.get(i)).setShowLabel(true);
        }
    }
}
