/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.projection.temporal.listeners;

import gnu.trove.list.array.TIntArrayList;
import topicevolutionvis.graph.TemporalGraph;
import topicevolutionvis.projection.temporal.TemporalProjection;
import topicevolutionvis.topic.Topic;
import topicevolutionvis.topic.TopicFactory;
import topicevolutionvis.view.ScienceViewMainFrame;
import topicevolutionvis.view.TemporalProjectionViewer;

/**
 *
 * @author Aretha
 */
public class CreateTopicSelectionListener extends VertexSelectionListener {

    private Topic topic = null;

    public CreateTopicSelectionListener(TemporalProjectionViewer panel) {
        super(panel);
        this.color = java.awt.Color.YELLOW;
    }

    @Override
    public void vertexSelected(TemporalGraph graph, Object param, TIntArrayList vertex) {
        if (vertex.size() > 0) {
            TemporalProjection projection = panel.getTemporalProjection();
            if (graph == null) {
                graph = panel.getGraph();
            }
            this.topic = TopicFactory.getInstance(projection, graph, vertex);
            this.topic.calcPolygon();
            this.topic.createTopic();
            graph.addTopic(topic);
            ScienceViewMainFrame.getInstance().updateTopicsTree();
            panel.updateImage();
        }
    }

    public Topic getLastTopic() {
        return this.topic;
    }
}
