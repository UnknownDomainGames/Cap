package engine.permission;

import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HashPermissible implements Permissible {

    private final Map<String, Boolean> permissionMap = new HashMap<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);

    @Override
    public boolean hasPermission(@Nonnull String permission) {
        Validate.notEmpty(permission);
        try {
            lock.readLock().lock();
            if (permissionMap.containsKey(permission)) {
                return permissionMap.get(permission);
            }
            while (true) {
                int lastDot = permission.lastIndexOf('.');
                if (lastDot <= 0) {
                    break;
                }
                permission = permission.substring(0, lastDot);
                if (permissionMap.containsKey(permission)) {
                    return permissionMap.get(permission);
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return false;
    }

    @Override
    public void setPermission(@Nonnull String permission, boolean bool) {
        Validate.notEmpty(permission);
        lock.writeLock().lock();
        permissionMap.put(permission, bool);
        lock.writeLock().unlock();
    }

    @Override
    public void removePermission(String permission) {
        lock.writeLock().lock();
        permissionMap.remove(permission);
        lock.writeLock().unlock();
    }

    @Override
    public void clearPermission() {
        lock.writeLock().lock();
        this.permissionMap.clear();
        lock.writeLock().unlock();
    }

    @Override
    public Map<String, Boolean> toPermissionMap() {
        return Map.copyOf(permissionMap);
    }
}
