package nullengine.command.completion;

import nullengine.command.CommandSender;

import java.util.List;

public interface Completer {
    List<String> complete(CommandSender sender, String command, String[] args);
}
