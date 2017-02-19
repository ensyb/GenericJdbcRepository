package io.github.ensyb.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.junit.After;
import org.junit.Before;

public class BaseRepositoryTest {

	private final String PROTOCOL_DATBASE_NAME_FLAGS = "jdbc:derby:myTestDataBase/testDb;create=true";
	private final String HUMAN_TABLE_NAME = "humans";
	
	protected Connection connection;

	protected String createTableSql = "CREATE TABLE "+ HUMAN_TABLE_NAME+ "(age INTEGER NOT NULL, name VARCHAR(40))";
	
	@Before
	public void setup() {
		try {
			DriverManager.registerDriver(new EmbeddedDriver());
			this.connection = DriverManager.getConnection(PROTOCOL_DATBASE_NAME_FLAGS);
			
			Statement statement = this.connection.createStatement();
			
			statement.executeUpdate(createTableSql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@After
	public void taredown() {
		if (this.connection != null) {
			try {
				
				Statement statement = this.connection.createStatement();
				
				statement.executeUpdate("DROP TABLE "+HUMAN_TABLE_NAME);
				
				DriverManager.getConnection(PROTOCOL_DATBASE_NAME_FLAGS+";shutdown=true");
				this.connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
