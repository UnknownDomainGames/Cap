package nullengine.command;

import java.util.Arrays;

public class CommandException {

    private Throwable exception;

    private String command;

    private Command commandInstance;

    private String[] args;

    private CommandException(Throwable exception, String command, Command commandInstance, String[] args) {
        this.exception = exception;
        this.command = command;
        this.commandInstance = commandInstance;
        this.args = args;
    }

    public static CommandException commandNotFound(Throwable throwable,String command){
        return new CommandException(throwable,command,null,null);
    }

    public static CommandException exception(Throwable throwable,Command command,String[] args){
        return new CommandException(throwable,command.getName(),command,args);
    }

    public static CommandException exception(Throwable throwable,Command command){
        return new CommandException(throwable,command.getName(),command,null);
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
