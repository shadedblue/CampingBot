package ca.hapke.campingbot.log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nathan Hapke
 */
public class DatabaseQuery {
	public enum ColumnType {
		Integer,
		Long,
		String;
	}

	private List<String> columns = new ArrayList<>();
	private List<ColumnType> types = new ArrayList<>();
	private List<Object> values = new ArrayList<>();
	private String table;

	public DatabaseQuery(String table) {
		this.table = table;
	}

	public void add(String column, ColumnType type, Object value) {
		if (column == null || type == null || value == null) {
			return;
		}
		columns.add(column);
		types.add(type);
		values.add(value);
	}

	public void add(String column, Object value) {
		ColumnType type = null;
		if (value instanceof Integer)
			type = ColumnType.Integer;
		else if (value instanceof Long)
			type = ColumnType.Long;
		else if (value instanceof String)
			type = ColumnType.String;
		if (column == null || type == null || value == null) {
			return;
		}
		columns.add(column);
		types.add(type);
		values.add(value);
	}

	public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
		String c = null;
		String questionMarks = null;
		for (int i = 0; i < columns.size(); i++) {
			String column = columns.get(i);
			String quotedColumn = quoted(column);
			if (c == null) {
				c = quotedColumn;
			} else {
				c += "," + quotedColumn;
			}

			if (questionMarks == null) {
				questionMarks = "?";
			} else {
				questionMarks += ",?";
			}
		}

		String sql = "INSERT INTO " + table + "(" + c + ") VALUES (" + questionMarks + ");";
		PreparedStatement ps = connection.prepareStatement(sql);

		for (int i = 0; i < types.size(); i++) {
			ColumnType type = types.get(i);
			Object value = values.get(i);

			switch (type) {
			case Integer:
				ps.setInt(i + 1, (int) value);
				break;
			case Long:
				ps.setLong(i + 1, (long) value);
				break;
			case String:
				ps.setString(i + 1, (String) value);
				break;
			}
		}
		return ps;
	}

	private static String quoted(String column) {
		String out = column;
		if (!out.startsWith("\""))
			out = "\"" + out;
		if (!out.endsWith("\""))
			out = out + "\"";
		return out;
	}
}
