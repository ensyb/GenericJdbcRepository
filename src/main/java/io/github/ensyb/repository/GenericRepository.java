package io.github.ensyb.repository;

import java.sql.Connection;
import java.util.List;

public interface GenericRepository<ValueObjectType, WithFunction> {

	public Execute<ValueObjectType, WithFunction> objectFromWhere(String keyName, Object key);

	public Execute<List<ValueObjectType>, WithFunction> listFromWhere(String keyName, Object key, Integer limit);

	public Execute<ValueObjectType, WithFunction> insertObject(ValueObjectType object);

	public Execute<ValueObjectType, WithFunction> updateObject(ValueObjectType object, String KeyName, Object Key);

	public void deleteObject(String keyName, Object key, Connection connection);

}
