/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.projection.temporal.listeners;

import topicevolutionvis.projection.temporal.ShowVertexLabelSelectionListener;
import topicevolutionvis.view.TemporalProjectionViewer;

/**
 *
 * @author Aretha
 */
public class VertexSelectionFactory {

    private static VertexSelectionListener instance;

    public enum SelectionType {

        CREATE_TOPIC, VIEW_CONTENT, FOLLOW_GROUP, SHOW_VERTEX_LABEL, SELECT_GRAPH
    }

    public static VertexSelectionListener getInstance(TemporalProjectionViewer panel, SelectionType type) {
        if (type == SelectionType.CREATE_TOPIC) {
            instance = new CreateTopicSelectionListener(panel);
        } else if (type == SelectionType.VIEW_CONTENT) {
            instance = new ViewContentSelectionListener(panel);
        } else if (type == SelectionType.FOLLOW_GROUP) {
            instance = new FollowGroupSelectionListener(panel);
        } else if (type == SelectionType.SHOW_VERTEX_LABEL) {
            instance = new ShowVertexLabelSelectionListener(panel);
        } else if (type == SelectionType.SELECT_GRAPH) {
            instance = new SelectGraphSelectionListener(panel);
        }
        return instance;
    }
}
