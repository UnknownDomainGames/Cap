package nullengine.command.anno.node;

import com.google.common.collect.Lists;
import nullengine.command.CommandSender;
import nullengine.command.completion.Completer;

import java.util.Objects;

public class RequiredNode extends CommandNode {

    private String require;

    public RequiredNode(String require) {
        this.require = require;
    }

    @Override
    public int getNeedArgs() {
        return 1;
    }

    @Override
    public Object parseArgs(CommandSender sender, String command, String... args) {
        if (args[0].equals(require))
            return require;
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequiredNode that = (RequiredNode) o;
        return Objects.equals(require, that.require);
    }

    @Override
    public int hashCode() {
        return Objects.hash(require);
    }

    @Override
    public Completer getCompleter() {
        return (sender, command, args) -> Lists.newArrayList(require);
    }

    @Override
    public String toString() {
        return "RequiredNode{" +
                "require='" + require + '\'' +
                '}';
    }

    @Override
    public int compareTo(CommandNode o) {
        if (!(o instanceof RequiredNode))
            return 1;
        RequiredNode other = (RequiredNode) o;
        return this.require.compareTo(other.require);
    }
}
