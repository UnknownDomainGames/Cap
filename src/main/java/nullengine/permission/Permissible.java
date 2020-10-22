package nullengine.permission;

import java.util.Collection;

public interface Permissible {

    boolean hasPermission(String permission);

    default boolean hasPermission(Collection<String> permissions) {
        for (String permission : permissions) {
            if(!hasPermission(permission))
                return false;
        }
        return true;
    }

    void setPermission(String permission, boolean bool);

    void removePermission(String permission);

    void clean();
}
