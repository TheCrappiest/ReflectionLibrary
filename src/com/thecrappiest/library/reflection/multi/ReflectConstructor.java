package com.thecrappiest.library.reflection.multi;

import java.lang.reflect.Constructor;
import java.util.stream.Stream;

public class ReflectConstructor {
    
    private Constructor<?> constructor;
    private Class<?> location;
    
    public ReflectConstructor(Class<?> location) {
        this.location = location;
        this.constructor = findConstructor();
    }
    
    public ReflectConstructor(Class<?> location, Class<?>... parameterTypes) {
        this.location = location;
        this.constructor = findConstructor(parameterTypes);
    }
    
    public ReflectConstructor(Class<?> location, int paramaterCount) {
        this.location = location;
        this.constructor = findConstructorWhileIgnoringTypes(paramaterCount);
    }
    
    public ReflectConstructor(Constructor<?> constructor) {
        this.constructor = constructor;
        this.location = constructor.getDeclaringClass();
    }
    
    public Constructor<?> get() {
        return constructor;
    }
    
    public Class<?> getLocation() {
        return location;
    }
    
    public Constructor<?> findConstructor() {
        Constructor<?> zeroParamConstruct = Stream.of(location.getDeclaredConstructors()).filter(con -> {
            con.setAccessible(true);
            return con.getParameterCount() == 0;
        }).findFirst().orElse(null);
        
        return zeroParamConstruct;
    }
    
    public Constructor<?> findConstructor(Class<?>[] parameterTypes) {
        int parameterCount = parameterTypes.length;
        Constructor<?> matchingParams = Stream.of(location.getDeclaredConstructors()).filter(con -> {
            con.setAccessible(true);
            
            if (con.getParameterCount() != parameterCount)
                return false;
            
            Class<?>[] conParams = con.getParameterTypes();
            for (int i = 0; i < parameterCount; i++) {
                if (!conParams[i].isAssignableFrom(parameterTypes[i]))
                    return false;
            }
            
            return true;
        }).findFirst().orElse(null);
        
        return matchingParams;
    }
    
    public Constructor<?> findConstructorWhileIgnoringTypes(int parameterCount) {
        Constructor<?> matchingParamAmount = Stream.of(location.getDeclaredConstructors()).filter(con -> {
            con.setAccessible(true);
            return con.getParameterCount() == parameterCount;
        }).findFirst().orElse(null);
        
        return matchingParamAmount;
    }
    
    public Object construct() {
        try {
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Object construct(Object...paramaters) {
        try {
            constructor.setAccessible(true);
            return constructor.newInstance(paramaters);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}