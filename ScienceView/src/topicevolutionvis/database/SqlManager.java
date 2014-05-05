package topicevolutionvis.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Manager of SQL statements (queries).
 */
public class SqlManager
{
	/**
	 * Instance of the SqlManager (only one per application, singleton pattern).
	 */
    private static SqlManager _instance;

    /**
     * Name of the file which holds the SQL statements.
     */
	private static final String SQL_STATEMENTS_CONFIG = "/scienceview/sql.properties";

    /**
     * SQL statements.
     */
    private Properties properties;
    
    private Map<String, Integer> stats;
    
    /**
     * Creates a new instance of SqlManager (singleton pattern)
     */
    private SqlManager()
    {
    	InputStream is = null;

    	try {
    		is = SqlManager.class.getResourceAsStream(SQL_STATEMENTS_CONFIG);
    		properties = new Properties();
    		properties.load(is);
        } catch (IOException ioe) {
        	throw new RuntimeException("Cannot load configuration from file", ioe);
        } finally {
        	if (is != null) {
        		try {
        			is.close();
        		} catch (IOException e) {
        		}
        	}
		}
    	
    	stats = new HashMap<String, Integer>();
    }

    /**
     * Create an instance of the SqlManager (singleton pattern).
     * 
     * @return SqlManager.
     */
    public synchronized static SqlManager getInstance()
    {
    	if (_instance == null) {
            _instance = new SqlManager();
        }
        return _instance;
    }
    
    public PreparedStatement getSqlStatement(Connection conn, String id) {
    	return getSqlStatement(conn, id, -1, -1);
    }
    
    public PreparedStatement getSqlStatement(Connection conn, String id, int resultSetType, int resultSetConcurrency)
    {
    	return createSqlStatement(conn, properties.getProperty(id), resultSetType, resultSetConcurrency);
    }
    
    public PreparedStatement createSqlStatement(Connection conn, String query)
    {
    	return createSqlStatement(conn, query, -1, -1);
    }

    public PreparedStatement createSqlStatement(Connection conn, String query, int resultSetType, int resultSetConcurrency)
    {
    	try {
    		synchronized (stats) {
	    		if (! stats.containsKey(query)) {
	    			stats.put(query, 1);
	    		} else {
	    			stats.put(query, stats.get(query) + 1);
	    		}
	    		// System.out.println(query);
			}
       	    if (resultSetType != -1 && resultSetConcurrency != -1) {
                return conn.prepareStatement(query, resultSetType, resultSetConcurrency);
            } else {
                return conn.prepareStatement(query);
            }
        } catch (SQLException ex) {
            throw new UnsupportedOperationException("Error preparing the SQL query", ex);
        }
    }

    
    public synchronized void close() {
    	properties = null;
    	_instance = null;
    }
}
