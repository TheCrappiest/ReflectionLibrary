package com.thecrappiest.library.reflection.multi;

import java.lang.reflect.Field;
import java.util.stream.Stream;

public class ReflectField {

	// Object variable
	Field field;
	
	/*
	 * Constructs object by field
	 */
	public ReflectField(Field field) {
		this.field = field;
	}
	
	/*
	 * Constructs object by location and name
	 */
	public ReflectField(Class<?> location, String name) {
		getField(location, name);
	}
	
	/*
	 * Constructs object by location, name and type
	 */
	public ReflectField(Class<?> location, String name, Class<?> type) {
		getFieldByType(location, name, type);
	}
	
	/*
	 * Returns the variabled field
	 */
	public Field get() {
		return field;
	}
	
	/*
	 * Sets the value of the field in the object
	 */
	public void set(Object location, Object value) {
		try {
			field.setAccessible(true);
			field.set(location, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Returns if the field exists
	 */
	public boolean contains() {
		return field != null;
	}
	
	/*
	 * Retrieves field by name from location
	 */
	private void getField(Class<?> location, String name) {
		try {
			field = location.getDeclaredField(name);
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Retrieves field by name and type from location
	 */
	private void getFieldByType(Class<?> location, String name, Class<?> type) {
		field = Stream.of(location.getDeclaredFields())
                .filter(field -> name.equals(field.getName()) && (type == null || type.isAssignableFrom(field.getType())))
                .findFirst().orElse(null);
	}
	
}
