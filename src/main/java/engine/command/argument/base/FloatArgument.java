package engine.command.argument.base;

import com.google.common.collect.Lists;
import engine.command.argument.Argument;
import engine.command.suggestion.Suggester;

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
    public Optional parse(String arg) {
        try {
            return Optional.of(Float.valueOf(arg));
        }catch (Exception e){
            return Optional.empty();
        }
    }

    @Override
    public Suggester getSuggester() {
        return (sender, command, args) -> Lists.newArrayList("[float]");
    }
}
