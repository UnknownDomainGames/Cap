package unknowndomain.command.anno.node;

import unknowndomain.command.CommandSender;
import unknowndomain.command.exception.CommandException;
import unknowndomain.command.exception.CommandSenderErrorException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SenderNode extends CommandNode {

    private Class<? extends CommandSender> allowedSender;

    public SenderNode() {}

    public SenderNode(Class<? extends CommandSender> clazz) {
        allowedSender = clazz;
    }

    @Override
    public int getNeedArgs() {
        return 0;
    }

    @Override
    public Object parseArgs(CommandSender sender, String command, String... args) {
        if (allowedSender.isAssignableFrom(sender.getClass()))
            return sender;
        throw new CommandSenderErrorException(command, sender);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SenderNode that = (SenderNode) o;
        return Objects.equals(allowedSender, that.allowedSender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allowedSender);
    }

    @Override
    public String toString() {
        return "SenderNode{" +
                "allowedSender=" + allowedSender +
                '}';
    }
}
