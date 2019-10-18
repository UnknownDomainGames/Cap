package nullengine.command.util;

public interface ClassUtil {

    static Class packing(Class clazz) {
        switch (clazz.getName()) {
            case "int":
                return Integer.class;
            case "float":
                return Float.class;
            case "boolean":
                return Boolean.class;
            case "double":
                return Double.class;
            case "char":
                return Character.class;
            case "long":
                return Long.class;
            case "short":
                return Short.class;
        }
        return clazz;
    }


}
