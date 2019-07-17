package unknowndomain.command.argument.base;

import com.google.common.collect.Sets;
import unknowndomain.command.CommandSender;
import unknowndomain.command.argument.Argument;
import unknowndomain.command.completion.Completer;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class BooleanArgument extends Argument {
    @Override
    public String getName() {
        return "Boolean";
    }

    @Override
    public Class responsibleClass() {
        return Boolean.class;
    }

    @Override
    public Optional parse(String arg) {
        return Optional.ofNullable(Boolean.valueOf(arg));
    }

    @Override
    public Completer getCompleter() {
        return (sender, command, args) -> {
            Set<String> completeSet = Sets.newHashSet("true", "false");
            if (args != null && !args[args.length - 1].isEmpty())
                return completeSet.stream().filter(completeName -> completeName.startsWith(args[args.length - 1])).collect(Collectors.toSet());
            return completeSet;
        };
    }
}
