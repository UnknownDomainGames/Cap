package unknowndomain.command.simple;

import unknowndomain.command.Command;
import unknowndomain.command.CommandResult;
import unknowndomain.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class SimpleCommand extends Command {

    private CommandExecutor executor;
    private CommandCompleter completer;

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
        this.completer = completer;
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if(completer == null) {
            return Collections.emptyList();
        }

        return completer.complete(sender, this, args);
    }
}
