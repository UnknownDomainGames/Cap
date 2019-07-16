package unknowndomain.command.anno.node;

import unknowndomain.command.argument.Argument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class MultiArgumentNode extends ArgumentNode {

    private Function<Object[], Object> instanceFunction;
    private int argsNum;

    public MultiArgumentNode(Argument argument, Function<Object[], Object> instanceFunction, int argsNum) {
        super(argument);
        this.instanceFunction = instanceFunction;
        this.argsNum = argsNum;
    }

    @Override
    public List<Object> collect() {
        ArrayList list = new ArrayList();

        MultiInstance instance = getMultiArgInstance();

        list.add(instance.instance);

        if(instance.parent!=null)
            list.addAll(instance.parent.collect());

        return list;
    }

    private MultiInstance getMultiArgInstance() {
        ArrayList<Object> args = new ArrayList<>();
        CommandNode parent = this;

        for (int i = 0; i < this.argsNum; i++) {
            if (parent.parseResult == null)
                i--;
            else {
                args.add(parent.parseResult);
                parent.parseResult = null;
                parent = parent.getParent();
            }
        }
        Collections.reverse(args);
        return new MultiInstance(parent,instanceFunction.apply(args.toArray(new Object[0])));
    }

    private class MultiInstance{
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
                "argsNum=" + argsNum +
                "argument=" + getArgument() +
                '}';
    }
}
