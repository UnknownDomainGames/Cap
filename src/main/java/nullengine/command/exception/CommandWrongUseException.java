package nullengine.command.exception;

import java.util.Arrays;

public class CommandWrongUseException extends CommandException {

    private String[] args;

    public CommandWrongUseException(String command,String[] args) {
        super(command);
        this.args = args;
    }

    @Override
    public String getMessage() {
        return "command: ["+command+"] args:"+ Arrays.toString(args);
    }

}
