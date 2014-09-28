/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aretha
 */
public class CreateDataBase {

    ConnectionManager connManager;

    private SqlManager sqlManager;

    public void create() throws Exception {
        connManager = ConnectionManager.getInstance();
        sqlManager = SqlManager.getInstance();
        removeTables();
        createTables();
        sqlManager.close();
    }

    private void createTables() {
        Connection conn = connManager.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = sqlManager.getSqlStatement(conn, "CREATE.TABLE.COLLECTIONS");
            stmt.executeUpdate();
            stmt.close();

            stmt = sqlManager.getSqlStatement(conn, "CREATE.TABLE.AUTHORS");
            stmt.executeUpdate();
            stmt.close();

            stmt = sqlManager.getSqlStatement(conn, "CREATE.TABLE.CONTENT");
            stmt.executeUpdate();
            stmt.close();

            stmt = sqlManager.getSqlStatement(conn, "CREATE.TABLE.REFERENCES");
            stmt.executeUpdate();
            stmt.close();

            stmt = sqlManager.getSqlStatement(conn, "CREATE.TABLE.DOCUMENTS.TO.REFERENCES");
            stmt.executeUpdate();
            stmt.close();

            stmt = sqlManager.getSqlStatement(conn, "CREATE.TABLE.DOCUMENTS.TO.AUTHORS");
            stmt.executeUpdate();
            stmt.close();

            stmt = sqlManager.getSqlStatement(conn, "CREATE.INDEX.REFERENCES");
            stmt.executeUpdate();
            stmt.close();

            stmt = sqlManager.getSqlStatement(conn, "CREATE.INDEX.AUTHORS");
            stmt.executeUpdate();
            stmt.close();

            stmt = sqlManager.getSqlStatement(conn, "CREATE.INDEX.MATCH");
            stmt.executeUpdate();
            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(CreateDataBase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                }
            }
        }
    }

    /**
     * Remove the tables of this data base.
     */
    private void removeTables() {
        Connection conn = connManager.getConnection();
        PreparedStatement stmt = null;

        try {
            stmt = SqlManager.getInstance().getSqlStatement(conn, "DROP.TABLE.COLLECTIONS");
            stmt.executeUpdate();
            stmt.close();

            stmt = SqlManager.getInstance().getSqlStatement(conn, "DROP.TABLE.DOCUMENTS");
            stmt.executeUpdate();
            stmt.close();

            stmt = SqlManager.getInstance().getSqlStatement(conn, "DROP.TABLE.REFERENCES");
            stmt.executeUpdate();
            stmt.close();

            stmt = SqlManager.getInstance().getSqlStatement(conn, "DROP.TABLE.DOCUMENTS.TO.REFERENCES");
            stmt.executeUpdate();
            stmt.close();

            stmt = SqlManager.getInstance().getSqlStatement(conn, "DROP.TABLE.AUTHORS");
            stmt.executeUpdate();
            stmt.close();

            stmt = SqlManager.getInstance().getSqlStatement(conn, "DROP.TABLE.DOCUMENTS.TO.AUTHORS");
            stmt.executeUpdate();
            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(CreateDataBase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        CreateDataBase cdb = new CreateDataBase();
        cdb.create();
    }
}
