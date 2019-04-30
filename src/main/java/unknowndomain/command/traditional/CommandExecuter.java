package unknowndomain.command.traditional;

import unknowndomain.command.CommandSender;

public interface CommandExecuter {
    boolean execute(CommandSender executor,String label, String[] args);
}
