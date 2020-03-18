package main.swing;

import engine.command.CommandException;
import engine.permission.HashPermissible;
import main.Entity;
import main.World;

import javax.annotation.Nonnull;

public class SwingEntity implements Entity {

    private static World world = new World("SwingWorld");
    private String name;
    private HashPermissible permissible = new HashPermissible();

    public SwingEntity(String name) {
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
    public void handleException(CommandException exception) {
        sendMessage(exception.getException().toString());
    }

    @Override
    public void sendMessage(String message) {
        System.out.println(name+" receive message:"+message);
    }

    @Override
    public boolean hasPermission(@Nonnull String permission) {
        return permissible.hasPermission(permission);
    }

    @Override
    public void setPermission(@Nonnull String permission, boolean bool) {
        permissible.setPermission(permission, bool);
    }

    @Override
    public void removePermission(String permission) {
        permissible.removePermission(permission);
    }

    @Override
    public void clearPermission() {

    }

    @Override
    public World getWorld() {
        return world;
    }
}
