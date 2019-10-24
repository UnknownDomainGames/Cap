package nullengine.command.completion;

import nullengine.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@FunctionalInterface
public interface Completer {
    List<String> complete(CommandSender sender, String command, String[] args);
}
