package nullengine.command.simple;

import nullengine.command.Command;
import nullengine.command.CommandSender;
import nullengine.command.completion.CompleteResult;
import nullengine.command.completion.Completer;

import java.util.List;

@FunctionalInterface
public interface CommandCompleter {
    List<String> complete(CommandSender sender, Command command, String[] args);
}
