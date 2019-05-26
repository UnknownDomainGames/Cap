package unknowndomain.command.anno;

import java.lang.reflect.Method;

public abstract class ReflectCommandArgument implements CommandArgument{

    private Method commandHandleMethod;

    @Override
    public boolean hasCommand() {
        return commandHandleMethod!=null;
    }

    public void setCommandHandleMethod(Method commandHandleMethod) {
        this.commandHandleMethod = commandHandleMethod;
    }

    public Method getCommandHandleMethod(){
        return  this.commandHandleMethod;
    }
}
