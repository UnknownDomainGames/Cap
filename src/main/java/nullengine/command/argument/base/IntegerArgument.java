package nullengine.command.argument.base;


import com.google.common.collect.Lists;
import nullengine.command.argument.SimpleArgument;
import nullengine.command.completion.Completer;

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
    public Completer getCompleter() {
        return (sender, command, args) -> Completer.CompleteResult.completeResult(Lists.newArrayList("[num]"));
    }
}
