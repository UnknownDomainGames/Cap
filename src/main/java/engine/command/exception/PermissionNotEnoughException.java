package engine.command.exception;

public class PermissionNotEnoughException extends CommandException{

    private final String[] permissions;

    public PermissionNotEnoughException(String command, String[] permissions) {
        super(command);
        this.permissions = permissions;
    }

    @Override
    public String getMessage() {
        return "command: "+command;
    }

    public String[] getPermissions(){
        return permissions;
    }

}
