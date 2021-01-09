package engine.command.util.node;

import engine.command.CommandSender;
import engine.command.suggestion.Suggester;
import engine.command.util.StringArgs;
import engine.command.util.context.LinkedContext;

import java.util.Arrays;
import java.util.Collections;

public class SenderNode extends CommandNode {

    private Class<? extends CommandSender>[] allowedSenders;

    public SenderNode(Class<? extends CommandSender>... clazz) {
        allowedSenders = clazz;
    }

    @Override
    public int getRequiredArgsNum() {
        return 0;
    }

    @Override
    public ParseResult parse(LinkedContext context, StringArgs args) {
        if (allowedSender(context.getSender())) {
            return ParseResult.success(context.getSender());
        }
        return ParseResult.fail();
    }

    public boolean allowedSender(CommandSender sender) {
        for (Class clazz : allowedSenders) {
            if (clazz.isAssignableFrom(sender.getClass())) {
                return true;
            }
        }
        return false;
    }

    public Class<? extends CommandSender>[] getAllowedSenders() {
        return allowedSenders;
    }

    @Override
    public String toString() {
        return "SenderNode{" +
                "allowedSender=" + Arrays.toString(allowedSenders) +
                '}';
    }

    public boolean hasTip() {
        return false;
    }

    @Override
    public Suggester getSuggester() {
        return (sender, command, args) -> Collections.EMPTY_LIST;
    }

    @Override
    public int priority() {
        return -5;
    }

    @Override
    public boolean same(CommandNode node) {
        if (super.same(node) && node instanceof SenderNode) {
            return Arrays.equals(((SenderNode) node).allowedSenders, allowedSenders);
        }
        return false;
    }
}
