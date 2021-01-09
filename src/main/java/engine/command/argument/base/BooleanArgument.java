package engine.command.argument.base;

import engine.command.argument.Argument;
import engine.command.suggestion.Suggester;
import engine.command.util.context.Context;

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
    public Optional parse(Context context, String arg) {
        if(arg.equals("true")){
            return Optional.of(true);
        }
        else if(arg.equals("false")){
            return Optional.of(false);
        }
        return Optional.empty();
    }

    @Override
    public Suggester getSuggester() {
        return (sender, command, args) -> {
            List<String> completeSet = List.of("true", "false");
            if (args != null && !args[args.length - 1].isEmpty()){
                return completeSet.stream().filter(completeName -> completeName.startsWith(args[args.length - 1])).collect(Collectors.toList());
            }
            return completeSet;
        };
    }
}
