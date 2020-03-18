package engine.command;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommandManager {

    void registerCommand(Command command);

    void unregisterCommand(String name);

    Collection<Command> registeredCommands();

    Optional<Command> getCommand(String name);

    boolean hasCommand(String name);

    void execute(CommandSender sender, String command);

    void execute(CommandSender sender, String name, String... args);

    List<String> complete(CommandSender sender, String command);

    List<String> complete(CommandSender sender, String name, String... args);

    List<String> getTips(CommandSender sender, String command);

    List<String> getTips(CommandSender sender, String name, String... args);

    ArgumentCheckResult checkLastArgument(CommandSender sender, String command);

    ArgumentCheckResult checkLastArgument(CommandSender sender, String name, String... args);
}
