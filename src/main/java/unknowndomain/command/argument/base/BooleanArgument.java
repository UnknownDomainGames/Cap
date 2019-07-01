package unknowndomain.command.argument.base;

import com.google.common.collect.Sets;
import unknowndomain.command.CommandSender;
import unknowndomain.command.argument.Argument;
import unknowndomain.command.completion.Completer;

import java.util.Optional;
import java.util.Set;

public class BooleanArgument extends Argument {
    @Override
    public String getName() {
        return "Boolean";
    }

    @Override
    public Class responsibleClass() {
        return Boolean.class;
    }

    @Override
    public Optional parse(String arg) {
        return Optional.ofNullable(Boolean.valueOf(arg));
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
                return Sets.newHashSet("true/false");
            }
        };
    }
}
