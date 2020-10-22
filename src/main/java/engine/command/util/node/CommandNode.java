package engine.command.util.node;

import engine.command.CommandSender;
import engine.command.suggestion.Suggester;
import engine.command.util.StringArgs;

import java.util.*;
import java.util.function.Consumer;

public abstract class CommandNode implements Cloneable, Comparable<CommandNode> {

    private CommandNode parent;

    private Consumer<List<Object>> executor;

    private List<CommandNode> children = new ArrayList<>();

    private Set<String> needPermission = new HashSet();

    private Suggester suggester;

    private String tip;

    protected Object parseResult;

    public CommandNode() {
    }

    public boolean parse(CommandSender sender, StringArgs args) {
        Object result = parseArgs(sender, args);
        if (result != null) {
            parseResult = result;
            return true;
        }
        return false;
    }

    public List<Object> collect() {
        ArrayList list = new ArrayList();
        list.add(parseResult);
        if (parent != null) {
            list.addAll(parent.collect());
        }
        return list;
    }

    public abstract int getRequiredArgsNum();

    protected abstract Object parseArgs(CommandSender sender, StringArgs args);

    public CommandNode getParent() {
        return parent;
    }

    public Collection<CommandNode> getChildren() {
        return children;
    }

    public void addChild(CommandNode commandNode) {
        if (commandNode.executor != null) {
            for (CommandNode child : children) {
                if (child.same(commandNode) && commandNode.executor.equals(child.executor))
                    return;
            }
            add(commandNode);
            return;
        }
        CommandNode node = matchChild(commandNode);
        if (node == null) {
            add(commandNode);
        } else {
            for (CommandNode child : commandNode.getChildren()) {
                node.addChild(child);
            }
        }
    }

    private void add(CommandNode commandNode) {
        commandNode.setParent(this);
        this.children.add(commandNode);
        Collections.sort(children);
    }

    private CommandNode matchChild(CommandNode commandNode) {
        for (CommandNode child : children) {
            if (child.executor == null && child.same(commandNode))
                return child;
        }
        return null;
    }

    public void removeChild(CommandNode commandNode) {
        if (this.children.remove(commandNode)) {
            commandNode.setParent(null);
        }
    }

    public boolean canExecuteCommand() {
        return getExecutor() != null;
    }

    protected void setParent(CommandNode parent) {
        this.parent = parent;
    }

    public void setExecutor(Consumer<List<Object>> executor) {
        this.executor = executor;
    }

    public Consumer<List<Object>> getExecutor() {
        return executor;
    }

    public Set<String> getNeedPermission() {
        return needPermission;
    }

    public void setNeedPermission(Set<String> needPermission) {
        this.needPermission = needPermission;
    }

    public Suggester getSuggester() {
        return suggester;
    }

    public void setSuggester(Suggester suggester) {
        this.suggester = suggester;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public boolean hasTip() {
        return tip != null;
    }

    @Override
    public CommandNode clone() {
        CommandNode node = null;
        try {
            node = (CommandNode) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        node.children = new ArrayList<>();
        for (CommandNode child : children) {
            node.addChild(child.clone());
        }
        return node;
    }

    public boolean same(CommandNode node) {
        return node != null && node instanceof CommandNode &&
                Objects.equals(suggester, node.suggester);
    }

    @Override
    public int compareTo(CommandNode o) {
        return o.priority() - priority();
    }

    public abstract int priority();
}
