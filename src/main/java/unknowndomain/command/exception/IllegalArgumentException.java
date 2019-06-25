package unknowndomain.command.exception;

import unknowndomain.command.argument.Argument;

public class IllegalArgumentException extends CommandException {

    private final Argument argument;

    public IllegalArgumentException(String command, Argument argument) {
        super(command);
        this.argument = argument;
    }

    public Argument getArgument() {
        return argument;
    }

    @Override
    public String getMessage() {
        return "command: "+command+" argument "+argument.getName()+" inputHelp: "+argument.getInputHelp();
    }
}
