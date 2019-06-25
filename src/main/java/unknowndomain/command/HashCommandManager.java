package unknowndomain.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            return new CommandResult(false, "command does not exist");

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
    public List<String> getCompleteList(CommandSender sender, String command, String... args) {

        if (args == null || args.length == 0) {
            List list = commandHashMap.keySet().stream().filter(commandName -> commandName.startsWith(command)).collect(Collectors.toList());
            return list;
        }

        Command command1 = commandHashMap.get(command);
        if (command1 == null)
            return new ArrayList<>();
        return command1.complete(sender, args);
    }

    @Override
    public void unregisterCommand(String command) {
        commandHashMap.remove(command);
    }
}
