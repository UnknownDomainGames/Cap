package unknowndomain.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

public class HashCommandManager extends CommandManager {

    private HashMap<String, Command> commandHashMap = new HashMap<>();

    private WeakHashMap<String, List<String>> completeCacheMap = new WeakHashMap<>();

    @Override
    public void registerCommand(Command command) {
        if(commandHashMap.containsKey(command.name))
            throw new RuntimeException("command: "+command.name+" already exist");
        commandHashMap.put(command.name, command);
    }

    @Override
    public CommandResult doCommand(CommandSender sender, String command, String[] args) {
        Command command1 = commandHashMap.get(command);
        if (command1 == null)
            return new CommandResult(false, "command does not exist");
        return command1.execute(sender, args);
    }

    @Override
    public Command getCommand(String command) {
        return commandHashMap.get(command);
    }

    @Override
    public List<String> getCompleteList(CommandSender sender, String command, String[] args) {

        if (args == null || args.length == 0) {
            if (completeCacheMap.containsKey(command)) {
                return completeCacheMap.get(command);
            } else {
                List list = commandHashMap.keySet().stream().filter(commandName -> commandName.startsWith(command)).collect(Collectors.toList());
                completeCacheMap.put(command, list);
                return list;
            }
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
