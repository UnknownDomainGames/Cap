package unknowndomain.command.simple;

import unknowndomain.command.Command;
import unknowndomain.command.CommandSender;

import java.util.List;
import java.util.Set;

@FunctionalInterface
public interface CommandCompleter {
    List<String> complete(CommandSender sender, Command command, String[] args);
}
