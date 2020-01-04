package nullengine.permission;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HashPermissible implements Permissible {

    private final Map<String, Boolean> permissionMap = new HashMap<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);

    @Override
    public boolean hasPermission(String permission) {
        if (permission == null || permission.isEmpty()){
            return false;
        }
        try {
            lock.readLock().lock();
            if (permissionMap.containsKey(permission)){
                return permissionMap.get(permission);
            }
            while (true) {
                int lastDot = permission.lastIndexOf('.');
                if (lastDot <= 0){
                    break;
                }
                permission = permission.substring(0, lastDot);
                if (permissionMap.containsKey(permission)){
                    return permissionMap.get(permission);
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return false;
    }

    @Override
    public void setPermission(String permission, boolean bool) {
        if (permission == null)
            return;
        lock.writeLock().lock();
        permissionMap.put(permission, bool);
        lock.writeLock().unlock();
    }

    public void removePermission(String permission) {
        lock.writeLock().lock();
        permissionMap.remove(permission);
        lock.writeLock().unlock();
    }

    public Map<String, Boolean> getBackingMap() {
        return Collections.unmodifiableMap(permissionMap);
    }

    @Override
    public void clean() {
        lock.writeLock().lock();
        this.permissionMap.clear();
        lock.writeLock().unlock();
    }
}
