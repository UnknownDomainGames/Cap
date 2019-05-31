package unknowndomain.command.traditional;

import unknowndomain.command.CommandSender;

import java.util.List;

public interface CommandCompleter {
    List<String> complete(CommandSender executor, String label, String[] args);
}
