                                                   /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.RunScript;
import org.h2.tools.Script;

/**
 *
 * @author Aretha
 */
public class ConnectionManager {

    private java.sql.Connection conn;
    private static ConnectionManager _instance;
    private final String properties = "./config/database.properties";
    private final String temp = "./data_base/temp.sql";

    public ConnectionManager() throws IOException {
        try {
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream(this.properties)) {
                props.load(in);
                String url = props.getProperty("jdbc.url");
                String username = props.getProperty("jdbc.username");
                String password = props.getProperty("jdbc.password");

                this.conn = this.createConnection(url, username, password);
            }

        } catch (IOException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex.getMessage());
        }
    }

    private Connection createConnection(String url, String username, String password) {
        Connection connection = null;
        try {
            JdbcConnectionPool cp = JdbcConnectionPool.create(url, username, password);
            connection = cp.getConnection();
        } catch (SQLException ex) {
            Object[] options = {"Retry", "Cancel"};
            int n = JOptionPane.showOptionDialog(null,
                    "The database is already open. Close the previous instance and retry.",
                    "Warning",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (n == 0) {
                connection = this.createConnection(url, username, password);
            } else if (n == 1) {
                return null;
            }
        } finally {
            return connection;
        }
    }

    public static void compress() throws IOException {
        try {
            Properties props = new Properties();
            FileInputStream in = new FileInputStream(ConnectionManager.getInstance().properties);
            props.load(in);

            String url = props.getProperty("jdbc.url");
            String username = props.getProperty("jdbc.username");
            String password = props.getProperty("jdbc.password");
            String dir = props.getProperty("jdbc.dir");
            String database = props.getProperty("jdbc.database");

            Script.execute(url, username, password, ConnectionManager.getInstance().temp);
            DeleteDbFiles.execute(dir, database, true);
            RunScript.execute(url, username, password, ConnectionManager.getInstance().temp, null, false);

        } catch (SQLException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex.getMessage());
        }
    }

    public java.sql.Connection getConnection() {
        return conn;
    }

    public static ConnectionManager getInstance() throws IOException {
        if (_instance == null || _instance.getConnection() == null) {
            _instance = new ConnectionManager();
        }

        return _instance;
    }

    public void dispose() throws IOException {
        //closing the data base connection
        if (this.conn != null) {
            try {
                this.conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
                throw new IOException(ex.getMessage());
            }
        }

        _instance = null;
    }
}
