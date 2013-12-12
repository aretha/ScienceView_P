/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 
 * @author Aretha
 */
public class CreateDataBase {

	public void create() throws Exception {
		removeTables();
		createTables();
		ConnectionManager.getInstance().dispose();
	}

	private void createTables() throws Exception {
		PreparedStatement stmt = null;
		try {

			stmt = SqlManager.getInstance().getSqlStatement("CREATE.TABLE.COLLECTIONS", -1, -1);
			stmt.executeUpdate();
			stmt.close();

			stmt = SqlManager.getInstance().getSqlStatement("CREATE.TABLE.AUTHORS", -1, -1);
			stmt.executeUpdate();
			stmt.close();

			stmt = SqlManager.getInstance().getSqlStatement("CREATE.TABLE.CONTENT", -1, -1);
			stmt.executeUpdate();
			stmt.close();

			stmt = SqlManager.getInstance().getSqlStatement("CREATE.TABLE.REFERENCES", -1, -1);
			stmt.executeUpdate();
			stmt.close();

			stmt = SqlManager.getInstance().getSqlStatement("CREATE.TABLE.DOCUMENTS.TO.REFERENCES", -1, -1);
			stmt.executeUpdate();
			stmt.close();

			stmt = SqlManager.getInstance().getSqlStatement("CREATE.TABLE.DOCUMENTS.TO.AUTHORS", -1, -1);
			stmt.executeUpdate();
			stmt.close();

			stmt = SqlManager.getInstance().getSqlStatement("CREATE.INDEX.REFERENCES", -1, -1);
			stmt.executeUpdate();
			stmt.close();

			stmt = SqlManager.getInstance().getSqlStatement("CREATE.INDEX.AUTHORS", -1, -1);
			stmt.executeUpdate();
			stmt.close();

			stmt = SqlManager.getInstance().getSqlStatement("CREATE.INDEX.MATCH", -1, -1);
			stmt.executeUpdate();
			stmt.close();

		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ex) {
				}
			}
		}
	}

	/** Remove the tables of this data base. */
	private void removeTables() throws Exception {
		PreparedStatement stmt = null;

		try {
			stmt = SqlManager.getInstance().getSqlStatement("DROP.TABLE.COLLECTIONS", -1, -1);
			stmt.executeUpdate();
			stmt.close();
			
			stmt = SqlManager.getInstance().getSqlStatement("DROP.TABLE.DOCUMENTS", -1, -1);
			stmt.executeUpdate();
			stmt.close();

			stmt = SqlManager.getInstance().getSqlStatement("DROP.TABLE.REFERENCES", -1, -1);
			stmt.executeUpdate();
			stmt.close();

			stmt = SqlManager.getInstance().getSqlStatement("DROP.TABLE.DOCUMENTS.TO.REFERENCES", -1, -1);
			stmt.executeUpdate();
			stmt.close();

			stmt = SqlManager.getInstance().getSqlStatement("DROP.TABLE.AUTHORS", -1, -1);
			stmt.executeUpdate();
			stmt.close();

			stmt = SqlManager.getInstance().getSqlStatement("DROP.TABLE.DOCUMENTS.TO.AUTHORS", -1, -1);
			stmt.executeUpdate();
			stmt.close();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ex) {
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		CreateDataBase cdb = new CreateDataBase();
		cdb.create();
	}
}
