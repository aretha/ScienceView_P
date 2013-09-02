/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.topic;

import com.vividsolutions.jts.geom.Coordinate;
import gnu.trove.list.array.TIntArrayList;
import java.util.ArrayList;
import topicevolutionvis.graph.TemporalGraph;
import topicevolutionvis.projection.temporal.TemporalProjection;
import topicevolutionvis.topic.TopicData.TopicType;

/**
 *
 * @author Aretha
 */
public class TopicFactory {

    public static Topic getInstance(TemporalProjection projection, TemporalGraph graph, TIntArrayList selectedVertices) {
        if (projection.getTopicData().getTopicType() == TopicType.COVARIANCE) {
            return new CovarianceTopic(selectedVertices, projection, graph);
        } else if (projection.getTopicData().getTopicType() == TopicType.PCA) {
            return new PCATopic(selectedVertices, projection, graph);
        } else if (projection.getTopicData().getTopicType() == TopicType.LDA) {
            return new LDATopic(selectedVertices, projection, graph, projection.getProjectionData().getLDAMatrices());
        }
        return null;
    }

    public static Topic getInstance(TemporalProjection projection, TemporalGraph graph, TIntArrayList selectedVertices, ArrayList<Coordinate> fake_vertex) {
        if (fake_vertex == null || fake_vertex.isEmpty()) {
            return TopicFactory.getInstance(projection, graph, selectedVertices);
        } else {
            if (projection.getTopicData().getTopicType() == TopicType.COVARIANCE) {
                return new CovarianceTopic(selectedVertices, fake_vertex, projection, graph);
            } else if (projection.getTopicData().getTopicType() == TopicType.PCA) {
                return new PCATopic(selectedVertices, fake_vertex, projection, graph);
            } else if (projection.getTopicData().getTopicType() == TopicType.LDA) {
                return new LDATopic(selectedVertices, fake_vertex, projection, graph, projection.getProjectionData().getLDAMatrices());
            }
            return null;
        }
    }
}
