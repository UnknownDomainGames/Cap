package main;

import engine.command.CommandException;
import engine.command.CommandSender;
import engine.permission.HashPermissible;

import java.util.Map;
import java.util.function.Consumer;

public class TestSender implements CommandSender {

    private String name;

    private HashPermissible permissible = new HashPermissible();

    private Consumer<String> sendConsumer;

    private Consumer<CommandException> commandExceptionConsumer;

    public TestSender(String name, Consumer<String> sendConsumer, Consumer<CommandException> commandExceptionConsumer) {
        this.name = name;
        this.sendConsumer = sendConsumer;
        this.commandExceptionConsumer = commandExceptionConsumer;
    }

    @Override
    public void sendMessage(String message) {
        sendConsumer.accept(message);
    }

    @Override
    public String getSenderName() {
        return name;
    }

    @Override
    public void sendCommandException(CommandException exception) {
        System.out.println(exception);
        commandExceptionConsumer.accept(exception);
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
}
