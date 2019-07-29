package unknowndomain.command;

import java.util.List;
import java.util.Optional;

public interface CommandManager {

    void registerCommand(Command command);

    void unregisterCommand(String command);

    Optional<Command> getCommand(String command);

    boolean hasCommand(String command);

    void executeCommand(CommandSender sender, String command, String... args);

    List<String> getCompleteList(CommandSender sender, String command, String... args);

    @FunctionalInterface
    interface UncaughtExceptionHandler {
        void handle(Exception e, CommandSender sender, Command command, String[] args);
    }
}
