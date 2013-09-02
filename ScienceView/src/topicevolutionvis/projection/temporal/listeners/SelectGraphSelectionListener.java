/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.projection.temporal.listeners;

import gnu.trove.list.array.TIntArrayList;
import java.util.ArrayList;
import topicevolutionvis.graph.TemporalGraph;
import topicevolutionvis.graph.Vertex;
import topicevolutionvis.view.TemporalProjectionViewer;

/**
 *
 * @author USER
 */
class SelectGraphSelectionListener extends VertexSelectionListener {

    /**
     * Creates a new instance of SelectGraphSelectionListener
     */
    public SelectGraphSelectionListener(TemporalProjectionViewer panel) {
        super(panel);
        this.color = java.awt.Color.BLUE;
    }

    @Override
    public void vertexSelected(TemporalGraph graph, Object param, TIntArrayList vertex) {
        ArrayList<Vertex> aux = new ArrayList<>();
        for (int i = 0; i < vertex.size(); i++) {
            aux.add(graph.getVertexById(vertex.get(i)));
        }
        panel.selectVertices(aux);
    }
}
