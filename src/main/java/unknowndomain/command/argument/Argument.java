package unknowndomain.command.argument;

import unknowndomain.command.CommandSender;

import java.util.List;
import java.util.Optional;

public abstract class Argument<T> {

    public abstract String getName();

    public abstract Class<T> responsibleClass();

    public abstract Optional<T> parse(String arg);

    public abstract List<String> getComplete(CommandSender sender, String arg);

}
