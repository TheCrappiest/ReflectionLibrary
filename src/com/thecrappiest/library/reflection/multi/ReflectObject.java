package com.thecrappiest.library.reflection.multi;

import java.util.HashSet;
import java.util.Set;

public class ReflectObject {

	/*
	 * Variables of the reflected object and its class
	 */
	private Object object;
	private Class<?> objectClass;
	
	/*
	 * Sets to cache fields, methods and constructors of reflected object
	 */
	private Set<ReflectField> fields = new HashSet<>();
	private Set<ReflectMethod> methods = new HashSet<>();
	private Set<ReflectConstructor> constructors = new HashSet<>();
	
	/*
	 * Object constructor
	 */
	public ReflectObject(Object obj) {
		object = obj;
		objectClass = obj.getClass();
	}
	
	/*
	 * Retrieves ReflectField from cache by name
	 */
	private ReflectField retrieveFieldFromCache(String name) {
		return fields.stream().filter(f -> f.get().getName().equals(name)).findAny().orElse(null);
	}
	
	/*
	 * Retrieves ReflectField from cache by name and type
	 */
	private ReflectField retrieveFieldFromCache(String name, Class<?> type) {
		return fields.stream().filter(f -> f.get().getName().equals(name) && 
				(type == null || (type != null && type.isAssignableFrom(f.get().getType())))).findAny().orElse(null);
	}
	
	/*
	 * Retrieves ReflectField from object or cache by name
	 */
	public ReflectField getField(String name) {
		ReflectField current = retrieveFieldFromCache(name);
		
		if(current == null) {
			ReflectField requested = new ReflectField(objectClass, name);
			if(requested != null)
				fields.add(requested);
			return requested;
		}else {
			return current;
		}
	}
	
	/*
	 * Retrieves ReflectField from object or cache by name and type
	 */
	public ReflectField getField(String name, Class<?> type) {
		ReflectField current = retrieveFieldFromCache(name, type);
		
		if(current == null) {
			ReflectField requested = new ReflectField(objectClass, name, type);
			if(requested != null)
				fields.add(requested);
			return requested;
		}else {
			return current;
		}
	}
	
	/*
	 * Sets value of field from object by name
	 */
	public void setField(String name, Object value) {
		ReflectField current = getField(name);
		if(current == null)
			return;
		
		current.set(object, value);
	}
	
	/*
	 * Sets value of field from object by name and type
	 */
	public void setField(String name, Class<?> type, Object value) {
		ReflectField current = getField(name, type);
		if(current == null)
			return;
		
		current.set(object, value);
	}
	
	/*
	 * Retrieves ReflectMethod from cache by name
	 */
	private ReflectMethod retrieveMethodFromCache(String name) {
		return methods.stream().filter(m -> m.get().getName().equals(name)).findAny().orElse(null);
	}
	
	/*
	 * Retrieves ReflectMethod from cache by name and return type
	 */
	private ReflectMethod retrieveMethodFromCache(String name, Class<?> type) {
		return methods.stream().filter(m -> m.get().getName().equals(name) && 
				(type == null || (type != null && type == m.get().getReturnType()))).findAny().orElse(null);
	}
	
	/*
	 * Retrieves ReflectMethod from object or cache by name
	 */
	public ReflectMethod getMethod(String name) {
		ReflectMethod current = retrieveMethodFromCache(name);
		
		if(current == null) {
			ReflectMethod requested = new ReflectMethod(objectClass, name);
			if(requested != null)
				methods.add(requested);
			return requested;
		}else {
			return current;
		}
	}
	
	/*
	 * Retrieves ReflectMethod from object or cache by name and return type
	 */
	public ReflectMethod getMethod(String name, Class<?> type) {
		ReflectMethod current = retrieveMethodFromCache(name, type);
		
		if(current == null) {
			ReflectMethod requested = new ReflectMethod(objectClass, name, type);
			if(requested != null)
				methods.add(requested);
			return requested;
		}else {
			return current;
		}
	}
	
	/*
	 * Invokes method on object by name
	 */
	public Object invokeMethod(String name) {
		ReflectMethod current = getMethod(name);
		if(current == null)
			return null;
		
		return current.invoke(object);
	}
	
