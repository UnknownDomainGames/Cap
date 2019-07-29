package unknowndomain.command.simple;

import unknowndomain.command.Command;
import unknowndomain.command.CommandSender;

@FunctionalInterface
public interface CommandUncaughtExceptionHandler {
    boolean handleUncaughtException(Exception e, CommandSender sender, Command command, String[] args);
}
