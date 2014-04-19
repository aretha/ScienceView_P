package topicevolutionvis.database;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConnectionManagerTest
{
	private ConnectionManager connManager;
	
	@Before
	public void setUp() throws Exception {
		connManager = ConnectionManager.getInstance();
	}

	@After
	public void tearDown() throws Exception {
		connManager.close();
	}

	@Test
	public void test() throws SQLException {
		Connection conn = connManager.getConnection();
		assertNotNull(conn);
	}

}
