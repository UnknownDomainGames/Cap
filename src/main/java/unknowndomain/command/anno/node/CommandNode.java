package unknowndomain.command.anno.node;

import unknowndomain.command.CommandResult;
import unknowndomain.command.CommandSender;
import unknowndomain.command.exception.CommandException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class CommandNode {

    private CommandNode parent;

    private Method method;

    private Object instance;

    private ArrayList<CommandNode> children = new ArrayList<>();

    public CommandNode() {}

    public abstract int getNeedArgs();

    public abstract Object parseArgs(CommandSender sender, String command, String... args);

    public CommandNode getParent(){
        return parent;
    }

    public List<CommandNode> getChildren(){
        return children;
    }

    public void addChild(CommandNode commandNode){
        commandNode.setParent(this);
        this.children.add(commandNode);
    }

    public abstract boolean equals(Object obj);

    public boolean canExecuteCommand(){
        return getMethod()!=null;
    }

    protected void setParent(CommandNode parent) {
        this.parent = parent;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Method getMethod() {
        return method;
    }

    public Object getInstance() {
        return instance;
    }

    public CommandResult execute(Object... args){
        try {
            Object result = method.invoke(instance,args);
            if(result instanceof CommandResult)
                return (CommandResult) result;
            else if(result instanceof Boolean){
                return new CommandResult((Boolean) result);
            }else return new CommandResult(true);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }catch (CommandException e){
            return new CommandResult(e);
        }
        return new CommandResult(false,"unknown reason");
    }
}
