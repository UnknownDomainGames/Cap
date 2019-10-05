package nullengine.command.simple;

import nullengine.command.Command;
import nullengine.command.CommandSender;

@FunctionalInterface
public interface CommandUncaughtExceptionHandler {
    boolean handleUncaughtException(Exception e, CommandSender sender, Command command, String[] args);
}
