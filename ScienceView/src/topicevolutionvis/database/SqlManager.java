/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aretha
 */
public class SqlManager {

    private static SqlManager _instance;
    private Properties properties;

    /**
     * Creates a new instance of SqlManager
     */
    private SqlManager() throws IOException {
        try {
            //read the file containing the sql statements
            this.properties = new Properties();
            FileInputStream file = new FileInputStream("./config/sql.properties");
            this.properties.load(file);
            if (file != null) {
                file.close();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SqlManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex.getMessage());
        }
    }

    public PreparedStatement getSqlStatement(String id, int resultSetType, int resultSetConcurrency) throws IOException {
        Connection conn;
        try {
            conn = ConnectionManager.getInstance().getConnection();
            if (resultSetType != -1 && resultSetConcurrency != -1) {
                return conn.prepareStatement(properties.getProperty(id), resultSetType, resultSetConcurrency);
            } else {
                return conn.prepareStatement(properties.getProperty(id));
            }
        } catch (SQLException ex) {
            Logger.getLogger(SqlManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getNome(Connection con, int id) {
        String nome = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String consulta = "SELECT nome FROM Funcionarios WHERE id_func=" + id;
        try {
            rs = stmt.executeQuery();
            if (rs.next()) {
                nome = rs.getString("nome");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                stmt.close();
                rs.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return nome;
    }

    public static SqlManager getInstance() throws IOException {
        if (_instance == null) {
            _instance = new SqlManager();
        }
        return _instance;
    }
}
