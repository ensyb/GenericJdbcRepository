package io.github.ensyb.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.github.ensyb.repository.Execute.SqlExecution;

public class JdbcRepository<ValueObjectType> implements GenericRepository<ValueObjectType, Connection> {
	
	protected ObjectDataAdapter<ValueObjectType> objectDataAdapter;
	protected TableDataAdapter<ValueObjectType> tableDataAdapter;
	private String tableName;

	public JdbcRepository(String tableName, ObjectDataAdapter<ValueObjectType> objectDataAdapter,
			TableDataAdapter<ValueObjectType> tableDataAdapter) {
		
		this.tableName = tableName;
		this.objectDataAdapter = objectDataAdapter;
		this.tableDataAdapter = tableDataAdapter;
	}
	
	@Override
	public SqlExecution<ValueObjectType> objectFromWhere(String idColumnName, Object id) {
		return (connection) -> {
			try {
				String query = constructSelectColumnsWhere(idColumnName, "=", "*");
				PreparedStatement statement;
				statement = preparedStatementBuilder(connection, query, id);

				ResultSet resultSet = statement.executeQuery();
				ValueObjectType querriedResult = null;

				if (resultSet.next()) {
					querriedResult = tableDataAdapter.row(resultSet);
				}
				return querriedResult;
			} catch (SQLException e) {
				throw new RepositoryException(e);
			}
		};

	}

	@Override
	public SqlExecution<ValueObjectType> insertObject(ValueObjectType object) {
		return (connection) -> {
			try {
				PreparedStatement statement = preparedStatementBuilder(connection, constructInsertIntoRow(object),
						objectDataAdapter.mapToComumns(object).values().stream().toArray(Object[]::new));
				statement.executeUpdate();
				return object;
			} catch (SQLException e) {
				throw new RepositoryException(e);
			}
		};
	}


	
	@Override
	public SqlExecution<List<ValueObjectType>> listFromWhere(String columnName, Object value, Integer limit) {
		return (connection) -> {
			try {
				String selectQuery = constructSelectColumnsWhere(columnName, "=", "*") + " LIMIT " + limit;
				PreparedStatement statement = preparedStatementBuilder(connection, selectQuery, value);
				ResultSet resultSet = statement.executeQuery();

				List<ValueObjectType> querriedResultSet = new ArrayList<>();
				while (resultSet.next()) {
					querriedResultSet.add(tableDataAdapter.row(resultSet));
				}
				return querriedResultSet;
			} catch (SQLException e) {
				throw new RepositoryException(e);
			}
		};
	}
	@Override
	public SqlExecution<ValueObjectType> updateObject(ValueObjectType object,String column, Object value) {
		return (connction) -> {
			try {
				// update object
				String updateQuerry = constructUpdateRow(object, column);
				
				List<Object> values = objectDataAdapter.mapToComumns(object)
						.values()
						.stream()
						.collect(Collectors.toList());
				
				values.add(value);
				
				Object[] valuesForUpdate = values.toArray(new Object[values.size()]);
				PreparedStatement statement = preparedStatementBuilder(connction, updateQuerry,valuesForUpdate);
				
				int result = statement.executeUpdate();

				if (result < objectDataAdapter.mapToComumns(object).keySet().size()-1)
					throw new RepositoryException("Update failed");
				else
					return object;
			} catch (SQLException e) {
				throw new RepositoryException(e);
			}
		};
	}

	@Override
	public void deleteObject(String columnName, Object value, Connection connection) {
		try {
			String deleteQuery = consturctDeleteRow(columnName);
			PreparedStatement statement = preparedStatementBuilder(connection, deleteQuery, value);
			statement.executeUpdate();
			
		} catch (SQLException e) {
			throw new RepositoryException(e);
		}
		
	}

	public static class RepositoryException extends RuntimeException{
		private static final long serialVersionUID = 1L;
		
		public RepositoryException(String message) {
			super(message);
		}
		
		public RepositoryException(Throwable cause) {
			super(cause);
		}
		
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
