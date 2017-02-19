package io.github.ensyb.repository;

import java.sql.Connection;

public interface GenericRepository<ValueObjectType> {

	public SqlExecution<ValueObjectType> objectFromWhere(String columnName, Object value);

	public SqlExecution<ValueObjectType> listFromWhere(String columnName, Object value);

	public SqlExecution<ValueObjectType> insertObject(ValueObjectType object);

	public SqlExecution<ValueObjectType> updateObject(ValueObjectType object);

	public void deleteObject(ValueObjectType object, Connection connection);

}
