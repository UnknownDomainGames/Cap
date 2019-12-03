package nullengine.command;

import nullengine.permission.Permissible;

public interface CommandSender extends Permissible {

    void sendMessage(String message);

    String getSenderName();

    void handleException(CommandException exception);

}
