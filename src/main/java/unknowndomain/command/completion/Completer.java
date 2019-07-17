package unknowndomain.command.completion;

import unknowndomain.command.CommandSender;

import java.util.Set;

public interface Completer {
    Set<String> complete(CommandSender sender, String command, String[] args);
}
