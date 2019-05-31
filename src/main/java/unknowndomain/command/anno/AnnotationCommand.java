package unknowndomain.command.anno;

import unknowndomain.command.Command;
import unknowndomain.command.CommandResult;
import unknowndomain.command.CommandSender;
import unknowndomain.command.argument.Argument;
import unknowndomain.command.completion.CompleteManager;
import unknowndomain.command.exception.DontHavePermissionException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnnotationCommand extends Command {

    private final Object instance;

    private final Class<? extends CommandSender> commandSenderClass;

    private final List<Argument> arguments = new ArrayList<>();

    private final Method handlerMethod;

    private String desc;

    private String helpMessage;

    private List<String> permissions = new ArrayList<>();


    public AnnotationCommand(String name, Object instance, Class<? extends CommandSender> commandSenderClass, Method handlerMethod, String desc, String helpMessage) {
        super(name);
        this.instance = instance;
        this.commandSenderClass = commandSenderClass;
        this.handlerMethod = handlerMethod;
        this.desc = desc;
        this.helpMessage = helpMessage;
    }


    @Override
    public CommandResult execute(CommandSender sender, String[] args) {

        for(String permission : permissions)
            if(!sender.hasPermission(permission))
                return new CommandResult(false,new DontHavePermissionException(this.name,permission));
        CommandResult result;

        Object handleMethodSender;

        List<Object> handleMethodArgument = new ArrayList<>();

        if (commandSenderClass == null) {
            handleMethodSender = null;
        } else {
            handleMethodSender = commandSenderClass.cast(sender);
            handleMethodArgument.add(handleMethodSender);
        }

        for (int i = 0; i < arguments.size(); i++) {
            handleMethodArgument.add(arguments.get(i).getHandleFunction().apply(sender, args[i]));
        }

        try {
            if (handleMethodSender == null) {
                Object o = handlerMethod.invoke(instance, handleMethodArgument.toArray());
                if (o instanceof CommandResult)
                    result = (CommandResult) o;
                else if (o instanceof Boolean) {
                    result = new CommandResult((Boolean) o);
                } else
                    result = new CommandResult(true);
            } else {
                Object o = handlerMethod.invoke(instance, handleMethodArgument.toArray());
                if (o instanceof CommandResult)
                    result = (CommandResult) o;
                else if (o instanceof Boolean) {
                    result = new CommandResult((Boolean) o);
                } else
                    result = new CommandResult(true);
            }
        } catch (Exception exception) {
            result = new CommandResult(false, exception);
        }

        return result;
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        int completeIndex = 0;

        if (args != null && args.length != 0) {
            completeIndex = args.length - 1;
        }

        return CompleteManager.complete(sender, this.arguments.get(completeIndex).getHandleClass());
    }

    @Override
    public String getDescription() {
        if (desc == null)
            return super.getDescription();
        else return desc;
    }

    @Override
    public void setDescription(String description) {
        this.desc = description;
    }

    @Override
    public String getHelpMessage() {
        if (helpMessage == null)
            return super.getHelpMessage();
        else return helpMessage;
    }

    @Override
    public void setHelpMessage(String helpMessage) {
        this.helpMessage = helpMessage;
    }

    protected void appendArgument(Argument argument) {
        this.arguments.add(argument);
    }

    protected void appendPermission(String permission){
        this.permissions.add(permission);
    }
}
