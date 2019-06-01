package unknowndomain.command;

import unknowndomain.permission.Permissible;

public interface CommandSender {

    void sendMessage(String message);

    String getSenderName();

    Permissible getPermissible();

}
