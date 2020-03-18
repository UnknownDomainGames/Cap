package engine.command.simple;

import engine.command.Command;
import engine.command.CommandSender;

import java.util.List;

@FunctionalInterface
public interface CommandSuggester {
    List<String> suggest(CommandSender sender, Command command, String[] args);
}
