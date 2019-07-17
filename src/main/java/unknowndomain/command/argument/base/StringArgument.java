package unknowndomain.command.argument.base;

import com.google.common.collect.Sets;
import unknowndomain.command.CommandSender;
import unknowndomain.command.argument.SingleArgument;
import unknowndomain.command.completion.Completer;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class StringArgument extends SingleArgument {

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
                return Sets.newHashSet("[text]");
            else return Collections.emptySet();
        };
    }
}
