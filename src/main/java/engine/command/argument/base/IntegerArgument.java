package engine.command.argument.base;


import engine.command.argument.SimpleArgument;
import engine.command.suggestion.Suggester;

import java.util.List;
import java.util.Optional;

public class IntegerArgument extends SimpleArgument {

    public IntegerArgument() {
        super(Integer.class,"Integer");
    }

    @Override
    public Optional parse(String arg) {
        try {
            return Optional.of(Integer.valueOf(arg));
        }catch (Exception e){
            return Optional.empty();
        }
    }

    @Override
    public Suggester getSuggester() {
        return (sender, command, args) -> List.of("[num]");
    }
}
