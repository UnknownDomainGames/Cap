package engine.command.argument.base;

import engine.command.argument.SimpleArgument;
import engine.command.suggestion.Suggester;

import java.util.List;
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
    public Suggester getSuggester() {
        return (sender, command, args) -> {
            String s = args[args.length - 1];
            if (s.isEmpty()) {
                return List.of("[text]");
            } else return List.of();
        };
    }
}
