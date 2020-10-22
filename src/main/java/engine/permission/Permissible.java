package engine.permission;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;

public interface Permissible {

    boolean hasPermission(@Nonnull String permission);

    default boolean hasPermission(Collection<String> permissions) {
        for (String permission : permissions) {
            if(!hasPermission(permission))
                return false;
        }
        return true;
    }

    void setPermission(@Nonnull String permission, boolean bool);

    void removePermission(String permission);

    void clearPermission();

    Map<String, Boolean> toPermissionMap();
}
