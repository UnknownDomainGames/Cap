package unknowndomain.command;

public class CommandResult {

    private boolean success;

    private String message;

    public CommandResult(boolean success) {
        this.success = success;
    }

    public CommandResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
