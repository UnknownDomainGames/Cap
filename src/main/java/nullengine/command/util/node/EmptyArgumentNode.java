package nullengine.command.util.node;

import nullengine.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class EmptyArgumentNode extends ArgumentNode {

    public EmptyArgumentNode() {
        super(null);
    }

    @Override
    public int getNeedArgs() {
        return 0;
    }

    public boolean parse(CommandSender sender, String command, String... arg) {
        return true;
    }

    @Override
    public List<Object> collect() {
        return Collections.emptyList();
    }

}
