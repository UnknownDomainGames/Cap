package engine.command.simple;

import engine.command.ArgumentCheckResult;
import engine.command.CommandSender;

public interface CommandArgumentChecker {
    ArgumentCheckResult checkArguments(CommandSender sender, String[] args);
}
