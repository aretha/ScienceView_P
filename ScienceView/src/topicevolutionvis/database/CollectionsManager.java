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

    public static ArrayList<String> getCollections() throws IOException {
        ArrayList<String> collections = new ArrayList<>();

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = SqlManager.getInstance().getSqlStatement("SELECT.COLLECTIONS", -1, -1);
                rs = stmt.executeQuery();

                while (rs.next()) {
                    String name = rs.getString("name");
                    collections.add(name);
                }
        } catch (SQLException ex) {
            Logger.getLogger(CollectionsManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }

                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CollectionsManager.class.getName()).log(Level.SEVERE, null, ex);
                throw new IOException(ex.getMessage());
            }
        }

        Collections.sort(collections);

        return collections;
    }

    public static boolean removeCollection(String name) throws IOException {
        PreparedStatement stmt = null;
        int rows = 0;

        try {
            stmt = SqlManager.getInstance().getSqlStatement("REMOVE.COLLECTION", -1, -1);
            stmt.setString(1, name);
            rows = stmt.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(CollectionsManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();

                } catch (SQLException ex) {
                    Logger.getLogger(CollectionsManager.class.getName()).log(Level.SEVERE, null, ex);
                    throw new IOException(ex.getMessage());
                }
            }
        }

        //compress the data base everytime a collection is removed
//        ConnectionManager.compress();

        return (rows > 0);
    }
}
