package unknowndomain.command.completion;

import unknowndomain.command.CommandSender;

import java.util.List;
import java.util.Set;

public interface Completer {
    List<String> complete(CommandSender sender, String command, String[] args);
}
