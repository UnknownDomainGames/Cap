package engine.command;

import java.util.Arrays;

public final class CommandException {

    private final Type type;
    private final String command;
    private final CommandSender sender;
    private final String commandName;
    private final Command commandInstance;
    private final String[] args;
    private final Object message;

    public enum Type {
        COMMAND_NOT_FOUND,
        COMMAND_WRONG_USAGE,
        COMMAND_WRONG_SENDER,
        COMMAND_ILLEGAL_ARGUMENT,
        PERMISSION_NOT_ENOUGH,
        COMMAND_RUNTIME,
        UNKNOWN
    }

    public static CommandException commandNotFound(CommandSender sender, CommandResolver.Result command) {
        return new CommandException(Type.COMMAND_NOT_FOUND, sender, command.getRaw(), command.getName(), null, command.getArgs(), null);
    }

    public CommandException(Type type, CommandSender sender, String command, String commandName, Command commandInstance, String[] args, Object message) {
        this.type = type;
        this.sender = sender;
        this.command = command;
        this.commandName = commandName;
        this.commandInstance = commandInstance;
        this.args = args;
        this.message = message;
    }

    public Type getType() {
        return type;
    }

    public String getCommand() {
        return command;
    }

    public CommandSender getSender() {
        return sender;
    }

    public String getCommandName() {
        return commandName;
    }

    public Command getCommandInstance() {
        return commandInstance;
    }

    public String[] getArgs() {
        return args;
    }

    public Object getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "CommandException{" +
                "type=" + type +
                ", command='" + command + '\'' +
                ", sender=" + sender +
                ", commandInstance=" + commandInstance +
                ", args=" + Arrays.toString(args) +
                ", message=" + message +
                '}';
    }
}
