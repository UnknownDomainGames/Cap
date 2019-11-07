package nullengine.command;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommandManager {

    void registerCommand(Command command);

    void unregisterCommand(String command);

    Collection<Command> registerCommands();

    Optional<Command> getCommand(String command);

    boolean hasCommand(String command);

    void execute(CommandSender sender, String rawCommand);

    void execute(CommandSender sender, String command, String... args);

    List<String> complete(CommandSender sender, String rawCommand);

    List<String> complete(CommandSender sender, String command, String... args);

    List<String> getTips(CommandSender sender,String rawCommand);

    List<String> getTips(CommandSender sender,String command,String... args);

    ArgumentCheckResult checkLastArgument(CommandSender sender,String rawCommand);

    ArgumentCheckResult checkLastArgument(CommandSender sender,String command,String... args);

    @FunctionalInterface
    interface UncaughtExceptionHandler {
        void handle(Exception e, CommandSender sender, String command, String[] args);
    }
}
