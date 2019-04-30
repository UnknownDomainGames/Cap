package unknowndomain.command;

import java.util.HashMap;
import java.util.Optional;

public class HashCommandManager extends CommandManager {

    private HashMap<String,Command> commandHashMap = new HashMap<>();

    @Override
    public void registeCommand(Command command) {
        commandHashMap.put(command.name,command);
    }

    @Override
    public void doCommand(CommandSender sender, String command, String[] args) {
        Optional.ofNullable(commandHashMap.get(command)).ifPresent(command1 -> command1.execute(sender,args));
    }

    @Override
    public void unregisterCommand(String command) {
        commandHashMap.remove(command);
    }
}
