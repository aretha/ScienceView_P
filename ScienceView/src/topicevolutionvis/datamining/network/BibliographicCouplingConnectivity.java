/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.datamining.network;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.ArrayList;
import topicevolutionvis.database.DatabaseCorpus;
import topicevolutionvis.graph.Connectivity;
import topicevolutionvis.graph.ConnectivityType;
import topicevolutionvis.graph.Vertex;
import topicevolutionvis.util.Pair;

/**
 *
 * @author Aretha
 */
public class BibliographicCouplingConnectivity {

    TIntObjectHashMap<TIntIntHashMap> neigh_aux;

    public BibliographicCouplingConnectivity(DatabaseCorpus corpus) {
        neigh_aux = corpus.getBibliographicCoupling();
    }

    public Connectivity getBibliographicCoupling(TIntObjectHashMap< Vertex> vertex) {
        Connectivity con = new Connectivity(ConnectivityType.BIBLIOGRAPHIC_COUPLING, false, true);
        int id_vertex;
        ArrayList<ArrayList<Pair>> neigh_aux_vertex = new ArrayList<>();
        TIntObjectIterator<Vertex> iterator = vertex.iterator();
        for (int i = 0; i < vertex.size(); i++) {
            iterator.advance();
            neigh_aux_vertex.add(new ArrayList<Pair>());
            id_vertex = iterator.key();
            for (TIntIntIterator it = neigh_aux.get(id_vertex).iterator(); it.hasNext();) {
                it.advance();
                int id_vertex2 = it.key();
                if (vertex.containsKey(id_vertex2)) {
                    neigh_aux_vertex.get(i).add(new Pair(id_vertex2, neigh_aux.get(id_vertex).get(id_vertex2)));
                }
            }
        }

        //creating the connectivity with the read neighborhood
        Pair[][] neighborhood = new Pair[vertex.size()][];
        for (int i = 0; i < neighborhood.length; i++) {

            neighborhood[i] = new Pair[neigh_aux_vertex.get(i).size()];
            for (int j = 0; j < neighborhood[i].length; j++) {
                neighborhood[i][j] = neigh_aux_vertex.get(i).get(j);
            }
        }

        con = con.create(vertex, neighborhood);
        return con;
    }
//    public Connectivity getBibliographicCoupling(ArrayList<Vertex> vertex) {
//
//        HashMap<Integer, Vertex> index = new HashMap<Integer, Vertex>();
//        for (Vertex v : vertex) {
//            index.put(v.getId(), v);
//        }
//
//        int id_vertex;
//        Pair[][] neighborhood = new Pair[vertex.size()][];
//        ArrayList<ArrayList<Pair>> neigh_aux_vertex = new ArrayList<ArrayList<Pair>>();
//        for (int i = 0; i < vertex.size(); i++) {
//
//            neigh_aux_vertex.add(new ArrayList<Pair>());
//            id_vertex = vertex.get(i).getId();
//            for (Integer id_vertex2 : neigh_aux.get(id_vertex).keySet()) {
//                if (index.containsKey(id_vertex2)) {
//                    neigh_aux_vertex.get(i).add(new Pair(id_vertex2, neigh_aux.get(id_vertex).get(id_vertex2).intValue()));
//                }
//            }
//
//            neighborhood[i] = new Pair[neigh_aux_vertex.get(i).size()];
//            for (int j = 0; j < neighborhood[i].length; j++) {
//                neighborhood[i][j] = neigh_aux_vertex.get(i).get(j);
//            }
//        }
//
//        Connectivity con = new Connectivity(PExConstants.BIBLIOGRAPHIC_COUPLING, false, true);
//        con = con.create(vertex, neighborhood);
//        return con;
//    }
}
