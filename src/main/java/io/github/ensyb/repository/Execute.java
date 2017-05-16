package io.github.ensyb.repository;

import java.sql.Connection;

@FunctionalInterface
public interface Execute<Type, Way> {
	
	Type executeWith(Way how);

	public static @FunctionalInterface interface SqlExecution<Type> extends Execute<Type, Connection> {
		Type executeWith(Connection connection);
	}
}
