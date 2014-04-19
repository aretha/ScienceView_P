package topicevolutionvis.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class SqlUtil
{
	private SqlUtil() {}
	
	public static void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {}
		}
	}

	public static void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {}
		}
	}

	public static void fullyClose(ResultSet rs) {
		if (rs != null) {
			SqlUtil.close(rs);
			try {
				SqlUtil.fullyClose(rs.getStatement());
			} catch (SQLException e) {
			}
		}
	}

	
	public static void close(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {}
		}
	}

	public static void fullyClose(Statement stmt) {
		if (stmt != null) {
			SqlUtil.close(stmt);
			try {
				SqlUtil.close(stmt.getConnection());
			} catch (SQLException e) {
			}
		}
	}
	
}
