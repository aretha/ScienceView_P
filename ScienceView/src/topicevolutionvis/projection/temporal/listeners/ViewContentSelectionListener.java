/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.projection.temporal.listeners;

import gnu.trove.list.array.TIntArrayList;
import topicevolutionvis.graph.TemporalGraph;
import topicevolutionvis.view.MultipleDocumentViewer;
import topicevolutionvis.view.TemporalProjectionViewer;

/**
 *
 * @author Aretha
 */
public class ViewContentSelectionListener extends VertexSelectionListener {

    /**
     * Creates a new instance of ViewContentSelectionListener
     * @param panel
     */
    public ViewContentSelectionListener(TemporalProjectionViewer panel) {
        super(panel);
        this.color = java.awt.Color.GREEN;
    }

    @Override
    public void vertexSelected(TemporalGraph graph, Object param, TIntArrayList vertex) {
        if (vertex.size() > 0) {
            int[] documents = new int[vertex.size()];
            for (int i = 0; i < vertex.size(); i++) {
                documents[i] = vertex.get(i);
            }
            (new MultipleDocumentViewer(documents, panel.getCorpus())).display();
        }
    }
}
