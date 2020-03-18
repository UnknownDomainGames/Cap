package engine.permission;

import javax.annotation.Nonnull;
import java.util.Map;

public interface Permissible {

    boolean hasPermission(@Nonnull String permission);

    void setPermission(@Nonnull String permission, boolean bool);

    void removePermission(String permission);

    void clearPermission();

    Map<String, Boolean> toPermissionMap();
}
