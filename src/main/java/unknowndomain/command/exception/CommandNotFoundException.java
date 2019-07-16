package unknowndomain.command.exception;

import java.util.Arrays;

public class CommandNotFoundException extends CommandException {

    public CommandNotFoundException(String command) {
        super(command);
    }

    @Override
    public String getMessage() {
        return "command not found: "+command;
    }

}
