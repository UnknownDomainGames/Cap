package java.unknowndomain.permission;

public interface Permissible {

    boolean hasPermission(String permission);

    void definePermission(String permission, boolean bool);

    void undefinePermission(String permission);

}
