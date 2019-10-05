package nullengine.command.exception;

public class CommandNotFoundException extends CommandException {

    public CommandNotFoundException(String command) {
        super(command);
    }

    @Override
    public String getMessage() {
        return "Command not found: "+command;
    }

}
