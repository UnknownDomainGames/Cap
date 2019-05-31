package unknowndomain.command;

public class CommandResult {

    private final boolean success;
    private final String message;
    private final Throwable cause;

    public CommandResult(boolean success) {
        this(success, (String) null);
    }

    public CommandResult(boolean success, String message) {
        this(success, message, null);
    }

    public CommandResult(boolean success, Throwable cause) {
        this(success, null, cause);
    }

    public CommandResult(boolean success, String message, Throwable cause) {
        this.success = success;
        this.message = message;
        this.cause = cause;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getCause() {
        return cause;
    }

    @Override
    public String toString() {
        return "CommandResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", cause=" + cause +
                '}';
    }
}
