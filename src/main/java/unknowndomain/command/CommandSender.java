package unknowndomain.command;

import unknowndomain.permission.Permissible;

public interface CommandSender extends Permissible {

    void sendMessage(String message);

    String getSenderName();

}
