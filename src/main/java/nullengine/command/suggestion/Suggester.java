package nullengine.command.suggestion;

import nullengine.command.CommandSender;

import java.util.List;

@FunctionalInterface
public interface Suggester {
    List<String> suggest(CommandSender sender, String command, String[] args);
}
