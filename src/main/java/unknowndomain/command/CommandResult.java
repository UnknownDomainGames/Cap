package unknowndomain.command;

import javax.annotation.Nullable;

public final class CommandResult {

    private final boolean success;
    private final String message;
    private final Throwable cause;

    public static CommandResult success() {
        return new CommandResult(true, null, null);
    }

    public static CommandResult failure(String message) {
        return new CommandResult(false, message, null);
    }

    @Deprecated
    public static CommandResult failure(Throwable cause) {
        return new CommandResult(false, null, cause);
    }

    public static CommandResult failure(String message, Throwable cause) {
        return new CommandResult(false, message, cause);
    }

    private CommandResult(boolean success, String message, Throwable cause) {
        this.success = success;
        this.message = message;
        this.cause = cause;
    }

    public boolean isSuccess() {
        return success;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    @Nullable
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
