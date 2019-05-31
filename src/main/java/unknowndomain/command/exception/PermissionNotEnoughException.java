package unknowndomain.command.exception;

public class PermissionNotEnoughException extends CommandException{

    private String permission;

    public PermissionNotEnoughException(String command, String permission) {
        super(command);
        this.permission = permission;
    }

    public String getPermission(){
        return permission;
    }
}
