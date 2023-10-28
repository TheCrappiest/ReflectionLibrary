package com.thecrappiest.library.reflection.multi;

import java.lang.reflect.Method;
import java.util.stream.Stream;

public class ReflectMethod {

    private Method method;
    private Class<?> location;
    
    public ReflectMethod(Class<?> location, String name) {
        this.location = location;
        method = findMethod(name);
    }
    
    public ReflectMethod(Class<?> location, String name, Class<?> returnType) {
        this.location = location;
        method = findMethod(name, returnType);
    }
    
    public ReflectMethod(Class<?> location, String name, Class<?>[] parameterTypes) {
        this.location = location;
        method = findMethod(name, parameterTypes);
    }
    
    public ReflectMethod(Class<?> location, String name, Class<?> returnType, Class<?>[] parameterTypes) {
        this.location = location;
        method = findMethod(name, returnType, parameterTypes);
    }
    
    public ReflectMethod(Method method) {
        location = method.getDeclaringClass();
        this.method = method;
    }
    
    public Class<?> getLocation() {
        return location;
    }
    
    public Method get() {
        return method;
    }
    
    public Method findMethod(String name) {
        return findMethod(name);
    }
    
    public Method findMethod(String name, Class<?> returnType) {
        return findMethod(name, returnType, null);
    }
    
    public Method findMethod(String name, Class<?>[] parameterTypes) {
        return findMethod(name, null, parameterTypes);
    }
    
    public Method findMethod(String name, Class<?> returnType, Class<?>[] parameterTypes) {
        if (location == null)
            throw new IllegalArgumentException("Location cannot be null.");
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Name cannot be null or empty.");
        
        int parameterCount = parameterTypes.length;
        
        Method retrievedMethod = Stream.of(location.getDeclaredMethods()).filter(method -> {
            method.setAccessible(true);
            
            if (!method.getName().equals(name))
                return false;
            if (returnType != null && method.getReturnType() != returnType)
                return false;
            if (parameterTypes != null) {
                if (method.getParameterCount() != parameterCount)
                    return false;
                
                Class<?>[] methodParams = method.getParameterTypes();
                for (int i = 0; i < parameterCount; i++) {
                    if (!methodParams[i].isAssignableFrom(parameterTypes[i]))
                        return false;
                }
            }
            
            return true;
        }).findFirst().orElse(null);
        
        return retrievedMethod;
    }
    
    public Object invoke(Object object) {
        try {
            return method.invoke(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Object invoke(Object object, Object... parameters) {
        try {
            return method.invoke(object, parameters);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
