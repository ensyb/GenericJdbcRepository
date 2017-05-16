package io.github.ensyb.repository;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.github.ensyb.repository.resources.Human;

public class RepositoryTest extends BaseRepositoryTest {

	private GenericRepository<Human, Connection> humanRepository;

	@Before
	public void beforeEach() {
		super.beforeEach();
		this.humanRepository = new JdbcRepository<>("human", 
				humanObject -> {
					Map<String, Object> humanData = new LinkedHashMap<>();
					humanData.put("age", humanObject.getAge());
					humanData.put("name", humanObject.getName());
					return humanData;
				},
		resultSet -> {
			return new Human(resultSet.getInt("id"), 
					resultSet.getInt("age"), 
					resultSet.getString("name"));
		});
	}

	@After
	public void afterEach() {
		super.taredown();
	}

	@Test
	public void insertAndSelectObjecTest() throws SQLException {
		Connection connection = new H2Configuration().useDataSource().getConnection();
			
		humanRepository.insertObject(new Human(1, 16, "fujo"))
				.executeWith(connection);
			
		Human humo = humanRepository
				.objectFromWhere("age", 16)
				.executeWith(connection);

		assertThat(humo.summary(), is(contains("fujo")) != null);


	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void selectListOfObjectsTest() throws SQLException {
		Connection connection = new H2Configuration().useDataSource().getConnection();

		humanRepository.insertObject(new Human(1, 16, "Fujo"))
			.executeWith(connection);
		
		humanRepository.insertObject(new Human(2, 21, "Ole"))
			.executeWith(connection);
		
		humanRepository.insertObject(new Human(3, 16, "Boew"))
			.executeWith(connection);

		int limit = 2;
		List<Human> humoList = humanRepository.listFromWhere("age", 16, limit).executeWith(connection);

		assertThat(humoList,
				anyOf(hasItem(
						hasProperty("age", is(16)))));
		
		assertThat(humoList, hasSize(equalTo(limit)));

	}
	
	@Test
	public void updateObjectTest() throws SQLException{
		Connection connection = new H2Configuration()
				.useDataSource()
				.getConnection();
			
		//insert object that will be updated
		humanRepository.insertObject(new Human(1, 16, "Fujo"))
			.executeWith(connection);
		
		//update object
		Human updated = humanRepository.updateObject(new Human(1,20,"Boew"), "id", 1)
				.executeWith(connection);
		
		//select updated object
		Human selected = humanRepository
				.objectFromWhere("id", 1)
				.executeWith(connection);
		
		assertThat(selected, hasProperty("name", is(equalTo(updated.getName()))));
			
	}
	
	@Test
	public void testDeleteFromRepository() throws SQLException{
		Connection connection = new H2Configuration()
				.useDataSource()
				.getConnection();
		//insert 
		humanRepository.insertObject(new Human(1, 16, "Fujo"))
			.executeWith(connection);
		//then delete what is inserted
		humanRepository.deleteObject("id", 1, connection);
		//after that try to select deleted human
		Human humo = humanRepository
				.objectFromWhere("id", 1)
				.executeWith(connection);
		
		assertThat(humo, is(equalTo(null)));
		
		
	}

}
