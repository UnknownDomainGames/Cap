package unknowndomain.command.argument.base;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import unknowndomain.command.argument.Argument;
import unknowndomain.command.completion.Completer;

import java.util.List;
import java.util.Optional;

public class DoubleArgument extends Argument {
    @Override
    public String getName() {
        return "Double";
    }

    @Override
    public Class responsibleClass() {
        return Double.class;
    }

    @Override
    public Optional parse(String arg) {
        return Optional.ofNullable(Double.valueOf(arg));
    }

    @Override
    public Completer getCompleter() {
        return (sender, command, args) -> Lists.newArrayList("[double]");
    }
}
