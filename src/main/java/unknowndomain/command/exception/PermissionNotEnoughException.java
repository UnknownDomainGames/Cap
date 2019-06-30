package unknowndomain.command.exception;

public class PermissionNotEnoughException extends CommandException{

    public PermissionNotEnoughException(String command) {
        super(command);
    }

    @Override
    public String getMessage() {
        return "command: "+command;
    }

}
