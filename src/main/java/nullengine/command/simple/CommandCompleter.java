package nullengine.command.simple;

import nullengine.command.Command;
import nullengine.command.CommandSender;
import nullengine.command.completion.Completer;

import java.util.List;

@FunctionalInterface
public interface CommandCompleter {
    Completer.CompleteResult complete(CommandSender sender, Command command, String[] args);
}
