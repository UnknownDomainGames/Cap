package nullengine.command.exception;

public class CommandIllegalArgumentException extends CommandException {

    private final String argument;

    public CommandIllegalArgumentException(String command, String argument) {
        super(command);
        this.argument = argument;
    }

    public String getArgument() {
        return argument;
    }

    @Override
    public String getMessage() {
        return "command: "+command+",argument: "+argument;
    }
}
