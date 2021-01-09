package engine.command;

import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseCommandManager implements CommandManager {

    private final Map<String, Command> commands = new HashMap<>();
    private final CommandParser parser = createCommandParser();
    private final CommandExceptionHandler exceptionHandler = createExceptionHandler();

    protected abstract CommandParser createCommandParser();

    protected abstract CommandExceptionHandler createExceptionHandler();

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
            sender.sendCommandFailure(new CommandFailure(CommandFailure.Type.COMMAND_NOT_FOUND, sender, name, args));
            return;
        }
        try {
            command.execute(sender, args != null ? args : new String[0]);
        } catch (Exception e) {
            exceptionHandler.handleOnExecuting(e);
        }
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

        try {
            return command.suggest(sender, args);
        } catch (Exception e) {
            exceptionHandler.handleOnSuggesting(e);
            return List.of();
        }
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
        Command command = commands.get(name.toLowerCase());
        if (command == null)
            return List.of();
        try {
            return command.getTips(sender, args);
        } catch (Exception e) {
            exceptionHandler.handleOnGettingTips(e);
            return List.of();
        }
    }

    @Override
    public ArgumentCheckResult checkLastArgument(CommandSender sender, String command) {
        CommandParser.Result result = this.parser.parse(command);
        return checkLastArgument(sender, result.getName(), result.getArgs());
    }

    @Override
    public ArgumentCheckResult checkLastArgument(CommandSender sender, String name, String... args) {
        Command command = commands.get(name.toLowerCase());
        if (command == null)
            return ArgumentCheckResult.Error("/" + name + "  command not found"); // TODO: L10n
        try {
            return command.checkLastArgument(sender, args);
        } catch (Exception e) {
            exceptionHandler.handleOnCheckingArgument(e);
            return ArgumentCheckResult.Error("Caught an exception when check last argument"); // TODO: L10n
        }
    }

    @Override
    public void unregisterCommand(String name) {
        commands.remove(name);
    }
}
