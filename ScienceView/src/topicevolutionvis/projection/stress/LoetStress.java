/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.projection.stress;

import java.util.ArrayList;
import java.util.TreeMap;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import topicevolutionvis.graph.TemporalGraph;
import topicevolutionvis.graph.Vertex;
import topicevolutionvis.matrix.SparseMatrix;
import topicevolutionvis.projection.distance.Dissimilarity;

/**
 *
 * @author barbosaa
 */
public class LoetStress extends Stress {

    private final XYSeriesCollection stress_series = new XYSeriesCollection();

    @Override
    public XYSeriesCollection calculate(SparseMatrix matrix, Dissimilarity diss, TreeMap<Integer, TemporalGraph> graphs) {
        double num, den, minr2, minrn, maxrn, maxr2, valuern, valuer2;

        Vertex v1, v2;
        ArrayList<Vertex> vertex_list;
        XYSeries static_stress = new XYSeries("Normalized Kruskal Stress");
        for (TemporalGraph graph : graphs.values()) {
            num = den = 0;
            vertex_list = new ArrayList<>(graph.getVertex().valueCollection());
            if (vertex_list.size() > 1) {
                maxrn = maxr2 = -1;
                minr2 = minrn = Double.MAX_VALUE;

                for (int i = 0; i < vertex_list.size(); i++) {
                    v1 = vertex_list.get(i);
                    for (int j = i + 1; j < vertex_list.size(); j++) {
                        v2 = vertex_list.get(j);
                        valuer2 = euclideanDistance(v1, v2);
                        valuern = diss.calculate(matrix.getRowWithId(v1.getId()), matrix.getRowWithId(v2.getId()));
                        if (valuern > maxrn) {
                            maxrn = valuern;
                        }
                        if (valuer2 > maxr2) {
                            maxr2 = valuer2;
                        }
                        if (valuer2 < minr2) {
                            minr2 = valuer2;
                        }
                        if (valuern < minrn) {
                            minrn = valuern;
                        }
                    }
                }

                for (int i = 0; i < vertex_list.size(); i++) {
                    v1 = vertex_list.get(i);
                    for (int j = i + 1; j < vertex_list.size(); j++) {
                        v2 = vertex_list.get(j);
                        valuer2 = (euclideanDistance(v1, v2) - minr2) / (maxr2 - minr2);
                        valuern = (diss.calculate(matrix.getRowWithId(v1.getId()), matrix.getRowWithId(v2.getId())) - minrn) / (maxrn - minrn);
                        num += Math.pow(valuern - valuer2, 2);
                        den += valuern * valuern;
                    }
                }

                static_stress.add(graph.getYear(), Math.sqrt(num / den));
            }
        }
        stress_series.addSeries(static_stress);

        XYSeries dynamic_stress = new XYSeries("Dynamic Stress");
        ArrayList<Integer> years = new ArrayList<>(graphs.keySet());
        int index_year = 0;
        double value;
        TemporalGraph next_graph;
        for (TemporalGraph graph : graphs.values()) {
            next_graph = graphs.get(years.get(index_year + 1));

            vertex_list = new ArrayList<>(graph.getVertex().valueCollection());
            value = 0;
            for (Vertex v : vertex_list) {
                v2 = next_graph.getVertexById(v.getId());
                value += (euclideanDistance(v, v2));

            }
            value = value / vertex_list.size();

            dynamic_stress.add(next_graph.getYear(), value);

            index_year++;
            if (index_year == years.size() - 1) {
                break;
            }

        }
        stress_series.addSeries(dynamic_stress);

        return stress_series;
    }
}
