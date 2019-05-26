package unknowndomain.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class HashCommandManager extends CommandManager {

    private HashMap<String,Command> commandHashMap = new HashMap<>();

    @Override
    public void registeCommand(Command command) {
        commandHashMap.put(command.name,command);
    }

    @Override
    public CommandResult doCommand(CommandSender sender, String command, String[] args) {
        Command command1 = commandHashMap.get(command);
        if(command1==null)
            return new CommandResult(false,"command does not exist");
        return command1.execute(sender,args);
    }

    @Override
    public List<String> getCompleteList(CommandSender sender, String command, String[] args) {
        Command command1 = commandHashMap.get(command);
        if(command1==null)
            return new ArrayList<>();
        return command1.complete(sender,args);
    }

    @Override
    public void unregisterCommand(String command) {
        commandHashMap.remove(command);
    }
}
