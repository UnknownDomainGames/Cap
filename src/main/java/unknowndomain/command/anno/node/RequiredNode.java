package unknowndomain.command.anno.node;

import com.google.common.collect.Sets;
import unknowndomain.command.CommandSender;
import unknowndomain.command.completion.Completer;

import java.util.Objects;
import java.util.Set;

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
        if(args[0].equals(require))
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
        return new Completer() {
            @Override
            public String getName() {
                return this.toString();
            }

            @Override
            public Set<String> complete(CommandSender sender, String command, String[] args) {
                return Sets.newHashSet(require);
            }
        };
    }
}
