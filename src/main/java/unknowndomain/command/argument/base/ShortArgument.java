package unknowndomain.command.argument.base;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import unknowndomain.command.argument.SimpleArgument;
import unknowndomain.command.completion.Completer;

import java.util.Optional;

public class ShortArgument extends SimpleArgument {
    public ShortArgument() {
        super(Short.class,"Short");
    }

    @Override
    public Optional parse(String arg) {
        try {
            return Optional.of(Short.valueOf(arg));
        }catch (Exception e){
            return Optional.empty();
        }
    }

    @Override
    public Completer getCompleter() {
        return (sender, command, args) -> Lists.newArrayList("[num]");
    }
}
