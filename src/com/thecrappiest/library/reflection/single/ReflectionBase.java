package com.thecrappiest.library.reflection.single;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ReflectionBase {
    
    // Base instance
    private static ReflectionBase instance;
    public static ReflectionBase base() {
        return instance = instance == null ? new ReflectionBase() : instance;
    }
    
    // Version variables
    private String bukkitVersion = "org.bukkit.craftbukkit.v1_20_R2"; // Bukkit.getServer().getClass().getPackage().getName();
    private String serverVersion = bukkitVersion.replace("org.bukkit.craftbukkit.", "");
    
    // Class paths
    private enum PlaceHolders {
        NMS("net.minecraft.server"), 
        NMSVersion("{NMS}.{VER}"), 
        OBC("org.bukkit.craftbukkit.{VER}");
        
        String path;
        
        private PlaceHolders(String classPath) {
            path = classPath;
        }
        
        public static boolean isSet(String s) {
            try {
                valueOf(s); 
                return true;
            }catch (IllegalArgumentException e) {
                return false;
            }
        }
        
        public String getPath() {
            return path;
        }
    }
    
    // Pattern used to test for placeholders
    private Pattern holderPattern = Pattern.compile("\\{([^}]+)\\}");
    
    // Replaces placeholders with paths from the placeholders enum
    public String parsePath(String input) {
        String edit = input;
        
        Matcher matcher = holderPattern.matcher(edit);
        int timeout = 0;
        
        while(matcher.find() && timeout < 10) {
            timeout++;
            
            String withBrackets = matcher.group();
            String withoutBrackets = withBrackets.substring(1, withBrackets.length()-1);
            String parsed = PlaceHolders.isSet(withoutBrackets) ? PlaceHolders.valueOf(withoutBrackets).getPath() : withBrackets;
            
            edit = edit.replace(withBrackets, parsed);
            matcher = holderPattern.matcher(edit);
        }
        
        edit = edit.replace("{VER}", serverVersion);
        
        return edit;
    }
    
    // Cache mapping paths to their classes
    private Map<String, Class<?>> cachedClasses = new HashMap<>();
    
    // Returns class from the provided path
    public Class<?> getClassFromPath(String path) {
        String parsedPath = parsePath(path);
        
        if (cachedClasses.containsKey(parsedPath))
            return cachedClasses.get(parsedPath);
        
        try {
            Class<?> retrievedClass = Class.forName(parsedPath);
            cachedClasses.put(parsedPath, retrievedClass);
            return retrievedClass;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /*
     * Constructor collection
     */
    
    // Searches location for constructor with zero parameters
    public Constructor<?> findConstructor(Class<?> location) {
        Constructor<?> zeroParamConstruct = Stream.of(location.getDeclaredConstructors()).filter(con -> {
            con.setAccessible(true);
            return con.getParameterCount() == 0;
        }).findFirst().orElse(null);
        
        return zeroParamConstruct;
    }
    
    // Searches location for constructor with specified parameter types
    public Constructor<?> findConstructor(Class<?> location, Class<?>[] parameterTypes) {
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
    
    // Searches location for constructor with specified parameter amount
    public Constructor<?> findConstructorWhileIgnoringTypes(Class<?> location, int parameterCount) {
        Constructor<?> matchingParamAmount = Stream.of(location.getDeclaredConstructors()).filter(con -> {
            con.setAccessible(true);
            return con.getParameterCount() == parameterCount;
        }).findFirst().orElse(null);
        
        return matchingParamAmount;
    }
    
    /*
     * Field collection
     */
    
    // Searches location for field by name
    public Field findField(Class<?> location, String name) {
        return findField(location, name, null);
    }
    
    // Searches location for field by name and type
    public Field findField(Class<?> location, String name, Class<?> type) {
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
    
    /*
     * Method collection
     */
    
    // Searches location for method by name
    public Method findMethod(Class<?> location, String name) {
        return findMethod(location, name);
    }
    
    // Searches location for method by name and return type
    public Method findMethod(Class<?> location, String name, Class<?> returnType) {
        return findMethod(location, name, returnType, null);
    }
    
    // Searches location for method by name and parameter types
    public Method findMethod(Class<?> location, String name, Class<?>[] paramaterTypes) {
        return findMethod(location, name, null, paramaterTypes);
    }
    
    // Searches location for method by name, return type and parameter types
    public Method findMethod(Class<?> location, String name, Class<?> returnType, Class<?>[] parameterTypes) {
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
}