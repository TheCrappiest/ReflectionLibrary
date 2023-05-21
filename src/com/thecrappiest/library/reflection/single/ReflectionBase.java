package com.thecrappiest.library.reflection.single;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Bukkit;

public class ReflectionBase {
    
    /*
     * Instance for ease of access
     */
    private static ReflectionBase instance = new ReflectionBase();
    public static ReflectionBase instance() {
        return instance;
    }
    
    /*
     * An interface for accessing fields
     */
    public interface ReflectField<T> {
        T get(Object location);
        void set(Object location, Object obj);
    }
    
    /*
     * An interface for accessing methods
     */
    public interface ReflectMethod {
        Object invoke(Object location, Object...parameters);
    }
    
    /*
     * Variables for the current server version
     */
    private String bukkitVersion = Bukkit.getServer().getClass().getPackage().getName();
    private String serverVersion = bukkitVersion.replace("org.bukkit.craftbukkit.", "");
    
    /*
     * Notably used nms classes
     */
    private enum ClassPlaceholders {
        NMS("net.minecraft.server"),
        NMN("net.minecraft.network"),
        NMSVersion("net.minecraft.server.{VER}"),
        
        NMSN("{NMS}.network"),
        NMSL("{NMS}.level"),
        NMNP("{NMN}.protocol"),
        NMNPG("{NMNP}.game"),
        
        OBC("org.bukkit.craftbukkit.{VER}"),
        OBCE("{OBC}.entity"),
        
        P_OUT("PacketPlayOut"),
        P_IN("PacketPlayIn");
        
        String path;
        
        private ClassPlaceholders(String classPath) {
            path = classPath;
        }
        
        public String getPath() {
            return path;
        }
    }
    
    /*
     * Replaces placeholders from enum in the provided string
     */
    public String parsePlaceholders(String s) {
        String edit = s;
        
        while(containsPlaceholder(edit))
            edit = replacePlaceholder(edit);
        
        return edit;
    }
    
    private boolean containsPlaceholder(String s) {
        List<ClassPlaceholders> placeholders = Arrays.asList(ClassPlaceholders.values());
        
        if(s.contains("{VER}"))
            return true;
        
        for(ClassPlaceholders placeholder : placeholders)
            if(s.contains("{"+placeholder.name()+"}"))
                return true;
        
        return false;
    }
    
    private String replacePlaceholder(String s) {
        String edit = s;
        
        for (ClassPlaceholders placeholder : ClassPlaceholders.values())
            edit = edit.replace("{"+placeholder.name()+"}", placeholder.getPath());
        edit = edit.replace("{VER}", serverVersion);
        
        return edit;
    }
    
    /*
     * Provides class from the provided path
     */
    public Class<?> getClassFromPath(String path) {
        String parsedPath = parsePlaceholders(path);
        
        try {
            return Class.forName(parsedPath);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /*
     * Returns a Constructor from the provided class path and parameter types
     */
    public Constructor<?> getConstructor(String path, Class<?>...params) {
        Class<?> location = getClassFromPath(path);
        
        if(location != null)
            return getConstructor(location, params);
        
        return null;
    }
    
    /*
     * Returns an Constructor from the provided class and parameter types
     */
    public Constructor<?> getConstructor(Class<?> constructorLocation, Class<?>...params) {
        return Arrays.stream(constructorLocation.getDeclaredConstructors()).filter(con -> con.getParameterTypes().equals(params)).findAny().orElse(null);
    }
    
    /*
     * Returns an Constructor from the provided class
     */
    public Constructor<?> getConstructor(Class<?> constructorLocation) {
        return Arrays.stream(constructorLocation.getDeclaredConstructors()).filter(con -> con.getParameterCount() == 0).findAny().orElse(null);
    }
    
    /*
     * Returns an AccessibleField from the provided class and field name
     */
    public <T> ReflectField<T> getField(Class<?> fieldLocation, String name) {
        if(name == null || name.isEmpty())
            throw new IllegalArgumentException("Name cannot be null or empty.");
        
        try {
            return createReflectField(fieldLocation.getDeclaredField(name));
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /*
     * Returns an AccessibleField from the provided class, field name and return type
     */
    public <T> ReflectField<T> getField(Class<?> fieldLocation, String name, Class<?> type) {
        if(name == null || name.isEmpty())
            throw new IllegalArgumentException("Name cannot be null or empty.");
        
        Field requestedField = Stream.of(fieldLocation.getDeclaredFields())
                .filter(field -> name.equals(field.getName()) && type.isAssignableFrom(field.getType()))
                .findFirst().orElse(null);
        
        if(requestedField != null)
            return createReflectField(requestedField);
        else
            return null;
    }
    
    /*
     * ReflectField creating method to prevent duplicated code
     */
    @SuppressWarnings("unchecked")
    public <T> ReflectField<T> createReflectField(Field field) {
        return new ReflectField<T>() {
            @Override
            public T get(Object location) {
                try {
                    return (T) field.get(location);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                    return null;
                }
            }
    
            @Override
            public void set(Object location, Object obj) {
                try {
                    field.set(location, obj);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        };
    }
    
    /*
     * Returns an AccessibleMethod from the provided class, method name and return type
     */
    public ReflectMethod getMethod(Class<?> methodLocation, String name) {
        if(name == null || name.isEmpty())
            throw new IllegalArgumentException("Name cannot be null or empty.");
        
        Method requestedMethod = Stream.of(methodLocation.getDeclaredMethods())
                .filter(method -> name.equals(method.getName()))
                .findFirst().orElse(null);
        
        if(requestedMethod != null)
            return createReflectMethod(requestedMethod);
        else
            return null;
    }
    
    /*
     * Returns an AccessibleMethod from the provided class, method name and return type
     */
    public ReflectMethod getMethodByReturnType(Class<?> methodLocation, String name, Class<?> type) {
        if(name == null || name.isEmpty())
            throw new IllegalArgumentException("Name cannot be null or empty.");
        
        Method requestedMethod = Stream.of(methodLocation.getDeclaredMethods())
                .filter(method -> name.equals(method.getName()) 
                        && (type == null || type != null && !method.getReturnType().equals(type)))
                .findFirst().orElse(null);
        
        if(requestedMethod != null)
            return createReflectMethod(requestedMethod);
        else
            return null;
    }
    
    /*
     * ReflectMethod creating method to prevent duplicated code
     */
    public ReflectMethod createReflectMethod(Method method) {
        return new ReflectMethod() {
            @Override
            public Object invoke(Object obj, Object... parameters) {
                try {
                    method.invoke(obj, parameters);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                
                return this;
            }
        };
    }
}