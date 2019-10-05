package nullengine.command.simple;

import nullengine.command.Command;
import nullengine.command.CommandSender;

@FunctionalInterface
public interface CommandExecutor {
    void execute(CommandSender executor, Command command, String[] args) throws Exception;
}
