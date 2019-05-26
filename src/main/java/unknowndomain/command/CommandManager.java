package unknowndomain.command;

import java.util.HashMap;
import java.util.List;

public abstract class CommandManager {

    private static CommandManager instance;

    static {
        instance = new HashCommandManager();
    }

    public static CommandManager getInstance(){
        return instance;
    }

    public abstract void registeCommand(Command command);

    public abstract CommandResult doCommand(CommandSender sender,String command,String[] args);

    public abstract List<String> getCompleteList(CommandSender sender, String command, String[] args);

    public abstract void unregisterCommand(String command);

}
