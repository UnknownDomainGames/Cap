package engine.command;

import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseCommandManager implements CommandManager {

    private final Map<String, Command> commands = new HashMap<>();
    private final CommandParser resolver = createCommandParser();

    protected abstract CommandParser createCommandParser();

    @Override
    public void registerCommand(Command command) {
        if (commands.containsKey(command.getName()))
            throw new RuntimeException("Command \"" + command.getName() + "\" already exists");
        commands.put(command.getName(), command);
    }

    @Override
    public Collection<Command> registeredCommands() {
        return commands.values();
    }

    @Override
    public Optional<Command> getCommand(String name) {
        return Optional.ofNullable(commands.get(name));
    }

    @Override
    public boolean hasCommand(String name) {
        return commands.containsKey(name);
    }

    @Override
    public void execute(CommandSender sender, String command) {
        execute(sender, resolver.parse(command));
    }

    @Override
    public void execute(CommandSender sender, String name, String... args) {
        execute(sender, resolver.parse(name, args));
    }

    protected void execute(CommandSender sender, CommandParser.Result command) {
        Command commandInstance = commands.get(command.getName());
        if (commandInstance == null) {
            sender.sendCommandException(CommandException.commandNotFound(sender, command));
            return;
        }

        String[] args = command.getArgs();
        commandInstance.execute(sender, args != null ? args : new String[0]);
    }

    @Override
    public List<String> complete(CommandSender sender, String command) {
        CommandParser.Result result = resolver.parse(command);
        return complete(sender, result.getName(), result.getArgs());
    }

    @Override
    public List<String> complete(CommandSender sender, String commandName, String... args) {
        Command commandInstance = commands.get(commandName);
        if (commandInstance == null) {
            return commands.keySet()
                    .stream()
                    .filter(name -> name.startsWith(commandName))
                    .collect(Collectors.toList());
        }
        if (args == null || args.length == 0) {
            return List.of();
        }

        if (commandInstance == null) {
            return List.of();
        }

        return commandInstance.suggest(sender, args);
    }

    @Override
    public List<String> getTips(CommandSender sender, String command) {
        CommandParser.Result result = resolver.parse(command);
        return getTips(sender, result.getName(), result.getArgs());
    }

    @Override
    public List<String> getTips(CommandSender sender, String name, String... args) {
        if (name == null || name.isEmpty())
            return List.of();
        Command commandInstance = commands.get(name);
        if (commandInstance == null)
            return List.of();
        return commandInstance.getTips(sender, args);
    }

    @Override
    public ArgumentCheckResult checkLastArgument(CommandSender sender, String command) {
        CommandParser.Result result = this.resolver.parse(command);
        return checkLastArgument(sender, result.getName(), result.getArgs());
    }

    @Override
    public ArgumentCheckResult checkLastArgument(CommandSender sender, String name, String... args) {
        Command command1 = commands.get(name);
        if (command1 == null)
            return ArgumentCheckResult.Error("/" + name + "  command not found");
        return command1.checkLastArgument(sender, args);
    }

    @Override
    public void unregisterCommand(String name) {
        commands.remove(name);
    }
}
