package engine.command.suggestion;

import engine.command.CommandSender;

import java.util.List;

@FunctionalInterface
public interface Suggester {
    List<String> suggest(CommandSender sender, String command, String[] args);
}
