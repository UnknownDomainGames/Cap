package engine.command.argument.base;

import engine.command.argument.SimpleArgument;
import engine.command.suggestion.Suggester;
import engine.command.util.context.Context;

import java.util.List;
import java.util.Optional;

public class ShortArgument extends SimpleArgument {
    public ShortArgument() {
        super(Short.class,"Short");
    }

    @Override
    public Optional parse(Context context, String arg) {
        try {
            return Optional.of(Short.valueOf(arg));
        }catch (Exception e){
            return Optional.empty();
        }
    }

    @Override
    public Suggester getSuggester() {
        return (sender, command, args) -> List.of("[num]");
    }
}
