package unknowndomain.command.argument;


import com.google.common.collect.Sets;
import unknowndomain.command.CommandSender;
import unknowndomain.command.completion.Completer;

import java.util.Optional;
import java.util.Set;

public class IntegerArgument extends SingleArgument {

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
        return new Completer() {
            @Override
            public String getName() {
                return this.toString();
            }

            @Override
            public Set<String> complete(CommandSender sender, String command, String[] args) {
                return Sets.newHashSet("[num]");
            }
        };
    }
}
