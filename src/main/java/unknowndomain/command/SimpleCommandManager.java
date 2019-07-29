package unknowndomain.command;

import unknowndomain.command.exception.CommandNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleCommandManager implements CommandManager {

    private final Map<String, Command> commands = new HashMap<>();

    private UncaughtExceptionHandler uncaughtExceptionHandler = (e, sender, command, args) -> e.printStackTrace();

    @Override
    public void registerCommand(Command command) {
        if (commands.containsKey(command.getName()))
            throw new RuntimeException("Command: [" + command.getName() + "] already exist");
        commands.put(command.getName(), command);
    }

    @Override
    public Optional<Command> getCommand(String command) {
        return Optional.ofNullable(commands.get(command));
    }

    @Override
    public boolean hasCommand(String command) {
        return commands.containsKey(command);
    }

    @Override
    public void executeCommand(CommandSender sender, String command, String... args) {
        Command commandInstance = commands.get(command);
        if (commandInstance == null)
            throw new CommandNotFoundException(command);

        if (args == null)
            args = new String[0];

        try {
            commandInstance.execute(sender, args);
        } catch (Exception e) {
            if (commandInstance.handleUncaughtException(e, sender, args)) {
                return;
            }

            uncaughtExceptionHandler.handle(e, sender, commandInstance, args);
        }
    }

    @Override
    public List<String> getCompleteList(CommandSender sender, String command, String... args) {
        Command commandInstance = commands.get(command);
        if ((args == null || args.length == 0) && commandInstance == null)
            return commands.keySet().stream().filter(commandName -> commandName.startsWith(command)).collect(Collectors.toList());

        if (commandInstance == null)
            return Collections.emptyList();

        return commandInstance.complete(sender, args);
    }

    @Override
    public void unregisterCommand(String command) {
        commands.remove(command);
    }

    public void setUncaughtExceptionHandler(UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }
}
