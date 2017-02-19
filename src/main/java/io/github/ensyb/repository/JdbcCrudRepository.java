package io.github.ensyb.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

public class JdbcCrudRepository<ValueObjectType> implements GenericRepository<ValueObjectType> {

	protected ObjectDataAdapter<ValueObjectType> objectDataAdapter;
	protected TableDataAdapter<ValueObjectType> tableDataAdapter;
	private String tableName;

	public JdbcCrudRepository(String tableName, ObjectDataAdapter<ValueObjectType> objectDataAdapter,
			TableDataAdapter<ValueObjectType> tableDataAdapter) {
		
		this.tableName = tableName;
		this.objectDataAdapter = objectDataAdapter;
		this.tableDataAdapter = tableDataAdapter;
	}

	@Override
	public SqlExecution<ValueObjectType> objectFromWhere(String idColumnName, Object id) {
		return (connection) -> {
			String ovako = constructSelectColumnsWhere(idColumnName, "=", "*");
			PreparedStatement statement = preparedStatementBuilder(connection,
					ovako, id);
			ResultSet resultSet = statement.executeQuery();

			ValueObjectType querriedResult = null;

			if (resultSet.next()) {
				querriedResult = tableDataAdapter.row(resultSet);
			}
			return querriedResult;
		};

	}

	/**
	 * Do not return generated keys
	 */
	@Override
	public SqlExecution<ValueObjectType> insertObject(ValueObjectType object) {
		return (connection) -> {
			PreparedStatement statement = preparedStatementBuilder(connection, constructInsertIntoRow(object),
					objectDataAdapter.mapToComumns(object).values().stream().toArray(Object[]::new));
			statement.executeUpdate();
			return object;
		};
	}

	@Override
	public SqlExecution<ValueObjectType> listFromWhere(String columnName, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SqlExecution<ValueObjectType> updateObject(ValueObjectType object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteObject(ValueObjectType object, Connection connection) {
		
	}
	
	
	private String constructSelectColumnsWhere(String whereColumnName, String operator, String... columnNames) {
		return new StringBuilder("SELECT ")
				.append(setupSelectValueParameters(columnNames))
				.append(" FROM ").append(this.tableName)
				.append(" WHERE ").append(whereColumnName).append(" ").append(operator).append(" ?").toString();
	}

	public String constructInsertIntoRow(ValueObjectType object) {
		return new StringBuilder("INSERT INTO ").append(this.tableName)
				.append(setupInsertPreparedStatementParameters(object)).toString();
	}
	
	private String constructUpdateRow(ValueObjectType object, String whereColumnName) {
		return new StringBuilder("UPDATE ").append(this.tableName).append(" SET ")
				.append(setupUpdatePreparedStatementParametars(object)).append(" WHERE ").append(whereColumnName)
				.append(" = ").append(" ?;").toString();
	}

	private String consturctDeleteRow(String idColumn) {
		return new StringBuilder("DELETE FROM ").append(this.tableName).append(" WHERE ").append(idColumn).append(" = ")
				.append("?;").toString();
	}

	private PreparedStatement preparedStatementBuilder(Connection connection, String sql, Object... values)
			throws SQLException {
		PreparedStatement statement = connection.prepareStatement(sql);
		setupValues(statement, values);
		return statement;

	}
	
	private String setupSelectValueParameters(String[] values){
		StringBuilder columnNames = new StringBuilder();
		for (String value : values) {
			columnNames.append(value).append(",");
		}
		return columnNames.deleteCharAt(columnNames.length()-1).toString();
	}

	private String setupInsertPreparedStatementParameters(ValueObjectType object) {
		StringBuilder columnNames = new StringBuilder(" (");
		for (Map.Entry<String, Object> columnInfo : this.objectDataAdapter.mapToComumns(object).entrySet()) {
			columnNames.append(columnInfo.getKey()).append(",");
		}

		columnNames.deleteCharAt(columnNames.length() - 1).append(" )").append(" VALUES (");

		for (int i = 0; i < this.objectDataAdapter.mapToComumns(object).entrySet().size(); i++) {
			columnNames.append("?").append(",");
		}

		return columnNames.deleteCharAt(columnNames.length() - 1).append(")").append("").toString();
	}

	private String setupUpdatePreparedStatementParametars(ValueObjectType object) {
		StringBuilder querryPartBuilder = new StringBuilder();
		for (String columnNames : this.objectDataAdapter.mapToComumns(object).keySet()) {
			querryPartBuilder.append(columnNames).append(" = ").append("?").append(",");
		}
		return querryPartBuilder.deleteCharAt(querryPartBuilder.length() - 1).toString();
	}

	private void setupValues(PreparedStatement statement, Object[] values) throws SQLException {
		int preparedStatementIndex = 1;
		for (Object object : values) {
			statement.setObject(preparedStatementIndex, object);
			preparedStatementIndex++;
		}
	}

}
