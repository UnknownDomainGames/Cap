package unknowndomain.command.simple;

import unknowndomain.command.CommandSender;

import java.util.Set;

public interface CommandCompleter {

    Set<String> complete(CommandSender sender, String label, String[] args);
}
