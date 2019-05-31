package unknowndomain.command.anno;

import unknowndomain.command.CommandManager;
import unknowndomain.command.CommandSender;
import unknowndomain.command.argument.Argument;
import unknowndomain.command.argument.ArgumentManager;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class AnnotationCommandManager {

    public static void registerAnnotationCommand(Object commandHandler) {

        for (Method method : commandHandler.getClass().getMethods()) {

            Command command = method.getAnnotation(Command.class);
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
                            arguments.add(ArgumentManager.getArgumentByClass(firstParameter.getType()));
                        } else arguments.add(ArgumentManager.getArgumentByName(argumentHandler.value()));
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
                        needAddArgument = ArgumentManager.getArgumentByClass(parameter.getType());
                        if (needAddArgument == null)
                            throw new RuntimeException(parameter.getType().getName() + " not support argument");
                    } else {
                        needAddArgument = ArgumentManager.getArgumentByName(argumentHandler.value());
                        if (needAddArgument == null) {
                            throw new RuntimeException(argumentHandler.value() + " not found");
                        }
                    }

                    arguments.add(needAddArgument);
                }

                AnnotationCommand annotationCommand = new AnnotationCommand(commandName, commandHandler, senderClass, method, desc, helpMessage);

                arguments.forEach(argument -> annotationCommand.appendArgument(argument));

                if (parameters != null && permissions.length >= 0)
                    for (String permission1 : permissions)
                        annotationCommand.appendPermission(permission1);

                CommandManager.getInstance().registerCommand(annotationCommand);
            }
        }


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
        return firstParameter.getAnnotation(Command.class) == null;
    }

}
