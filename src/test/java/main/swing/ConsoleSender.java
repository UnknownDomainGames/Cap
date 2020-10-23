package main.swing;


import engine.command.CommandException;
import engine.command.CommandSender;

import javax.annotation.Nonnull;
import java.util.Map;

public class ConsoleSender implements CommandSender {
    @Override
    public String getSenderName() {
        return "console";
    }

    @Override
    public void sendMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void sendCommandException(CommandException exception) {
        System.out.println(exception.toString());
    }

    @Override
    public boolean hasPermission(@Nonnull String permission) {
        return true;
    }

    @Override
    public void setPermission(@Nonnull String permission, boolean bool) {
        throw new UnsupportedOperationException("Cannot set permission to console");
    }

    @Override
    public void removePermission(String permission) {
        throw new UnsupportedOperationException("Cannot remove permission to console");
    }

    @Override
    public void clearPermission() {
        throw new UnsupportedOperationException("Cannot clear permission to console");
    }

    @Override
    public Map<String, Boolean> toPermissionMap() {
        return Map.of();
    }
}
