package main;

import engine.command.argument.Argument;
import engine.command.suggestion.Suggester;
import engine.command.util.context.Context;

import java.util.List;
import java.util.Optional;

public class WorldArgument extends Argument {
    @Override
    public String getName() {
        return "World";
    }

    @Override
    public Class responsibleClass() {
        return World.class;
    }

    @Override
    public Optional parse(Context context, String arg) {
        return Optional.of(new World(arg));
    }

    @Override
    public Suggester getSuggester() {
        return (sender, command, args) -> List.of("world");
    }
}
