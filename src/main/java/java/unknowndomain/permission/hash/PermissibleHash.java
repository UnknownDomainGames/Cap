package java.unknowndomain.permission.hash;

import java.unknowndomain.permission.Permissible;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PermissibleHash implements Permissible {

    private HashMap<String, Boolean> permissionMap = new HashMap<>();

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public boolean hasPermission(String permission) {
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        try {
            if (permission == null || permission.isEmpty())
                return false;
            readLock.lock();
            if (permissionMap.containsKey(permission))
                return permissionMap.get(permission);
            while(true){
                int lastDot = permission.lastIndexOf('.');
                if(lastDot<=0)
                    break;
                permission = permission.substring(0,lastDot);
                if (permissionMap.containsKey(permission))
                    return permissionMap.get(permission);
            }
        } finally {
            readLock.unlock();
        }
        return false;
    }

    @Override
    public void definePermission(String permission, boolean bool) {
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        writeLock.lock();
        permissionMap.put(permission, bool);
        writeLock.unlock();
    }

    public void undefinePermission(String permission) {
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        writeLock.lock();
        permissionMap.remove(permission);
        writeLock.unlock();
    }


}
