package engine.command.argument.base;

import engine.command.argument.Argument;
import engine.command.suggestion.Suggester;
import engine.command.util.context.Context;

import java.util.List;
import java.util.Optional;

public class FloatArgument extends Argument {
    @Override
    public String getName() {
        return "Float";
    }

    @Override
    public Class responsibleClass() {
        return Float.class;
    }

    @Override
    public Optional parse(Context context, String arg) {
        try {
            return Optional.of(Float.valueOf(arg));
        }catch (Exception e){
            return Optional.empty();
        }
    }

    @Override
    public Suggester getSuggester() {
        return (sender, command, args) -> List.of("[float]");
    }
}
