package engine.command.util.node;

import engine.command.CommandSender;
import engine.command.suggestion.Suggester;

import java.util.List;
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
    public Object parseArgs(CommandSender sender, String command, String... args) {
        if (args[0].equals(require)){
            return require;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RequiredNode that = (RequiredNode) o;
        return Objects.equals(require, that.require);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), require);
    }

    @Override
    public Suggester getSuggester() {
        return (sender, command, args) -> List.of(require);
    }

    @Override
    public String toString() {
        return "RequiredNode{" +
                "require='" + require + '\'' +
                '}';
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
