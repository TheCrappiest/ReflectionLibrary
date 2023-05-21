package com.thecrappiest.library.reflection.multi;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ReflectConstructor {

	/*
	 * Object variables
	 */
	Constructor<?> constructor;
	Class<?> location;
	
	/*
	 * Constructs object by constructor
	 */
	public ReflectConstructor(Constructor<?> constructor) {
		this.constructor = constructor;
	}
	
	/*
	 * Constructs object by location
	 */
	public ReflectConstructor(Class<?> location) {
		this.location = location;
		getConstructor();
	}
	
	/*
	 * Constructs object by location and parameter types
	 */
	public ReflectConstructor(Class<?> location, Class<?>...parameterTypes) {
		this.location = location;
		getConstructor(parameterTypes);
	}
	
	/*
	 * Constructs object by location with the option to ignore parameter types
	 */
	public ReflectConstructor(Class<?> location, boolean ignoreTypes, Class<?>...parameterTypes) {
		this.location = location;
		if(ignoreTypes)
			getConstructor(parameterTypes.length);
		else
			getConstructor(parameterTypes);
	}
	
	/*
	 * Returns the variabled constructor
	 */
	public Constructor<?> get() {
		return constructor;
	}
	
	/*
	 * Retrieves constructor from location with no parameters
	 */
	private void getConstructor() {
		constructor = Arrays.stream(location.getDeclaredConstructors())
				.filter(con -> con.getParameterCount() == 0).findAny().orElse(null);
	}
	
	/*
	 * Retrieves constructor from location with parameters
	 */
	private void getConstructor(Class<?>...parameterTypes) {
		constructor = Arrays.stream(location.getDeclaredConstructors())
				.filter(con -> con.getParameterTypes().equals(parameterTypes)).findAny().orElse(null);
	}
	
	/*
	 * Retrieves constructor from location by parameter count
	 */
	private void getConstructor(int parameterCount) {
		constructor = Arrays.stream(location.getDeclaredConstructors())
				.filter(con -> con.getParameterCount() == parameterCount).findAny().orElse(null);
	}
	
	/*
	 * Constructs new object using the variabled constructor
	 */
	public Object construct() {
		try {
			return constructor.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * Constructs new object using the variabled constructor and parameters
	 */
	public Object construct(Object...paramaters) {
		try {
			return constructor.newInstance(paramaters);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
