package topicevolutionvis.database;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SqlManagerTest {
	
	private ConnectionManager connManager;
	
	private SqlManager sqlManager;

	@Before
	public void setUp() throws Exception {
		connManager = H2ConnectionManager.getInstance();
		sqlManager = SqlManager.getInstance();
	}

	@After
	public void tearDown() throws Exception {
		sqlManager.close();
	}

	@Test
	public void testGetInstance() {
		assertNotNull(sqlManager);
	}

	@Test
	public void testGetSqlStatementString() {
		assertEquals(
				"SELECT abstract FROM Documents WHERE id_collection=? AND id_doc=?",
				sqlManager.getSqlStatement(connManager.getConnection(), "SELECT.DOCUMENT.ABSTRACT").toString().replaceFirst("prep[0-9]*:", "").trim());
	}

	@Test
	public void testGetSqlStatementStringManyTimes() throws SQLException {
		for (int i = 0; i < 200; i++) {
			System.out.print(".");
			Connection conn = connManager.getConnection();
			Statement stmt = sqlManager.getSqlStatement(conn, "SELECT.DOCUMENT.ABSTRACT");
			assertEquals(
				"SELECT abstract FROM Documents WHERE id_collection=? AND id_doc=?",
				stmt.toString().replaceFirst("prep[0-9]*:", "").trim());
			stmt.close();
			conn.close();
		}
	}
	
}
