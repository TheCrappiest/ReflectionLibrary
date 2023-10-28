package com.thecrappiest.library.reflection.multi;

public class ReflectObject {

    private Object object;
    private Class<?> objectClass;
    
    public ReflectObject(Object obj) {
        this.object = obj;
        this.objectClass = obj.getClass();
    }
    
    public Object get() {
        return object;
    }
    
    public Class<?> getObjectClass() {
        return objectClass;
    }
    
    public Object construct() {
        return construct(false);
    }
    
    public Object constructWithParameters(Object... parameters) {
        return construct(false, parameters);
    }
    
    public Object construct(boolean reflect, Object... parameters) {
        if(parameters == null) {
            ReflectConstructor constructor = new ReflectConstructor(objectClass);
            return reflect ? new ReflectObject(constructor.construct()) : constructor.construct();
        }else {
            Class<?>[] parameterTypes = new Class<?>[parameters.length];
            for(int i = 0; i < parameterTypes.length; i++)
                parameterTypes[i] = parameters[i].getClass();
            
            ReflectConstructor constructor = new ReflectConstructor(objectClass, parameterTypes);
            return reflect ? new ReflectObject(constructor.construct(parameters)) : constructor.construct(parameters);
        }
    }
    
    public Object getFieldValue(String name) {
        return getFieldValue(name, null);
    }
    
    public Object getFieldValue(String name, Class<?> type) {
        ReflectField field = type == null ? new ReflectField(objectClass, name) : new ReflectField(objectClass, name, type);
        if(field.get() == null)
            new IllegalArgumentException("Field "+name+" was not found in "+objectClass.getName());
        
        return field.getValue(this.object);
    }
    
    public void setField(String name, Object value) {
        setField(name, null, value);
    }
    
    public void setField(String name, Class<?> type, Object value) {
        ReflectField field = type == null ? new ReflectField(objectClass, name) : new ReflectField(objectClass, name, type);
        if(field.get() == null)
            new IllegalArgumentException("Field "+name+" was not found in "+objectClass.getName());
        
        field.setValue(this.object, value);
    }
    
    public Object invokeMethod(String name) {
        return invokeMethod(name, null, true);
    }
    
    public Object invokeMethod(String name, Class<?> type) {
        return invokeMethod(name, type, true);
    }
    
    public Object invokeMethod(String name, Class<?> type, boolean ignoreParameterTypes, Object... parameters) {
        ReflectMethod method = null;
        
        Class<?>[] parameterTypes = new Class<?>[parameters.length];
        for(int i = 0; i < parameterTypes.length; i++)
            parameterTypes[i] = parameters[i].getClass();
        
        method = new ReflectMethod(objectClass, name, type, (ignoreParameterTypes || parameters == null ? parameterTypes : null));
        if(method.get() == null)
            new IllegalArgumentException("Method "+name+" was not found in "+objectClass.getName());
        
        return parameters == null ? method.invoke(object) : method.invoke(object, parameters);
    }
}
