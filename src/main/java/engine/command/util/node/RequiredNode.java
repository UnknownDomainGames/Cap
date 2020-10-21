package nullengine.command.util.node;

import com.google.common.collect.Lists;
import nullengine.command.CommandSender;
import nullengine.command.suggestion.Suggester;
import nullengine.command.util.StringArgs;

import java.util.Objects;

public class RequiredNode extends CommandNode {

    private String require;

    public RequiredNode(String require) {
        this.require = require;
    }

    @Override
    public int getRequiredArgsNum() {
        return 1;
    }

    @Override
    public Object parseArgs(CommandSender sender, StringArgs args) {
        if (args.next().equals(require)) {
            return require;
        }
        return null;
    }

    @Override
    public Suggester getSuggester() {
        return (sender, command, args) -> Lists.newArrayList(require);
    }

    public String getRequire() {
        return require;
    }

    @Override
    public String toString() {
        return "RequiredNode{" +
                "require='" + require + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ Objects.hash(require);
    }

    @Override
    public int priority() {
        return 10;
    }

    @Override
    public boolean same(CommandNode node) {
        if (super.same(node) && node instanceof RequiredNode) {
            return ((RequiredNode) node).require.equals(require);
        }
        return false;
    }
}
