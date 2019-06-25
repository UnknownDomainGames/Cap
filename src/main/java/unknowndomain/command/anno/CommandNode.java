package unknowndomain.command.anno;

import unknowndomain.command.completion.Completer;
import unknowndomain.command.traditional.CommandCompleter;
import unknowndomain.command.argument.Argument;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

class CommandNode {

    private CommandNode parent;

    public Argument argument;

    private List<CommandNode> children = new ArrayList<>();

    private Method handleMethod;

    private Object instance;

    private unknowndomain.command.completion.Completer commandCompleter;

    public CommandNode(Argument argument) {
        this.argument = argument;
    }

    public Method getHandleMethod() {
        return handleMethod;
    }

    public Object getInstance() {
        return instance;
    }

    public void setHandleMethod(Method handleMethod) {
        this.handleMethod = handleMethod;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public void addChild(CommandNode... commandNodes){
        addChild(Arrays.asList(commandNodes));
    }

    public CommandNode getParent() {
        return parent;
    }

    public void setParent(CommandNode parent) {
        this.parent = parent;
    }

    public void addChild(List<CommandNode> commandNodes){
        commandNodes.stream().forEach(node -> node.setParent(this));
        children.addAll(commandNodes);
    }

    public List<CommandNode> getChildren(){
        return children;
    }

    public Completer getCompleter() {
        return commandCompleter;
    }

    public void setCompleter(Completer commandCompleter) {
        this.commandCompleter = commandCompleter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandNode node = (CommandNode) o;
        return Objects.equals(argument, node.argument) &&
                Objects.equals(handleMethod, node.handleMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(argument, handleMethod);
    }

    @Override
    public String toString() {
        return "CommandNode{" +
                "argument=" + argument +
                ", children=" + children +
                ", handleMethod=" + handleMethod +
                '}';
    }
}
