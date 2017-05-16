package io.github.ensyb.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;

public class BaseRepositoryTest {

	protected final class H2Configuration {

		private final String driverClass = "org.h2.Driver";
		private final String databaseUrl = "jdbc:h2:mem:repoTest;mvcc=true;lock_timeout=10000;"
				+ "MODE=MYSQL;lock_mode=0;AUTOCOMMIT=FALSE;DATABASE_TO_UPPER=false";

		public BasicDataSource useDataSource() {
			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setDriverClassName(this.driverClass);
			dataSource.setUrl(this.databaseUrl);
			dataSource.setUsername("");
			dataSource.setPassword("");
			dataSource.setInitialSize(1);

			return dataSource;

		}
	}

	@Before
	public void beforeEach() {
		try {
			BasicDataSource ds = new H2Configuration().useDataSource();
			String tableDefinition = "CREATE TABLE IF NOT EXISTS `human` ("+
					  "`id` integer NOT NULL AUTO_INCREMENT PRIMARY KEY,"+
					  "`age` INT(3) NOT NULL,"+
					  "`name` varchar(48) NOT NULL, )";

			Connection connection = ds.getConnection();
			PreparedStatement statement = connection.prepareStatement(tableDefinition);
			statement.executeUpdate();
			connection.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@After
	public void taredown() {
		try {
			BasicDataSource ds = new H2Configuration().useDataSource();

			String drop = "DROP TABLE IF EXISTS `human`; ";

			Connection connection = ds.getConnection();
			PreparedStatement statement = connection.prepareStatement(drop);
			statement.executeUpdate();
			connection.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
