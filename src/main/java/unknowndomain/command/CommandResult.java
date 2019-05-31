package unknowndomain.command;

public class CommandResult {

    public final boolean success;

    private String message;

    private Throwable throwable;

    public CommandResult(boolean success) {
        this.success = success;
    }

    public CommandResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public CommandResult(boolean success, Throwable throwable) {
        this.success = success;
        this.throwable = throwable;
    }

    public CommandResult(boolean success, String message, Throwable throwable) {
        this.success = success;
        this.message = message;
        this.throwable = throwable;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Throwable getThrowable(){
        return throwable;
    }

    @Override
    public String toString() {
        return "CommandResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", throwable=" + throwable +
                '}';
    }
}
