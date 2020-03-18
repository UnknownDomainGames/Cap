package engine.permission;

public interface Permissible {

    boolean hasPermission(String permission);

    void setPermission(String permission, boolean bool);

    void removePermission(String permission);

    void clean();
}
