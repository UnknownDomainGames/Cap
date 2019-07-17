package unknowndomain.command.simple;

import unknowndomain.command.Command;
import unknowndomain.command.CommandResult;
import unknowndomain.command.CommandSender;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class SimpleCommand extends Command {

    private CommandExecutor executor;
    private Optional<CommandCompleter> completer;

    public SimpleCommand(String name) {
        super(name);
    }

    public SimpleCommand(String name, CommandExecutor executor) {
        super(name);
        this.executor = executor;
    }

    @Override
    public CommandResult execute(CommandSender executor, String[] args) {
        if (this.executor == null)
            return new CommandResult(false, "no executor in " + this.name);
        return this.executor.execute(executor, this, args);
    }

    public void setExecutor(CommandExecutor executor) {
        this.executor = executor;
    }

    public void setCompleter(CommandCompleter completer) {
        this.completer = Optional.ofNullable(completer);
    }

    @Override
    public Set<String> complete(CommandSender sender, String[] args) {
        return completer.orElse((sender1, string, args1) -> Collections.emptySet()).complete(sender, this.name, args);
    }
}