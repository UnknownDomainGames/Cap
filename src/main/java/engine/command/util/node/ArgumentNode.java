package engine.command.util.node;

import engine.command.argument.Argument;
import engine.command.suggestion.Suggester;
import engine.command.util.StringArgs;
import engine.command.util.context.LinkedContext;

import java.util.Optional;

public class ArgumentNode extends CommandNode {

    private Argument argument;

    public ArgumentNode(Argument argument) {
        this.argument = argument;
        if (argument != null) {
            setTip(argument.getName());
        }
    }

    @Override
    public int getRequiredArgsNum() {
        return 1;
    }

    @Override
    public ParseResult parse(LinkedContext context, StringArgs args) {
        String next = args.next();
        if (next.isEmpty()) {
            return ParseResult.fail();
        }
        Optional parse = argument.parse(context, next);
        if (parse.isPresent()) {
            return ParseResult.success(parse.get());
        } else {
            return ParseResult.fail();
        }
    }

    @Override
    public String toString() {
        return "ArgumentNode{" +
                "argument=" + argument +
                '}';
    }

    public Argument getArgument() {
        return argument;
    }

    public void setArgument(Argument argument) {
        this.argument = argument;
    }

    @Override
    public Suggester getSuggester() {
        if (super.getSuggester() != null) {
            return super.getSuggester();
        }
        return argument.getSuggester();
    }

    @Override
    public int priority() {
        return 0 + (argument.getName().equals("String") ? -1 : 0) + (argument.responsibleClass().equals(String.class) ? -1 : 0);
    }

    @Override
    public boolean same(CommandNode node) {
        if (super.same(node) && node instanceof ArgumentNode) {
            return argument.equals(((ArgumentNode) node).argument);
        }
        return false;
    }
}
