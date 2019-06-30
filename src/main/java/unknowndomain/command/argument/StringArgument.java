package unknowndomain.command.argument;

import com.google.common.collect.Lists;
import unknowndomain.command.CommandSender;

import java.util.List;
import java.util.Optional;

public class StringArgument extends SingleArgument {

    public StringArgument() {
        super(String.class, "String");
    }

    @Override
    public Optional parse(String arg) {
        return Optional.of(arg);
    }


    @Override
    public List<String> getComplete(CommandSender sender, String arg) {
        return Lists.asList("[text]",new String[1]);
    }
}
