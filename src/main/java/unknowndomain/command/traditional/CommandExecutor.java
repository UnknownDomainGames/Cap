package unknowndomain.command.traditional;

import unknowndomain.command.CommandResult;
import unknowndomain.command.CommandSender;

public interface CommandExecutor {
    CommandResult execute(CommandSender executor, String label, String[] args);
}
