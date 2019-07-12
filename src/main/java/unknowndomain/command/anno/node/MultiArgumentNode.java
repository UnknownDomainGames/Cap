package unknowndomain.command.anno.node;

import unknowndomain.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class MultiArgumentNode extends ArgumentNode {

    private ArgumentNode[] argumentNodes;

    private Function<Object[],Object> instanceFunction;

    public MultiArgumentNode(Function<Object[],Object> instanceFunction,ArgumentNode... argumentNodes) {
        this.argumentNodes = argumentNodes;
        this.instanceFunction = instanceFunction;
    }

    @Override
    public int getNeedArgs() {
        return sumNeedArgs();
    }

    private int sumNeedArgs(){
        int needArgs = 0;
        for(ArgumentNode node : argumentNodes){
            needArgs += node.getNeedArgs();
        }
        return needArgs;
    }

    @Override
    public Object parseArgs(CommandSender sender, String command, String... args) {
        List<Object> list = new ArrayList<>();

        int index = 0;

        for(ArgumentNode argumentNode : argumentNodes){

            int needArgs = argumentNode.getNeedArgs();

            list.add(argumentNode.parseArgs(sender,command, Arrays.copyOfRange(args,index,index+needArgs)));

            index+=needArgs;
        }

        return instanceFunction.apply(list.toArray());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MultiArgumentNode that = (MultiArgumentNode) o;
        return Arrays.equals(argumentNodes, that.argumentNodes);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(argumentNodes);
        return result;
    }

    @Override
    public String toString() {
        return "MultiArgumentNode{" +
                "argumentNodes=" + Arrays.toString(argumentNodes) +
                '}';
    }

    @Override
    public int compareTo(CommandNode o) {
        if(o instanceof MultiArgumentNode){
            MultiArgumentNode other = (MultiArgumentNode) o;
            return this.argumentNodes.length-other.argumentNodes.length;
        }
        return 1;
    }
}
