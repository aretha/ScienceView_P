package topicevolutionvis.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.h2.jdbcx.JdbcConnectionPool;


/**
 * Manager of database connections.
 */
public class ConnectionManager
{
	/**
	 * Singleton for ConnectionManager.
	 */
    private static ConnectionManager _instance;
    
    /**
     * Pool of database connection. Whenever you need to get a new database connection,
     * you should ask for the pool using JdbcConnectionPool.getConnection().
     */
    private JdbcConnectionPool connPool;
    
    private int connectionsRequested;
    
    private int connectionsFailed;
	

    
    /**
     * File that hosts the configuration required to connect to the database.
     */
    private static final String DEFAULT_DATABASE_CONFIG = "/scienceview/database.properties";
    
    /**
     * Create the ConnectionManager. The constructor must be private due to the Singleton
     * pattern.
     * @throws FileNotFoundException 
     */
    private ConnectionManager(File file) throws FileNotFoundException
    {
        this(new FileInputStream(file));
            
    }

    /**
     * Create the ConnectionManager. The constructor must be private due to the Singleton
     * pattern.
     */
    private ConnectionManager(String resourcePath)
    {
        this(ConnectionManager.class.getResourceAsStream(resourcePath));
    }

    /**
     * Create the ConnectionManager. The constructor must be private due to the Singleton
     * pattern.
     */
    private ConnectionManager(InputStream in)
    {
        try {
            Properties props = new Properties();
            props.load(in);

            String url = props.getProperty("jdbc.url");
            String username = props.getProperty("jdbc.username");
            String password = props.getProperty("jdbc.password");
            connPool = JdbcConnectionPool.create(url, username, password);
            if (connPool == null) {
            	throw new IllegalArgumentException("Cannot create pool of database connections");
            }
            connPool.setMaxConnections(30);
            connPool.setLoginTimeout(0);
            System.out.println("Max database connections: " + connPool.getMaxConnections());
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot load configuration for database connection", e);
        } finally {
            if (in != null) {
                try {
					in.close();
				} catch (IOException e) {
				}
            }
        }
    }
    
    
    /**
     * Get the instance of ConnectionManager (Singleton pattern).
     * 
     * @return ConnectionManager
     * 
     * @throws IllegalArgumentException if configuration data could not be loaded or the
     * data was incorrect (and no connection to the database was possible).
     */
    public synchronized static ConnectionManager getInstance() 
    {
        if (_instance == null) {
       		_instance = new ConnectionManager(DEFAULT_DATABASE_CONFIG);
        }

        return _instance;
    }

    /**
     * Get the instance of ConnectionManager (Singleton pattern).
     * 
     * @return ConnectionManager
     * 
     * @throws IllegalArgumentException if configuration data could not be loaded or the
     * data was incorrect (and no connection to the database was possible).
     */
    public synchronized static ConnectionManager getInstance(File properties) 
    {
        if (_instance == null) {
        	try {
        		_instance = new ConnectionManager(properties);
        	} catch (FileNotFoundException fnfe) {
        		throw new IllegalArgumentException(fnfe);
        	}
        }

        return _instance;
    }
    
    /**
     * Get JDBC connection from database connection pool.
     * 
     * @return Database connection
     */
    public Connection getConnection() {
    	Connection conn = null;
		try {
			while (conn == null) {
				connectionsRequested++;
				try {
	    			conn = connPool.getConnection();
	    			if (conn == null) {
	    	        	connectionsFailed++;
	    				Thread.sleep(1000);
	    			}
				} catch (SQLException e) {
					connectionsFailed++;
					Thread.sleep(1000);
				}
			}
		} catch (InterruptedException e) {
			connectionsFailed++;
			Thread.currentThread().interrupt();
		}
		return conn;
    }



    /**
     * Close the connection manager.
     */
    public synchronized void close()
    {
        if (connPool != null) {
        	connPool.dispose();
        	connPool = null;
        }
        _instance = null;
    }
}
