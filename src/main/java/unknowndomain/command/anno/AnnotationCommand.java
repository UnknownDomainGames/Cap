package unknowndomain.command.anno;

import unknowndomain.command.Command;
import unknowndomain.command.CommandResult;
import unknowndomain.command.CommandSender;
import unknowndomain.command.argument.*;
import unknowndomain.command.completion.CompleteManager;
import unknowndomain.command.exception.IllegalArgumentException;
import unknowndomain.command.exception.PermissionNotEnoughException;
import unknowndomain.permission.Permissible;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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

    private AnnotationCommand(String name, Object instance, Class<? extends CommandSender> commandSenderClass, Method handlerMethod, String desc, String helpMessage) {
        super(name);
        this.instance = instance;
        this.commandSenderClass = commandSenderClass;
        this.handlerMethod = handlerMethod;
        this.desc = desc;
        this.helpMessage = helpMessage;
    }

    @Override
    public CommandResult execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender.getPermissible()))
            return new CommandResult(false, new PermissionNotEnoughException(this.name, permissions.toString()));

        CommandResult result;

        List<Object> methodArguments = new ArrayList<>();
        Object methodSender;

        if (commandSenderClass != null){
            methodSender = commandSenderClass.cast(sender);
            methodArguments.add(methodSender);
        }

        int index = 0;

        for (int i = 0; i < arguments.size(); i++) {
            ParseResult parseResult = arguments.get(i).parseArgs(Arrays.copyOfRange(args,index,args.length));
            if(parseResult.fail){
                return new CommandResult(false,new IllegalArgumentException(this.name,arguments.get(i)));
            }
            methodArguments.add(parseResult.result);
            index+=parseResult.uesdArgsNum;
        }

        try {
            Object o = handlerMethod.invoke(instance, methodArguments.toArray());
            if (o instanceof CommandResult)
                result = (CommandResult) o;
            else if (o instanceof Boolean) {
                result = new CommandResult((Boolean) o);
            } else
                result = new CommandResult(true);
        } catch (Exception exception) {
            result = new CommandResult(false, exception);
        }

        return result;
    }

    private boolean hasPermission(Permissible permissible) {
        for (String s : permissions)
            if (!permissible.hasPermission(s))
                return false;
        return true;
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        int completeIndex = 0;

        if (args != null && args.length != 0) {
            completeIndex = args.length - 1;
        }

        return CompleteManager.complete(sender, this.arguments.get(completeIndex).responsibleClass());
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

    public void appendPermission(String permission) {
        this.permissions.add(permission);
    }


    public static List<Command> as(ArgumentManager argumentManager,Object commandHandler) {

        List<Command> list = new ArrayList<>();

        for (Method method : commandHandler.getClass().getMethods()) {

            unknowndomain.command.anno.Command command = method.getAnnotation(unknowndomain.command.anno.Command.class);
            if (command == null) {
                continue;
            }

            Permission permission = method.getAnnotation(Permission.class);
            String[] permissions = new String[0];
            if (permission != null)
                permissions = permission.value();

            String commandName = command.value();
            String desc = command.desc();
            String helpMessage = command.helpMessage();

            Class<? extends CommandSender> senderClass = null;

            Parameter[] parameters = method.getParameters();

            List<Argument> arguments = new ArrayList<>();

            if (parameters != null) {
                if (parameters.length >= 1) {
                    Parameter firstParameter = parameters[0];
                    Sender sender = firstParameter.getAnnotation(Sender.class);
                    if (sender != null) {
                        if (!withOutArgumentHandlerAnnotation(firstParameter))
                            throw new RuntimeException("parameter can't annotate Sender and ArgumentHandler both");
                        Class markedClass = firstParameter.getType();
                        if (classInstanceOfCommandSender(markedClass)) {
                            senderClass = markedClass;
                        } else
                            throw new RuntimeException("class marked as Sender did not implement CommandSender");
                    } else {
                        ArgumentHandler argumentHandler = firstParameter.getAnnotation(ArgumentHandler.class);

                        if (argumentHandler == null) {
                            arguments.add(argumentManager.getArgument(firstParameter.getType()));
                        } else arguments.add(argumentManager.getArgument(argumentHandler.value()));
                    }
                }

                for (int i = 1; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    if (hasAnnotationOfSender(parameter)) {
                        throw new RuntimeException("sender must be first of parameters");
                    }
                    ArgumentHandler argumentHandler = parameter.getAnnotation(ArgumentHandler.class);

                    Argument needAddArgument;
                    if (argumentHandler == null) {
                        needAddArgument = argumentManager.getArgument(parameter.getType());
                        if (needAddArgument == null)
                            throw new RuntimeException(parameter.getType().getName() + " not support argument:");
                    } else {
                        needAddArgument = argumentManager.getArgument(argumentHandler.value());
                        if (needAddArgument == null) {
                            throw new RuntimeException(argumentHandler.value() + "argument not found");
                        }
                    }

                    arguments.add(needAddArgument);
                }

                AnnotationCommand annotationCommand = new AnnotationCommand(commandName, commandHandler, senderClass, method, desc, helpMessage);

                annotationCommand.arguments.addAll(arguments);

                if (parameters != null && permissions.length >= 0)
                    for (String permission1 : permissions)
                        annotationCommand.appendPermission(permission1);

                list.add(annotationCommand);
            }
        }

        return list;
    }

    private static boolean hasAnnotationOfSender(Parameter parameter) {
        return parameter.getAnnotation(Sender.class) != null;
    }

    private static boolean classInstanceOfCommandSender(Class markedClass) {
        return classInstanceOf(markedClass, CommandSender.class);
    }

    private static boolean classInstanceOf(Class markedClass, Class clazz) {
        if (markedClass.equals(Object.class)) {
            return false;
        }
        if (markedClass.equals(clazz))
            return true;

        for (Class interfaceClass : markedClass.getInterfaces()) {
            if (interfaceClass.equals(clazz))
                return true;
            else {
                boolean b = classInstanceOf(interfaceClass.getSuperclass(), clazz);
                if (b)
                    return true;
                else continue;
            }
        }
        return classInstanceOf(markedClass.getSuperclass(), clazz);
    }

    private static boolean withOutArgumentHandlerAnnotation(Parameter firstParameter) {
        return firstParameter.getAnnotation(unknowndomain.command.anno.Command.class) == null;
    }
}
