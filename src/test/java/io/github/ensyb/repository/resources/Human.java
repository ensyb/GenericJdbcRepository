package io.github.ensyb.repository.resources;

public class Human {

	private final int id;
	private final int age;
	private final String name;

	public Human(int id,int age, String name) {
		this.age = age;
		this.name = name;
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public int getAge() {
		return age;
	}

	public String getName() {
		return name;
	}

	public String summary(){
		return age+" "+name;
	}
}
