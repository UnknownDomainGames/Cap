package unknowndomain.command.argument;


import com.google.common.collect.Lists;
import unknowndomain.command.CommandSender;

import java.util.List;
import java.util.Optional;

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
    public List<String> getComplete(CommandSender sender, String arg) {
        return Lists.asList("[num]",new String[1]);
    }


}
