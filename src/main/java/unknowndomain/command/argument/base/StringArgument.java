package unknowndomain.command.argument.base;

import com.google.common.collect.Sets;
import unknowndomain.command.CommandSender;
import unknowndomain.command.argument.SingleArgument;
import unknowndomain.command.completion.Completer;

import java.util.Optional;
import java.util.Set;

public class StringArgument extends SingleArgument {

    public StringArgument() {
        super(String.class, "String");
    }

    @Override
    public Optional parse(String arg) {
        return Optional.of(arg);
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
                return Sets.newHashSet("[text]");
            }
        };
    }
}
