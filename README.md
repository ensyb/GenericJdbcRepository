[![Build Status](https://travis-ci.org/ensyb/GenericJdbcRepository.svg?branch=master)](https://travis-ci.org/ensyb/GenericJdbcRepository)
[![Coverage Status](https://coveralls.io/repos/github/ensyb/GenericJdbcRepository/badge.svg?branch=master)](https://coveralls.io/github/ensyb/GenericJdbcRepository?branch=master)


# generic repository

generic repository is small library for persisting java objects

example using jdbc implementation of generic repository which comes from library
```java

	private GenericRepository<Human, Connection> humanRepository = new JdbcRepository<>("human", 
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
		
	private Human selectHumanWithAge(int age, Connection connection){
		return humanRepository
					.objectFromWhere("age", age)
					.executeWith(connection);
	}
```