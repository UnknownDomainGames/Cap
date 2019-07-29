package unknowndomain.command.simple;

import unknowndomain.command.Command;
import unknowndomain.command.CommandSender;

@FunctionalInterface
public interface CommandExecutor {
    void execute(CommandSender executor, Command command, String[] args) throws Exception;
}
