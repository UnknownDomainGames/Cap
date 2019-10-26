package nullengine.command.argument;

import nullengine.command.suggestion.Suggester;

import java.util.Optional;

public abstract class Argument<T> {

    public abstract String getName();

    public abstract Class<T> responsibleClass();

    public abstract Optional<T> parse(String arg);

    public abstract Suggester getSuggester();

}

