package unknowndomain.command.exception;

import java.util.Arrays;

public class CommandWrongUseException extends CommandException {

    private String[] args;

    public CommandWrongUseException(String command) {
        super(command);
    }

    @Override
    public String getMessage() {
        return "command: "+command+" args:"+ Arrays.toString(args);
    }

}
