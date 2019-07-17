package unknowndomain.command.argument.base;

import com.google.common.collect.Sets;
import unknowndomain.command.argument.SingleArgument;
import unknowndomain.command.completion.Completer;

import java.util.Optional;

public class ShortArgument extends SingleArgument {
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
        return (sender, command, args) -> Sets.newHashSet("[num]");
    }
}
