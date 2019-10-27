package main.swing;

import nullengine.command.CommandSender;
import nullengine.permission.HashPermissible;

public class Entity implements CommandSender {

    private String name;
    private HashPermissible permissible = new HashPermissible();

    public Entity(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }

    @Override
    public String getSenderName() {
        return getName();
    }

    @Override
    public void sendMessage(String message) {
        System.out.println(name+" receive message:"+message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return permissible.hasPermission(permission);
    }

    @Override
    public void setPermission(String permission, boolean bool) {
        permissible.setPermission(permission,bool);
    }

    @Override
    public void removePermission(String permission) {
        permissible.removePermission(permission);
    }
}
