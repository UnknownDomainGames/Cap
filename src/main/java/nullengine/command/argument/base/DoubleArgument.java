package nullengine.command.argument.base;

import com.google.common.collect.Lists;
import nullengine.command.argument.Argument;
import nullengine.command.suggestion.Suggester;

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
    public Suggester getSuggester() {
        return (sender, command, args) -> Lists.newArrayList("[double]");
    }
}
