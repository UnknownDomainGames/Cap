package engine.command;

import java.util.Arrays;

public final class CommandFailure {

    private final Type type;
    private final CommandSender sender;
    private final String commandName;
    private final Command command;
    private final String[] args;
    private final Object message;

    public enum Type {
        COMMAND_NOT_FOUND,
        COMMAND_WRONG_USAGE,
        COMMAND_WRONG_SENDER,
        COMMAND_ILLEGAL_ARGUMENT,
        PERMISSION_NOT_ENOUGH,
        CUSTOM
    }

    public CommandFailure(Type type, CommandSender sender, Command command, String[] args) {
        this(type, sender, command, args, null);
    }

    public CommandFailure(Type type, CommandSender sender, Command command, String[] args, Object message) {
        this.type = type;
        this.sender = sender;
        this.commandName = command.getName();
        this.command = command;
        this.args = args;
        this.message = message;
    }

    public CommandFailure(Type type, CommandSender sender, String commandName, String[] args) {
        this(type, sender, commandName, args, null);
    }

    public CommandFailure(Type type, CommandSender sender, String commandName, String[] args, Object message) {
        this.type = type;
        this.sender = sender;
        this.commandName = commandName;
        this.command = null;
        this.args = args;
        this.message = message;
    }

    public Type getType() {
        return type;
    }

    public CommandSender getSender() {
        return sender;
    }

    public String getCommandName() {
        return commandName;
    }

    public Command getCommand() {
        return command;
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
                ", sender=" + sender +
                ", commandName='" + commandName + '\'' +
                ", commandInstance=" + command +
                ", args=" + Arrays.toString(args) +
                ", message=" + message +
                '}';
    }
}
