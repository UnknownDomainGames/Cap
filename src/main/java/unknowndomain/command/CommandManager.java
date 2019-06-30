package unknowndomain.command;

import java.util.List;
import java.util.Set;

public abstract class CommandManager {

    public abstract void registerCommand(Command command);

    public abstract CommandResult executeCommand(CommandSender sender, String command, String... args);

    public abstract Command getCommand(String command);

    public abstract boolean hasCommand(String command);

    public abstract Set<String> getCompleteList(CommandSender sender, String command, String... args);

    public abstract void unregisterCommand(String command);

}
