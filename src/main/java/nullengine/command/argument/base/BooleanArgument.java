package nullengine.command.argument.base;

import com.google.common.collect.Lists;
import nullengine.command.argument.Argument;
import nullengine.command.completion.Completer;

import java.util.List;
import java.util.Optional;
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
            List<String> completeSet = Lists.newArrayList("true", "false");
            if (args != null && !args[args.length - 1].isEmpty())
                return Completer.CompleteResult.completeResult(completeSet.stream().filter(completeName -> completeName.startsWith(args[args.length - 1])).collect(Collectors.toList()));
            return Completer.CompleteResult.completeResult(completeSet);
        };
    }
}
