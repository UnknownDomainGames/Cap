package unknowndomain.command.anno.node;

import unknowndomain.command.CommandSender;
import unknowndomain.command.argument.Argument;

import java.lang.reflect.Method;
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

}
