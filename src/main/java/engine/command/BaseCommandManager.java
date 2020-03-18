package engine.command;

import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseCommandManager implements CommandManager {

    private final Map<String, Command> commands = new HashMap<>();
    private final CommandResolver resolver = createCommandResolver();

    protected abstract CommandResolver createCommandResolver();

    @Override
    public void registerCommand(Command command) {
        if (commands.containsKey(command.getName()))
            throw new RuntimeException("Command \"" + command.getName() + "\" already exists");
        commands.put(command.getName(), command);
    }

    @Override
    public Collection<Command> registerCommands() {
        return commands.values();
    }

    @Override
    public Optional<Command> getCommand(String command) {
        return Optional.ofNullable(commands.get(command));
    }

    @Override
    public boolean hasCommand(String command) {
        return commands.containsKey(command);
    }

    @Override
    public void execute(CommandSender sender, String rawCommand) {
        execute(sender, resolver.resolve(rawCommand));
    }

    @Override
    public void execute(CommandSender sender, String command, String... args) {
        execute(sender, resolver.resolve(command, args));
    }

    protected void execute(CommandSender sender, CommandResolver.Result command) {
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
        CommandResolver.Result result = resolver.resolve(command);
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
            return Collections.EMPTY_LIST;
        }

        if (commandInstance == null) {
            return Collections.EMPTY_LIST;
        }

        return commandInstance.suggest(sender, args);
    }

    @Override
    public List<String> getTips(CommandSender sender, String rawCommand) {
        CommandResolver.Result result = resolver.resolve(rawCommand);
        return getTips(sender, result.getName(), result.getArgs());
    }

    @Override
    public List<String> getTips(CommandSender sender, String command, String... args) {
        if (command == null || command.isEmpty())
            return Collections.EMPTY_LIST;
        Command commandInstance = commands.get(command);
        if (commandInstance == null)
            return Collections.EMPTY_LIST;
        return commandInstance.getTips(sender, args);
    }

    @Override
    public ArgumentCheckResult checkLastArgument(CommandSender sender, String rawCommand) {
        CommandResolver.Result result = this.resolver.resolve(rawCommand);
        return checkLastArgument(sender, result.getName(), result.getArgs());
    }

    @Override
    public ArgumentCheckResult checkLastArgument(CommandSender sender, String command, String... args) {
        Command command1 = commands.get(command);
        if (command1 == null)
            return ArgumentCheckResult.Error("/" + command + "  command not found");
        return command1.checkLastArgument(sender, args);
    }

    @Override
    public void unregisterCommand(String command) {
        commands.remove(command);
    }
}
