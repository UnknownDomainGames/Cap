package unknowndomain.command;

import unknowndomain.command.argument.SimpleArgumentManager;

import java.util.List;

public abstract class CommandManager {

    public abstract void registerCommand(Command command);

    public abstract CommandResult executeCommand(CommandSender sender, String command, String... args);

    public abstract Command getCommand(String command);

    public abstract List<String> getCompleteList(CommandSender sender, String command, String... args);

    public abstract void unregisterCommand(String command);

}
