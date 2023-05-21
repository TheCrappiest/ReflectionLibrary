package com.thecrappiest.library.reflection.multi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

public class ReflectMethod {

	// Object variable
	Method method;
	
	/*
	 * Constructs object by method
	 */
	public ReflectMethod(Method method) {
		this.method = method;
	}
	
	/*
	 * Constructs object by location and name
	 */
	public ReflectMethod(Class<?> location, String name) {
		getMethod(location, name);
	}
	
	/*
	 * Constructs object by location, name and return type
	 */
	public ReflectMethod(Class<?> location, String name, Class<?> type) {
		getMethodByReturnType(location, name, type);
	}
	
	/*
	 * Returns the variabled method
	 */
	public Method get() {
		return method;
	}
	
	/*
	 * Invokes method at location
	 */
	public Object invoke(Object location) {
		Class<?> returnType = method.getReturnType();
		
		Object returned = null;
		try {
			returned = method.invoke(location);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		if(returnType == Void.TYPE || returnType == void.class)
			return this;
		else
			return returned;
	}
	
	/*
	 * Invokes method at location with parameters
	 */
	public Object invoke(Object location, Object...parameters) {
		Class<?> returnType = method.getReturnType();
		
		Object returned = null;
		try {
			returned = method.invoke(location, parameters);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		if(returnType == Void.TYPE || returnType == void.class)
			return this;
		else
			return returned;
	}
	
	/*
	 * Retrieves method by location and name
	 */
	private void getMethod(Class<?> location, String name) {
		method = Stream.of(location.getDeclaredMethods())
                .filter(method -> name.equals(method.getName()))
                .findFirst().orElse(null);
	}
	
	/*
	 * Retrieves method by location, name and return type
	 */
	private void getMethodByReturnType(Class<?> location, String name, Class<?> type) {
		method = Stream.of(location.getDeclaredMethods())
                .filter(method -> name.equals(method.getName()) 
                        && (type == null || (type != null && !method.getReturnType().equals(type))))
                .findFirst().orElse(null);
	}
	
}
