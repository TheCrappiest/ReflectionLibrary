Simple java reflection library with 2 individually usable portions.\
The purpose of this library is to provide an example of the usage for reflection. To use, simply copy the contents of the portion from the library to your project.
##

Example usage (Multi-Class);
```
public class Example {
    private String value = "Reflection Testing";
    public Example(String s) { value = s; }
    public String getValue() { return value; }
}

ReflectObject reflectedExample = new ReflectObject(new ReflectConstructor(<Example>).construct());
String value = String.valueOf(reflectedExample.getFieldValue("value"));
System.out.println("Value: "+value);

reflectedExample.setField("value", "This value should be changed.");
value = String.valueOf(reflectedExample.getFieldValue("value"));
System.out.println("Value: "+value);

reflectedExample = (ReflectObject) reflectedExample.construct(true);
value = String.valueOf(reflectedExample.getFieldValue("value"));
System.out.println("Value: "+value);
```
##
Example usage (Single-Class);
```
ReflectionBase reflect = ReflectionBase.base();
Class<?> namedEntitySpawn = reflect.getClassFromPath("<PacketPlayOutNamedEntitySpawn>");
Constructor<?> constructor = reflect.findConstructor(namedEntitySpawn);
		
Object packet = null;
try {
    packet = constructor.newInstance();
} catch (Exception e) {
    e.printStackTrace();
}
```