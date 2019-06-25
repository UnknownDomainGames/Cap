package unknowndomain.command.exception;

import java.util.Arrays;

public class CommandFoundException extends CommandException {
    
    private String[] args;
    
    public CommandFoundException(String command, String[] args) {
        super(command);
        this.args = args;
    }

    @Override
    public String getMessage() {
        return "command: "+command+" args:"+ Arrays.toString(args);
    }
    
}
