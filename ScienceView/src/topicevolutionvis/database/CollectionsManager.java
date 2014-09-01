package topicevolutionvis.database;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class CollectionsManager {

    public static int getNextCollectionId() {
        int aux = -1;
        try {
            try (PreparedStatement stmt = SqlManager.getInstance().getSqlStatement("SELECT.MAX.IDCOLLECTION", -1, -1)) {
                ResultSet rs = stmt.executeQuery();
                rs.next();
                aux = rs.getInt(1) + 1;
            }

        } catch (IOException | SQLException ex) {
            Logger.getLogger(CollectionsManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return aux;
    }

    public static ArrayList<String> getCollections() {

        ArrayList<String> collections = new ArrayList<>();
        try {
            try (PreparedStatement stmt = SqlManager.getInstance().getSqlStatement("SELECT.COLLECTIONS", -1, -1); ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    collections.add(name);
                }
            }
            Collections.sort(collections);
        } catch (SQLException | IOException ex) {
            Logger.getLogger(CollectionsManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return collections;
    }

    public static int getCollectionId(String name) {
        int aux = -1;
        try {
            try (PreparedStatement stmt = SqlManager.getInstance().getSqlStatement("SELECT.COLLECTION.BY.NAME", -1, -1)) {
                stmt.setString(1, name);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        aux = rs.getInt(1);
                    }
                }
            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(CollectionsManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return aux;
    }

    public static boolean removeCollection(int id) throws IOException {
        int rows = 0;
        try {
            try (PreparedStatement stmt = SqlManager.getInstance().getSqlStatement("REMOVE.COLLECTION", -1, -1)) {
                stmt.setInt(1, id);
                rows = stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(CollectionsManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex.getMessage());
        }
        return (rows > 0);
    }
}
