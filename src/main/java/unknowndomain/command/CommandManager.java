package unknowndomain.command;

import java.util.HashMap;

public abstract class CommandManager {

    private static CommandManager instance;

    public static CommandManager getInstance(){
        return instance;
    }

    public abstract void registeCommand(Command command);

    public abstract void doCommand(CommandSender sender,String command,String[] args);

}
