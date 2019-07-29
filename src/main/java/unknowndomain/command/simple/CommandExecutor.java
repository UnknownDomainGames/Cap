package unknowndomain.command.simple;

import unknowndomain.command.Command;
import unknowndomain.command.CommandResult;
import unknowndomain.command.CommandSender;

@FunctionalInterface
public interface CommandExecutor {
    CommandResult execute(CommandSender executor, Command command, String[] args);
}
