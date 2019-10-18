package nullengine.command.simple;

import nullengine.command.Command;
import nullengine.command.CommandSender;
import nullengine.command.completion.Completer;

import java.util.Collections;
import java.util.List;

public class SimpleCommand extends Command {

    private CommandExecutor executor;
    private CommandCompleter completer;
    private CommandUncaughtExceptionHandler uncaughtExceptionHandler;

    public SimpleCommand(String name) {
        super(name);
    }

    public SimpleCommand(String name, CommandExecutor executor) {
        super(name);
        this.executor = executor;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (sender != null) {
            executor.execute(sender, this, args);
        }
    }

    @Override
    public Completer.CompleteResult complete(CommandSender sender, String[] args) {
        if (completer == null) {
            return Completer.CompleteResult.EMPTY;
        }

        return completer.complete(sender, this, args);
    }

    @Override
    public boolean handleUncaughtException(Exception e, CommandSender sender, String[] args) {
        if (uncaughtExceptionHandler == null)
            return false;
        return uncaughtExceptionHandler.handleUncaughtException(e, sender, this, args);
    }

    public void setExecutor(CommandExecutor executor) {
        this.executor = executor;
    }

    public void setCompleter(CommandCompleter completer) {
        this.completer = completer;
    }

    public void setUncaughtExceptionHandler(CommandUncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }
}
