package io.github.ensyb.repository;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import io.github.ensyb.repository.resources.Human;

public class RepositoryTest extends BaseRepositoryTest {

	ObjectDataAdapter<Human> humanObjectDataAdapter =  humanObject -> {
		Map<String, Object> humanData = new LinkedHashMap<>();
		humanData.put("age", humanObject.getAge());
		humanData.put("name", humanObject.getName());
		return humanData;
	};
	
	TableDataAdapter<Human> humanTableDataAdapter = resultSet -> {
		Human newHuman = new Human();
		newHuman.setAge(resultSet.getInt("age"));
		newHuman.setName(resultSet.getString("name"));
		return newHuman;
	};

	
	public void setup() {
		super.setup();
	}
	
	public void taredown() {
		super.taredown();
	}

	@Test 
	public void insertObjecTest(){
		
		JdbcCrudRepository<Human> mhm = new JdbcCrudRepository<>("humans", humanObjectDataAdapter,humanTableDataAdapter);
		
		try {
			mhm.insertObject(new Human(16, "fujo")).execute(super.connection);
			Human humo = mhm.objectFromWhere("age", 16).execute(super.connection);
			
			System.out.println("ovo je "+ humo.getName()+ " i ima godina " +humo.getAge() );
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}

