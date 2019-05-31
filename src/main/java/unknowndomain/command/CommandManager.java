package unknowndomain.command;

import java.util.List;

public abstract class CommandManager {

    private static CommandManager instance;

    static {
        instance = new HashCommandManager();
    }

    public static CommandManager getInstance(){
        return instance;
    }

    public abstract void registerCommand(Command command);

    public abstract CommandResult executeCommand(CommandSender sender, String command, String... args);

    public abstract Command getCommand(String command);

    public abstract List<String> getCompleteList(CommandSender sender, String command, String... args);

    public abstract void unregisterCommand(String command);

}
