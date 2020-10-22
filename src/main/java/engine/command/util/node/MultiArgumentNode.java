package nullengine.command.util.node;

import nullengine.command.CommandSender;
import nullengine.command.util.StringArgs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class MultiArgumentNode extends CommandNode {

    private CommandNode commandNode;
    private Function<Object[], Object> instanceFunction;
    private int argsNum;

    public MultiArgumentNode(CommandNode commandNode, Function<Object[], Object> instanceFunction, int argsNum) {
        this.commandNode = commandNode;
        this.instanceFunction = instanceFunction;
        this.argsNum = argsNum;
    }

    @Override
    public List<Object> collect() {
        ArrayList list = new ArrayList();

        MultiInstance instance = getMultiArgInstance();

        list.add(instance.instance);

        if (instance.parent != null) {
            list.addAll(instance.parent.collect());
        }

        return list;
    }

    private MultiInstance getMultiArgInstance() {
        ArrayList<Object> args = new ArrayList<>();
        CommandNode parent = this;
        for (int i = 0; i < this.argsNum + getRequiredArgsNum(); i++) {
            if (parent.parseResult == null) {
                i--;
            } else {
                args.add(parent.parseResult);
                parent.parseResult = null;
                parent = parent.getParent();
            }
        }
        Collections.reverse(args);
        return new MultiInstance(parent, instanceFunction.apply(args.toArray(new Object[0])));
    }

    @Override
    public int getRequiredArgsNum() {
        return commandNode.getRequiredArgsNum();
    }

    @Override
    protected Object parseArgs(CommandSender sender, StringArgs args) {
        return commandNode.parseArgs(sender, args);
    }

    @Override
    public String getTip() {
        return commandNode.getTip();
    }

    @Override
    public void setTip(String tip) {
        commandNode.setTip(tip);
    }

    @Override
    public boolean hasTip() {
        return commandNode.hasTip();
    }

    private class MultiInstance {
        public final CommandNode parent;
        public final Object instance;

        public MultiInstance(CommandNode parent, Object instance) {
            this.parent = parent;
            this.instance = instance;
        }
    }

    @Override
    public String toString() {
        return "MultiArgumentNode{" +
                "commandNode=" + commandNode +
                ", instanceFunction=" + instanceFunction +
                ", argsNum=" + argsNum +
                '}';
    }

    @Override
    public int priority() {
        return commandNode.priority();
    }

    @Override
    public boolean same(CommandNode node) {
        if (super.same(node) && node instanceof MultiArgumentNode) {
            MultiArgumentNode multiArgumentNode = (MultiArgumentNode) node;
            return argsNum == multiArgumentNode.argsNum &&
                    commandNode.same(multiArgumentNode.commandNode) &&
                    instanceFunction.equals(multiArgumentNode.instanceFunction);
        }
        return false;
    }
}
