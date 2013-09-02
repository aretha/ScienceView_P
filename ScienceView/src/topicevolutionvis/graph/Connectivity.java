package topicevolutionvis.graph;

import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.ArrayList;
import java.util.Collections;
import topicevolutionvis.util.Pair;

/**
 * This class represents the graph connectivity.
 *
 * @author Fernando Vieira Paulovich
 */
public class Connectivity implements java.io.Serializable {

    private boolean directed = false;
    private boolean weighted = false;
    private float min_weight, max_weight;

    /**
     * Creates a new instance of Connectivity
     *
     * @param name The connectivity's name
     */
    public Connectivity(ConnectivityType type, boolean directed, boolean weighted) {
        this.type = type;
        this.directed = directed;
        this.weighted = weighted;
    }

    public boolean isWeighted() {
        return this.weighted;
    }

    public boolean isDirected() {
        return this.directed;
    }

    public ArrayList<Edge> getEdgesWithinRange(float low_value, float high_value) {
        ArrayList<Edge> new_edges = new ArrayList<>();
        for (Edge e : this.edges) {
            if (e.weight >= low_value && e.weight <= high_value) {
                new_edges.add(e);
            }
        }
        return new_edges;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public void setEdges(ArrayList<Edge> edges) {
        if (edges != null) {
            this.edges = edges;
        }
    }

    public String getName() {
        return type.toString();
    }

    public ConnectivityType getType() {
        return this.type;
    }

    public void setMinWeight(float min_weight) {
        this.min_weight = min_weight;
    }

    public void setMaxWeight(float max_weight) {
        this.max_weight = max_weight;
    }

    public float getMinWeight() {
        return this.min_weight;
    }

    public float getMaxWeight() {
        return this.max_weight;
    }

    public Connectivity create(TIntObjectHashMap< Vertex> vertexH, Pair[][] neighborhood) {
        if (neighborhood != null) {
            max_weight = Float.MIN_VALUE;
            min_weight = Float.MAX_VALUE;


            ArrayList<Vertex> vertex = new ArrayList<>(vertexH.valueCollection());

            for (int i = 0; i < neighborhood.length; i++) {
                for (int j = 0; j < neighborhood[i].length; j++) {
                    if (neighborhood[i][j].value < min_weight) {
                        min_weight = (float) neighborhood[i][j].value;
                    }
                    if (neighborhood[i][j].value > max_weight) {
                        max_weight = (float) neighborhood[i][j].value;
                    }
                    if (!directed) {
                        edges.add(new Edge((float) neighborhood[i][j].value, vertex.get(i).getId(),
                                vertexH.get(neighborhood[i][j].index).getId()));
                    } else {
                        edges.add(new Arrow((float) neighborhood[i][j].value, vertex.get(i).getId(),
                                vertexH.get(neighborhood[i][j].index).getId()));
                    }
                }
            }

            if (!directed) {
                edges = Connectivity.compress(edges);
            }
            this.setEdges(edges);
        }

        return this;
    }

    public static ArrayList<Edge> compress(ArrayList<Edge> edges) {
        if (edges.size() > 0) {
            Collections.sort(edges);
            ArrayList<Edge> edges_aux = edges;
            edges = new ArrayList<>();

            int n = 0;
            edges.add(edges_aux.get(0));
            for (int i = 1; i < edges_aux.size(); i++) {
                if (!edges_aux.get(n).equals(edges_aux.get(i))) {
                    edges.add(edges_aux.get(i));
                    n = i;
                }
            }
        }

        return edges;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Connectivity) {
            return this.type.equals(((Connectivity) obj).type);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3 + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return this.type.toString();
    }
    private ConnectivityType type = ConnectivityType.NONE; //The connectivity name    
    private ArrayList<Edge> edges = new ArrayList<>(); //The edges which composes the connectivity
}
