package topicevolutionvis.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class CollectionManager {

	public int getNextCollectionId() {
		ConnectionManager connManager = H2ConnectionManager.getInstance();
		int aux = -1;
		try (
			Connection conn = connManager.getConnection();
			PreparedStatement stmt = SqlManager.getInstance().getSqlStatement(conn, "SELECT.MAX.IDCOLLECTION");
			ResultSet rs = stmt.executeQuery();
		) {
			rs.next();
			aux = rs.getInt(1) + 1;
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
		
		return aux;
	}

	public int getCollectionId(String name) {
		ConnectionManager connManager = H2ConnectionManager.getInstance();
        int aux = -1;
        try (
			Connection conn = connManager.getConnection();
    		PreparedStatement stmt = SqlManager.getInstance().getSqlStatement(conn, "SELECT.COLLECTION.BY.NAME")) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    aux = rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            throw new IllegalArgumentException("Could not find a collection named " + name, ex);
        }
            
        return aux;
    }

	
    public boolean isUnique(String collection)
    {
    	ConnectionManager connManager = H2ConnectionManager.getInstance();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
        	conn = connManager.getConnection();
            stmt = SqlManager.getInstance().getSqlStatement(conn, "SELECT.COLLECTION.BY.NAME");
            stmt.setString(1, collection);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from database", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);
        }
    }
	
    public ArrayList<String> getCollections()
    {
    	ArrayList<String> collections = new ArrayList<String>();
        ConnectionManager connManager = H2ConnectionManager.getInstance();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
        	conn = connManager.getConnection();
            stmt = SqlManager.getInstance().getSqlStatement(conn, "SELECT.COLLECTIONS");
            rs = stmt.executeQuery();
        
            while (rs.next()) {
                String name = rs.getString("name");
                collections.add(name);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error reading collection", e);
        } finally {
            SqlUtil.close(rs);
            SqlUtil.close(stmt);
            SqlUtil.close(conn);

        }

        return collections;
    }

    public boolean removeCollection(String name) {
        ConnectionManager connManager = H2ConnectionManager.getInstance();
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;

        try {
        	conn = connManager.getConnection();
            stmt = SqlManager.getInstance().getSqlStatement(conn, "REMOVE.COLLECTION_BY_NAME");
            stmt.setString(1, name);
            rows = stmt.executeUpdate();
        } catch (SQLException e) {
        	throw new RuntimeException("Error removing collection", e);
        } finally {
            SqlUtil.close(stmt);
            SqlUtil.close(conn);

        }

        return (rows > 0);
    }
    
    public boolean removeCollection(int id) {
        ConnectionManager connManager = H2ConnectionManager.getInstance();
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;

        try {
        	conn = connManager.getConnection();
            stmt = SqlManager.getInstance().getSqlStatement(conn, "REMOVE.COLLECTION_BY_ID");
            stmt.setInt(1, id);
            rows = stmt.executeUpdate();
        } catch (SQLException e) {
        	throw new RuntimeException("Error removing collection", e);
        } finally {
            SqlUtil.close(stmt);
            SqlUtil.close(conn);

        }

        return (rows > 0);
    }
}
