package unknowndomain.command.anno.node;

import unknowndomain.command.CommandSender;
import unknowndomain.command.exception.CommandException;
import unknowndomain.command.exception.CommandSenderErrorException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SenderNode extends CommandNode {

    private Set<Class<? extends CommandSender>> allowedSenders = new HashSet<>();

    public SenderNode() {}

    public SenderNode(Class<? extends CommandSender>... classes) {
        allowedSenders.addAll(Arrays.asList(classes));
    }

    @Override
    public int getNeedArgs() {
        return 0;
    }

    @Override
    public Object parseArgs(CommandSender sender, String command, String... args){
        boolean allow = false;
        for (Class<? extends CommandSender> allowedSender : allowedSenders)
            if(allow)
                break;
            else
            if (allowedSender.isAssignableFrom(sender.getClass()))
                allow = true;

        if (!allow)
            throw new CommandSenderErrorException(command, sender);

        return sender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SenderNode that = (SenderNode) o;
        return Objects.equals(allowedSenders, that.allowedSenders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allowedSenders);
    }


}
