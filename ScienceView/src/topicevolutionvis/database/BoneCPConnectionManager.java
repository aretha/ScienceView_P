package topicevolutionvis.database;

import java.sql.Connection;
import java.sql.SQLException;

import org.h2.jdbcx.JdbcConnectionPool;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;


/**
 * Manager of database connections.
 */
public class BoneCPConnectionManager extends ConnectionManager
{
    /**
     * Pool of database connection. Whenever you need to get a new database connection,
     * you should ask for the pool using JdbcConnectionPool.getConnection().
     */
    private BoneCP connPool;
    
	@Override
	protected boolean createPool(String url, String username, String password) {
		try {
			BoneCPConfig config = new BoneCPConfig();
			config.setJdbcUrl(url);
			config.setUsername(username);
			config.setPassword(password);
			config.setMinConnectionsPerPartition(5);
			config.setMaxConnectionsPerPartition(30);
			config.setPartitionCount(4);
			config.setMaxConnectionAgeInSeconds(60);
			config.setAcquireRetryDelayInMs(200);
			config.setLazyInit(true);
			config.setDisableJMX(true);
			config.setDisableConnectionTracking(true);
			config.setConnectionTimeoutInMs(8000);
			connPool = new BoneCP(config);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	protected Connection createConnection() throws SQLException {
		return connPool.getConnection();
	}

	@Override
	public void close() {
		connPool.shutdown();
		super.close();
	}
	
	
}
