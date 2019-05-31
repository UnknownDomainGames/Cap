package unknowndomain.command.traditional;

import unknowndomain.command.Command;
import unknowndomain.command.CommandResult;
import unknowndomain.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TraditionalCommand extends Command {

    private CommandExecutor executor;
    private Optional<CommandCompleter> completer;

    public TraditionalCommand(String name) {
        super(name);
    }

    public TraditionalCommand(String name, CommandExecutor executor) {
        super(name);
        this.executor = executor;
    }

    @Override
    public CommandResult execute(CommandSender executor, String[] args) {
        if (this.executor == null)
            return new CommandResult(false, "no execute in " + this.name);
        return this.executor.execute(executor, this, args);
    }

    public void setExecutor(CommandExecutor executor) {
        this.executor = executor;
    }

    public void setCompleter(CommandCompleter completer) {
        this.completer = Optional.ofNullable(completer);
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return completer.orElse((sender1, string, args1) -> Collections.emptyList()).complete(sender, this.name, args);
    }
}
