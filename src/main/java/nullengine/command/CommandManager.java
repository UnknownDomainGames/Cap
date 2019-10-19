package nullengine.command;

import nullengine.command.completion.Completer;

import java.util.List;
import java.util.Optional;

public interface CommandManager {

    void registerCommand(Command command);

    void unregisterCommand(String command);

    Optional<Command> getCommand(String command);

    boolean hasCommand(String command);

    void execute(CommandSender sender, String rawCommand);

    void execute(CommandSender sender, String command, String... args);

    Completer.CompleteResult complete(CommandSender sender, String rawCommand);

    Completer.CompleteResult complete(CommandSender sender, String command, String... args);

    @FunctionalInterface
    interface UncaughtExceptionHandler {
        void handle(Exception e, CommandSender sender, Command command, String[] args);
    }
}
