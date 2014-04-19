package topicevolutionvis.database;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SqlManagerTest {
	
	private ConnectionManager connManager;
	
	private SqlManager sqlManager;

	@Before
	public void setUp() throws Exception {
		connManager = ConnectionManager.getInstance();
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
				"prep0: SELECT abstract FROM Documents WHERE id_collection=? AND id_doc=?",
				sqlManager.getSqlStatement(connManager.getConnection(), "SELECT.DOCUMENT.ABSTRACT").toString());
	}

}
