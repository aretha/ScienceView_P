package topicevolutionvis.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;


public abstract class ConnectionManager
{
	private static ConnectionManager _instance;
	
	private int connectionsRequested;
	
	private int connectionsFailed;

    /**
     * File that hosts the configuration required to connect to the database.
     */
    static final String DEFAULT_DATABASE_CONFIG = "/scienceview/database.properties";
	
    /**
     * Create the ConnectionManager. The constructor must be private due to the Singleton
     * pattern.
     * @throws FileNotFoundException 
     */
    protected final Properties loadProperties(File file) throws FileNotFoundException
    {
        return loadProperties(new FileInputStream(file));
    }

    /**
     * Create the ConnectionManager. The constructor must be private due to the Singleton
     * pattern.
     */
    protected final Properties loadProperties(String resourcePath)
    {
        InputStream in = ConnectionManager.class.getResourceAsStream(resourcePath);
        return loadProperties(in);
    }

    /**
     * Create the ConnectionManager. The constructor must be private due to the Singleton
     * pattern.
     */
    protected final Properties loadProperties(InputStream in)
    {
        Properties props = new Properties();
        try {
			props.load(in);
		} catch (IOException e) {
			throw new IllegalArgumentException("Invalid source for properties");
		} finally {
			try {
				in.close();
			} catch (Exception e) {}
		}
        return props;
    }
    
    /**
     * Create the ConnectionManager. The constructor must be private due to the Singleton
     * pattern.
     */
    protected final void preparePool(Properties props)
    {
        try {
            String url = props.getProperty("jdbc.url");
            String username = props.getProperty("jdbc.username");
            String password = props.getProperty("jdbc.password");
            
            boolean poolOk = createPool(url + ";AUTO_SERVER=TRUE", username, password); 
            if (! poolOk) {
            	throw new IllegalArgumentException("Cannot create pool of database connections");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot load configuration for database connection", e);
        } finally {
        }
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
	    			conn = createConnection();
	    			if (conn == null) {
	    	        	connectionsFailed++;
	    				Thread.sleep(1000);
	    			}
				} catch (Exception e) {
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

	public void close() {
		if (_instance != null) {
			_instance = null;
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
	public static synchronized ConnectionManager getInstance() {
	    if (_instance == null) {
	   		// _instance = new H2ConnectionManager();
	   		_instance = new BoneCPConnectionManager();
	   		Properties props = _instance.loadProperties(DEFAULT_DATABASE_CONFIG);
	   		_instance.preparePool(props);
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
	public static synchronized ConnectionManager getInstance(File properties) {
	    if (_instance == null) {
	    	try {
//	    		_instance = new H2ConnectionManager();
		   		_instance = new BoneCPConnectionManager();
		   		Properties props = _instance.loadProperties(properties);
		   		_instance.preparePool(props);
	    	} catch (FileNotFoundException fnfe) {
	    		throw new IllegalArgumentException(fnfe);
	    	}
	    }
	
	    return _instance;
	}
		
	protected abstract boolean createPool(String url, String username, String password);

	protected abstract Connection createConnection() throws SQLException; 
}