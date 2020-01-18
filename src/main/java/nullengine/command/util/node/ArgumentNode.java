package nullengine.command.util.node;

import nullengine.command.CommandSender;
import nullengine.command.argument.Argument;
import nullengine.command.suggestion.Suggester;

import java.util.Objects;

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
    public Object parseArgs(CommandSender sender, String command, String... args) {
        if (args[0].isEmpty()) {
            return null;
        }
        return argument.parse(args[0]).orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ArgumentNode that = (ArgumentNode) o;
        return Objects.equals(argument, that.argument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), argument);
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
    public int weights() {
        return 0 + (argument.getName().equals("String") ? -1 : 0) + (argument.responsibleClass().equals(String.class) ? -1 : 0);
    }

    @Override
    public boolean same(CommandNode node) {
        if (super.same(node) && node instanceof ArgumentNode) {
            Argument argument = ((ArgumentNode) node).argument;
            if (argument.responsibleClass().equals(this.argument.responsibleClass()) &&
                    argument.getName().equals(argument.getName()))
                return true;
        }
        return false;
    }
}
