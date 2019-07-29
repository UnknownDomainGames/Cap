package unknowndomain.command.argument.base;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import unknowndomain.command.argument.SimpleArgument;
import unknowndomain.command.completion.Completer;

import java.util.Collections;
import java.util.Optional;

public class StringArgument extends SimpleArgument {

    public StringArgument() {
        super(String.class, "String");
    }

    @Override
    public Optional parse(String arg) {
        return Optional.of(arg);
    }

    @Override
    public Completer getCompleter() {
        return (sender, command, args) -> {
            String s = args[args.length - 1];
            if (s.isEmpty())
                return Lists.newArrayList("[text]");
            else return Collections.emptyList();
        };
    }
}
