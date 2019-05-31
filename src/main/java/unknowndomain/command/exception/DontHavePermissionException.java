package unknowndomain.command.exception;

public class DontHavePermissionException extends CommandException{

    private String permission;

    public DontHavePermissionException(String command, String permission) {
        super(command);
        this.permission = permission;
    }

    public String getPermission(){
        return permission;
    }
}
