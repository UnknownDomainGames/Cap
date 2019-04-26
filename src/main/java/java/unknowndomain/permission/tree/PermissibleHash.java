package java.unknowndomain.permission.tree;

import java.unknowndomain.permission.Permissible;
import java.util.HashSet;
import java.util.Iterator;

public class PermissibleHash implements Permissible {

    private HashSet<String> permissionSet = new HashSet();

    @Override
    public synchronized boolean hasPermission(String permission) {
        String[] permissionSplit = permission.split(".");
        for(int i = 0;i<permissionSplit.length;i++){
            StringBuilder split = new StringBuilder();
            for(int j =0;j<i;j++){
                split.append(permissionSplit[j]);
                split.append('.');
            }
            split.append(permissionSplit[i]);
            if(permissionSet.contains(split.toString())){
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized void addPermission(String permission) {
        String[] permissionSplit = permission.split(".");

        for(int i = 0;i<permissionSplit.length;i++){
            StringBuilder split = new StringBuilder();
            for(int j =0;j<i;j++){
                split.append(permissionSplit[j]);
                split.append('.');
            }
            split.append(permissionSplit[i]);
            if(hasPermission(split.toString())){
                return;
            }
        }
        permissionSet.add(permission);

        Iterator<String> iterator = permissionSet.iterator();

        while(iterator.hasNext()){
            String p = iterator.next();
            if(p.startsWith(permission))
                iterator.remove();
        }
    }

    @Override
    public synchronized void removePermission(String permission) {
        permissionSet.remove(permission);
    }
}
