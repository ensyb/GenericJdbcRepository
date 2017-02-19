package io.github.ensyb.repository;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface SqlExecution<Type> {
	Type execute(Connection connection) throws SQLException;
}
