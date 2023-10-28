package com.thecrappiest.library.reflection.multi;

import java.lang.reflect.Field;
import java.util.stream.Stream;

public class ReflectField {

    private Field field;
    private Class<?> location;
    
    public ReflectField(Class<?> location, String name) {
        this.location = location;
        this.field = findField(name);
    }
    
    public ReflectField(Class<?> location, String name, Class<?> type) {
        this.location = location;
        this.field = findField(name, type);
    }
    
    public ReflectField(Field field) {
        this.field = field;
        this.location = field.getDeclaringClass();
    }
    
    public Class<?> getLocation() {
        return location;
    }
    
    public Field get() {
        return field;
    }
    
    public Object getValue(Object object) {
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void setValue(Object object, Object value) {
        try {
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Field findField(String name) {
        return findField(name, null);
    }
    
    public Field findField(String name, Class<?> type) {
        if (location == null)
            throw new IllegalArgumentException("Location cannot be null.");
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Name cannot be null or empty.");
        
        Field retrievedField = Stream.of(location.getDeclaredFields()).filter(field -> {
            field.setAccessible(true);
            
            if (!field.getName().equals(name))
                return false;
            if (type != null && !field.getType().isAssignableFrom(type))
                return false;
            
            return true;
        }).findFirst().orElse(null);
        
        return retrievedField;
    }
    
}
