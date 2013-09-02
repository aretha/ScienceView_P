/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.datamining.network;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import topicevolutionvis.matrix.SparseMatrix;
import topicevolutionvis.projection.distance.Dissimilarity;
import topicevolutionvis.projection.distance.DistanceMatrix;

/**
 *
 * @author barbosaa
 */
public class SimilarityConnectivy {

    private DistanceMatrix dmat;

    public SimilarityConnectivy(SparseMatrix matrix, Dissimilarity diss) {
        try {
            dmat = new DistanceMatrix(matrix, diss);
        } catch (IOException ex) {
            Logger.getLogger(SimilarityConnectivy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    public Connectivity getSimilarityConnectivy(TIntObjectHashMap<Vertex> vertex) {
//        Pair[][] neighborhood = new Pair[vertex.size()][];
//        ArrayList<ArrayList<Pair>> neigh_aux_vertex = new ArrayList<>();
//        float dist;
//        TIntObjectIterator<Vertex> iterator = vertex.iterator();
//        for (int i = 0; i < vertex.size(); i++) {
//            iterator.advance();
//            neigh_aux_vertex.add(new ArrayList<Pair>());
//            for (int j = i + 1; j < vertex.size(); j++) {
//                dist = 1.0f - dmat.getDistance(vertex.get(i).getId(), vertex.get(j).getId());
//                if (dist > 0.2f) {
//                    neigh_aux_vertex.get(i).add(new Pair(vertex.get(j).getId(), dist));
//                }
//            }
//
//            neighborhood[i] = new Pair[neigh_aux_vertex.get(i).size()];
//            for (int j = 0; j < neighborhood[i].length; j++) {
//                neighborhood[i][j] = neigh_aux_vertex.get(i).get(j);
//            }
//        }
//
//        Connectivity con = new Connectivity(ConnectivityType.SIMILARITY, false, true);
//        con = con.create(vertex, neighborhood);
//        return con;
//    }
}
