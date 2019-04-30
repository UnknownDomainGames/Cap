package unknowndomain.command.traditional;

import unknowndomain.command.CommandSender;

public interface CommandExecutor {
    boolean execute(CommandSender executor,String label, String[] args);
}
