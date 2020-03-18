package engine.command.simple;

import engine.command.Command;
import engine.command.CommandSender;

@FunctionalInterface
public interface CommandExecutor {
    void execute(CommandSender sender, Command command, String[] args);
}
