package nullengine.command.exception;

public class CommandException extends RuntimeException {

    public final String command;

    public CommandException(String command) {
        this.command = command;
    }
}
