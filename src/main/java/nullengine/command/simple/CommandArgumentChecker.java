package nullengine.command.simple;

import nullengine.command.ArgumentCheckResult;
import nullengine.command.CommandSender;

public interface CommandArgumentChecker {
    ArgumentCheckResult checkArguments(CommandSender sender, String[] args);
}
