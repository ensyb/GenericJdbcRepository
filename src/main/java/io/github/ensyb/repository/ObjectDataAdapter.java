package io.github.ensyb.repository;

import java.util.Map;

@FunctionalInterface
public interface ObjectDataAdapter<Type> {

	public Map<String, Object> mapToComumns(Type object);
}
