package main.swing;

import engine.command.CommandFailure;
import engine.permission.HashPermissible;
import main.Entity;
import main.World;

import java.util.Map;

public class SwingEntity implements Entity {

    private static World world = new World("SwingWorld");
    private String name;
    private HashPermissible permissible = new HashPermissible();

    public SwingEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getSenderName() {
        return getName();
    }

    @Override
    public void sendCommandFailure(CommandFailure failure) {
        sendMessage(failure.toString());
    }

    @Override
    public void sendMessage(String message) {
        System.out.println(name + " receive message:" + message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return permissible.hasPermission(permission);
    }

    @Override
    public void setPermission(String permission, boolean bool) {
        permissible.setPermission(permission, bool);
    }

    @Override
    public void removePermission(String permission) {
        permissible.removePermission(permission);
    }

    @Override
    public void clearPermission() {
        permissible.clearPermission();
    }

    @Override
    public Map<String, Boolean> toPermissionMap() {
        return permissible.toPermissionMap();
    }

    @Override
    public World getWorld() {
        return world;
    }
}
