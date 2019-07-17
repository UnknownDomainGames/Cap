package unknowndomain.command;

import unknowndomain.command.exception.CommandNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

public class HashCommandManager extends CommandManager {

    private Map<String, Command> commandHashMap = new HashMap<>();

    @Override
    public void registerCommand(Command command) {
        if (commandHashMap.containsKey(command.name))
            throw new RuntimeException("command: [" + command.name + "] already exist");
        commandHashMap.put(command.name, command);
    }

    @Override
    public CommandResult executeCommand(CommandSender sender, String command, String... args) {
        Command command1 = commandHashMap.get(command);
        if (command1 == null)
            return new CommandResult(new CommandNotFoundException(command));

        if (args == null)
            args = new String[0];
        return command1.execute(sender, args);
    }

    @Override
    public Command getCommand(String command) {
        return commandHashMap.get(command);
    }

    @Override
    public boolean hasCommand(String command) {
        return commandHashMap.containsKey(command);
    }

    @Override
    public Set<String> getCompleteList(CommandSender sender, String command, String... args) {

        Command command1 = commandHashMap.get(command);
        if ((args == null || args.length == 0)&& command1==null)
            return commandHashMap.keySet().stream().filter(commandName -> commandName.startsWith(command)).collect(Collectors.toSet());

        if (command1 == null)
            return new HashSet<>();
        return command1.complete(sender, args);
    }

    @Override
    public void unregisterCommand(String command) {
        commandHashMap.remove(command);
    }
}
