package io.github.ensyb.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface TableDataAdapter<Type> {

	public Type row(ResultSet rs) throws SQLException;
	
	
}
