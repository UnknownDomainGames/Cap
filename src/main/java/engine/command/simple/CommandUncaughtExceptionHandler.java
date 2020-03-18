package engine.command.simple;

import engine.command.Command;
import engine.command.CommandSender;

@FunctionalInterface
public interface CommandUncaughtExceptionHandler {
    boolean handleUncaughtException(Exception e, CommandSender sender, Command command, String[] args);
}
