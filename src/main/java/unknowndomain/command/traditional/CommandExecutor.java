package unknowndomain.command.traditional;

import unknowndomain.command.Command;
import unknowndomain.command.CommandResult;
import unknowndomain.command.CommandSender;

public interface CommandExecutor {
    CommandResult execute(CommandSender executor, Command command, String[] args);
}
