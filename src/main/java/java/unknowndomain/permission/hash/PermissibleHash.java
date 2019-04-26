package java.unknowndomain.permission.hash;

import java.unknowndomain.permission.Permissible;
import java.util.HashMap;

public class PermissibleHash implements Permissible {

    private HashMap<String, Boolean> permissionMap = new HashMap<>();

    @Override
    public boolean hasPermission(String permission) {
        if(permission==null||permission.isEmpty())
            return false;
        if(permissionMap.containsKey(permission))
            return permissionMap.get(permission);
        return hasPermission(permission.substring(0,permission.lastIndexOf('.')));
    }

    @Override
    public void setPermission(String permission, boolean bool) {
        permissionMap.put(permission,bool);
    }

    public void undefinePermission(String permission) {
        permissionMap.remove(permission);
    }
}
