package nullengine.command.exception;

import nullengine.command.CommandSender;

public class CommandSenderErrorException extends CommandException {

    private final CommandSender commandSender;

    public CommandSenderErrorException(String command, CommandSender commandSender) {
        super(command);
        this.commandSender = commandSender;
    }

    @Override
    public String getMessage() {
        return "sender error";
    }

    public CommandSender getCommandSender() {
        return commandSender;
    }
}
