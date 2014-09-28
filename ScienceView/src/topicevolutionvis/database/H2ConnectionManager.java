package topicevolutionvis.database;

import java.sql.Connection;
import java.sql.SQLException;


import org.h2.jdbcx.JdbcConnectionPool;


/**
 * Manager of database connections.
 */
public class H2ConnectionManager extends ConnectionManager
{
    /**
     * Pool of database connection. Whenever you need to get a new database connection,
     * you should ask for the pool using JdbcConnectionPool.getConnection().
     */
    private JdbcConnectionPool connPool;
    
	@Override
	protected boolean createPool(String url, String username, String password) {
		try {
			                 connPool = JdbcConnectionPool.create(url, username, password);
            connPool.setMaxConnections(60);
            connPool.setLoginTimeout(1);
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}

	@Override
	protected Connection createConnection() throws SQLException {
		return connPool.getConnection();
	}
}