	/*
	 * Invokes method on object by name
	 */
	public Object invokeMethod(String name, Object...parameters) {
		ReflectMethod current = getMethod(name);
		if(current == null)
			return null;
		
		return current.invoke(object, parameters);
	}
	
	/*
	 * Invokes method on object by name and return type
	 */
	public Object invokeMethod(String name, Class<?> type, Object...parameters) {
		ReflectMethod current = getMethod(name, type);
		if(current == null)
			return null;
		
		return current.invoke(object, parameters);
	}
	
	/*
	 * Retrieves ReflectConstructor from cache
	 */
	private ReflectConstructor retrieveConstructorFromCache() {
		return constructors.stream().filter(c -> c.get().getParameterCount() == 0).findAny().orElse(null);
	}
	
	/*
	 * Retrieves ReflectConstructor from cache by parameter types
	 */
	private ReflectConstructor retrieveConstructorFromCache(Class<?>...parameterTypes) {
		return constructors.stream().filter(c -> c.get().getParameterTypes().equals(parameterTypes)).findAny().orElse(null);
	}
	
	/*
	 * Retrieves ReflectConstructor from cache with the option to ignore parameter types
	 */
	private ReflectConstructor retrieveConstructorFromCache(boolean ignoreTypes, Class<?>...parameterTypes) {
		return constructors.stream().filter(c -> !ignoreTypes && c.get().getParameterTypes().equals(parameterTypes)).findAny().orElse(null);
	}
	
	/*
	 * Retrieves ReflectConstructor from object or cache
	 */
	public ReflectConstructor getConstructor() {
		ReflectConstructor current = retrieveConstructorFromCache();
		
		if(current == null) {
			ReflectConstructor requested = new ReflectConstructor(objectClass);
			if(requested != null)
				constructors.add(requested);
			return requested;
		}else {
			return current;
		}
	}
	
	/*
	 * Retrieves ReflectConstructor from object or cache by parameterTypes
	 */
	public ReflectConstructor getConstructor(Class<?>...parameterTypes) {
		ReflectConstructor current = retrieveConstructorFromCache(parameterTypes);
		
		if(current == null) {
			ReflectConstructor requested = new ReflectConstructor(objectClass, parameterTypes);
			if(requested != null)
				constructors.add(requested);
			return requested;
		}else {
			return current;
		}
	}
	
	/*
	 * Retrieves ReflectConstructor from object or cache with the option to ignore parameter types
	 */
	public ReflectConstructor getConstructor(boolean ignoreTypes, Class<?>...parameterTypes) {
		ReflectConstructor current = retrieveConstructorFromCache(ignoreTypes, parameterTypes);
		
		if(current == null) {
			ReflectConstructor requested = new ReflectConstructor(objectClass, ignoreTypes, parameterTypes);
			if(requested != null)
				constructors.add(requested);
			return requested;
		}else {
			return current;
		}
	}
	
	/*
	 * Constructors new object from retrieved constructor
	 */
	public Object construct() {
		ReflectConstructor current = getConstructor();
		if(current == null)
			return null;
		
		return current.construct();
	}
	
	/*
	 * Constructors new object from retrieved constructor and parameters
	 */
	public Object construct(Object...parameters) {
		Class<?>[] parameterTypes = new Class<?>[parameters.length];
    	for(int i=0; i<parameters.length; i++)
    		parameters[i] = parameters[i].getClass();
		
		ReflectConstructor current = getConstructor(parameterTypes);
		if(current == null)
			return null;
		
		return current.construct(parameters);
	}
	
	/*
	 * Constructors new object from retrieved constructor and with the option to ignore paremter types
	 */
	public Object construct(boolean ignoreTypes, Object...parameters) {
		Class<?>[] parameterTypes = new Class<?>[parameters.length];
    	for(int i=0; i<parameters.length; i++)
    		parameters[i] = parameters[i].getClass();
		
		ReflectConstructor current = getConstructor(ignoreTypes, parameterTypes);
		if(current == null)
			return null;
		
		return current.construct(parameters);
	}
}
