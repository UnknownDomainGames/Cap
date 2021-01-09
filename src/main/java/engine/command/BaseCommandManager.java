package engine.command;

import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseCommandManager implements CommandManager {

    private final Map<String, Command> commands = new HashMap<>();
    private final CommandParser parser = createCommandParser();

    protected abstract CommandParser createCommandParser();

    @Override
    public void registerCommand(Command command) {
        if (hasCommand(command.getName()))
            throw new RuntimeException("Command \"" + command.getName() + "\" already exists");
        commands.put(command.getName().toLowerCase(), command);
    }

    @Override
    public Collection<Command> registeredCommands() {
        return commands.values();
    }

    @Override
    public Optional<Command> getCommand(String name) {
        return Optional.ofNullable(commands.get(name.toLowerCase()));
    }

    @Override
    public boolean hasCommand(String name) {
        return commands.containsKey(name.toLowerCase());
    }

    @Override
    public void execute(CommandSender sender, String command) {
        CommandParser.Result parsedCommand = parser.parse(command);
        execute(sender, parsedCommand.getName(), parsedCommand.getArgs());
    }

    @Override
    public void execute(CommandSender sender, String name, String... args) {
        Command command = commands.get(name.toLowerCase());
        if (command == null) {
            sender.sendCommandException(new CommandException(CommandException.Type.COMMAND_NOT_FOUND, sender, name, args));
            return;
        }
        command.execute(sender, args != null ? args : new String[0]);
    }

    @Override
    public List<String> complete(CommandSender sender, String command) {
        CommandParser.Result result = parser.parse(command);
        return complete(sender, result.getName(), result.getArgs());
    }

    @Override
    public List<String> complete(CommandSender sender, String commandName, String... args) {
        Command command = commands.get(commandName.toLowerCase());
        if (command == null) {
            return commands.keySet()
                    .stream()
                    .filter(name -> name.startsWith(commandName))
                    .collect(Collectors.toList());
        }

        if (args == null || args.length == 0) {
            return List.of();
        }

        return command.suggest(sender, args);
    }

    @Override
    public List<String> getTips(CommandSender sender, String command) {
        CommandParser.Result result = parser.parse(command);
        return getTips(sender, result.getName(), result.getArgs());
    }

    @Override
    public List<String> getTips(CommandSender sender, String name, String... args) {
        if (name == null || name.isEmpty())
            return List.of();
        Command commandInstance = commands.get(name.toLowerCase());
        if (commandInstance == null)
            return List.of();
        return commandInstance.getTips(sender, args);
    }

    @Override
    public ArgumentCheckResult checkLastArgument(CommandSender sender, String command) {
        CommandParser.Result result = this.parser.parse(command);
        return checkLastArgument(sender, result.getName(), result.getArgs());
    }

    @Override
    public ArgumentCheckResult checkLastArgument(CommandSender sender, String name, String... args) {
        Command command1 = commands.get(name.toLowerCase());
        if (command1 == null)
            return ArgumentCheckResult.Error("/" + name + "  command not found");
        return command1.checkLastArgument(sender, args);
    }

    @Override
    public void unregisterCommand(String name) {
        commands.remove(name);
    }
}
