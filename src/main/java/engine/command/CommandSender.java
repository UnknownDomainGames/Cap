package engine.command;

import engine.permission.Permissible;

public interface CommandSender extends Permissible {

    void sendMessage(String message);

    String getSenderName();

    void handleException(CommandException exception);

}
