package main;

import nullengine.command.argument.Argument;
import nullengine.command.suggestion.Suggester;

import java.util.Collections;
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
    public Optional parse(String arg) {
        return Optional.of(new World(arg));
    }

    @Override
    public Suggester getSuggester() {
        return (sender, command, args) -> List.of("world");
    }
}
