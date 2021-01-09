package engine.command.argument;

import engine.command.suggestion.Suggester;
import engine.command.util.context.Context;

import java.util.Optional;

public abstract class Argument<T> {

    public abstract String getName();

    public abstract Class<T> responsibleClass();

    public abstract Optional<T> parse(Context context, String arg);

    public abstract Suggester getSuggester();

}

