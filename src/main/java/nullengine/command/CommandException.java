package nullengine.command;

import java.util.Arrays;

public class CommandException {

    private CommandExceptionState state;

    private Throwable exception;

    private String command;

    private Command commandInstance;

    private String[] args;

    private CommandException(Throwable exception,CommandExceptionState state, String command, Command commandInstance, String[] args) {
        this.exception = exception;
        this.command = command;
        this.commandInstance = commandInstance;
        this.args = args;
        this.state = state;
    }

    public static CommandException commandNotFound(Throwable throwable,String command){
        return new CommandException(throwable,CommandExceptionState.COMMAND_NOT_FOUND,command,null,null);
    }

    public static CommandException exception(Throwable throwable,Command command,String[] args){
        return new CommandException(throwable,CommandExceptionState.RUNNING,command.getName(),command,args);
    }

    public static CommandException exception(Throwable throwable,Command command){
        return new CommandException(throwable,CommandExceptionState.RUNNING,command.getName(),command,null);
    }

    public static CommandException unknownException(Throwable throwable,Command command){
        return new CommandException(throwable,CommandExceptionState.UNKNOWN,command.getName(),command,null);
    }

    public static CommandException unknownException(Throwable throwable,Command command,String[] args){
        return new CommandException(throwable,CommandExceptionState.UNKNOWN,command.getName(),command,args);
    }

    public Throwable getException() {
        return exception;
    }

    public String getCommand() {
        return command;
    }

    public Command getCommandInstance() {
        return commandInstance;
    }

    public String[] getArgs() {
        return args;
    }

    public CommandExceptionState getState() {
        return state;
    }

    @Override
    public String toString() {
        return "CommandException{" +
                "exception=" + exception +
                ", command='" + command + '\'' +
                ", commandInstance=" + commandInstance +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
