package unknowndomain.command.argument.base;

import com.google.common.collect.Sets;
import unknowndomain.command.argument.SingleArgument;
import unknowndomain.command.completion.Completer;

import java.util.Optional;

public class LongArgument extends SingleArgument {
    public LongArgument() {
        super(Long.class,"Long");
    }

    @Override
    public Optional parse(String arg) {
        try {
            return Optional.of(Long.valueOf(arg));
        }catch (Exception e){
            return Optional.empty();
        }
    }

    @Override
    public Completer getCompleter() {
        return (sender, command, args) -> Sets.newHashSet("[num]");
    }
}
