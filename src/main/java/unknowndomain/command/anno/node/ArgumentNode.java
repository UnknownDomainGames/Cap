package unknowndomain.command.anno.node;

import unknowndomain.command.CommandSender;
import unknowndomain.command.argument.Argument;
import unknowndomain.command.completion.Completer;

import java.util.Objects;

public class ArgumentNode extends CommandNode {

    private Argument argument;

    public ArgumentNode() {}

    public ArgumentNode(Argument argument) {
        this.argument = argument;
    }

    @Override
    public int getNeedArgs() {
        return 1;
    }

    @Override
    public Object parseArgs(CommandSender sender, String command, String... args) {
        return argument.parse(args[0]).get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArgumentNode that = (ArgumentNode) o;
        return Objects.equals(argument, that.argument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(argument);
    }

    @Override
    public String toString() {
        return "ArgumentNode{" +
                "argument=" + argument +
                '}';
    }

    public void setArgument(Argument argument) {
        this.argument = argument;
    }

    @Override
    public int compareTo(CommandNode o) {
        if(argument.responsibleClass().equals(String.class))
            return -1;
        return super.compareTo(o);
    }

    @Override
    public Completer getCompleter() {
        if(super.getCompleter()!=null)
            return super.getCompleter();
        return argument.getCompleter();
    }


}