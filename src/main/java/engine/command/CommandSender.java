package engine.command;

import engine.permission.Permissible;

public interface CommandSender extends Permissible {

    String getSenderName();

    void sendMessage(String message);

    void sendCommandFailure(CommandFailure failure);
}
