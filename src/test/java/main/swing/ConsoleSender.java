package main.swing;

import nullengine.command.CommandException;
import nullengine.command.CommandSender;
import nullengine.permission.HashPermissible;

public class ConsoleSender implements CommandSender {
    private HashPermissible permissible = new HashPermissible();
    @Override
    public void sendMessage(String message) {
        System.out.println(message);
    }

    @Override
    public String getSenderName() {
        return "console";
    }

    @Override
    public void handleException(CommandException exception) {
        System.out.println(exception.toString());
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    public void setPermission(String permission, boolean bool) {}

    @Override
    public void removePermission(String permission) {}

    @Override
    public void clean() {

    }
}
