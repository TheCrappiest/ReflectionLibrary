Single or Multi-Class reflection library.
##
The purpose of this library is to provide an example of the usage for reflection.\
To use, simple copy the contents of the portion (multi or single) of the library you want to use into your project.


Example usage (Multi-Class);
```
Object playOutSpawnNamed = Class.fromName(<PacketPlayOutNamedEntitySpawn>).newInstance();
ReflectObject reflectSpawnNamed = new ReflectObject(playOutSpawnNamed);

reflectSpawnNamed.setField("a", entityId);
reflectSpawnNamed.setField("b", uuid);
reflectSpawnNamed.setField("c", location.getX());
reflectSpawnNamed.setField("d", location.getY());
reflectSpawnNamed.setField("e", location.getZ());
```
Example usage (Single-Class);
```
Object playOutSpawnNamed = Class.fromName(<PacketPlayOutNamedEntitySpawn>).newInstance();
reflect.getField(playOutSpawnNamed.getClass(), "a").set(playOutSpawnNamed, entityId);
```
