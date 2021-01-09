package engine.command.util;

import java.util.HashMap;

public class Type {

    private static final HashMap<Class, Type> typeCache = new HashMap<>();

    private Class clazz;

    public Type(Class clazz) {
        this.clazz = clazz;
    }

    public boolean is(Type type) {
        return clazz.equals(type.clazz);
    }

    public static Type of(Class clazz) {
        return typeCache.computeIfAbsent(clazz, Type::new);
    }

}
