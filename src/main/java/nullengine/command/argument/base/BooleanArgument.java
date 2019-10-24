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

    /**
     * arg must be 'true' or 'false'.
     * do not use Boolean.valueOf or Boolean.parseBoolean,cause:
     * if arg is normal string it will return false.
     * @param arg
     * @return
     */
    @Override
    public Optional parse(String arg) {
        if(arg.equals("true"))
            return Optional.of(true);
        else if(arg.equals("false"))
            return Optional.of(false);
        return Optional.empty();
    }

    @Override
    public Completer getCompleter() {
        return (sender, command, args) -> {
            List<String> completeSet = Lists.newArrayList("true", "false");
            if (args != null && !args[args.length - 1].isEmpty())
                return completeSet.stream().filter(completeName -> completeName.startsWith(args[args.length - 1])).collect(Collectors.toList());
            return completeSet;
        };
    }
}
