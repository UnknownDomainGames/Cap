package unknowndomain.command.anno;

import unknowndomain.command.Command;
import unknowndomain.command.CommandManager;
import unknowndomain.command.CommandResult;
import unknowndomain.command.CommandSender;
import unknowndomain.command.argument.Argument;
import unknowndomain.command.argument.ArgumentManager;
import unknowndomain.command.argument.ParseResult;
import unknowndomain.command.completion.CompleteManager;
import unknowndomain.command.exception.CommandFoundException;
import unknowndomain.command.exception.PermissionNotEnoughException;
import unknowndomain.permission.Permissible;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class AnnotationCommand extends Command {

    private List<Argument> arguments = new ArrayList<>();

    private CommandNode commandNode = new CommandNode(null);

    private String desc;

    private String helpMessage;

    private Set<String> permissions = new HashSet<>();


    public AnnotationCommand(String name, String description, String helpMessage) {
        super(name, description, helpMessage);
    }

    public CommandResult execute(CommandSender sender, String[] args) {
        if (!hasPermissions(sender.getPermissible()))
            return new CommandResult(false, new PermissionNotEnoughException(this.name, permissions.toString()));
        if (args == null || args.length == 0) {
            return commandExecute(commandNode, sender, args);
        } else {
            CommandNode node = matchNode(commandNode, args);
            if (node == null)
                return new CommandResult(false, new CommandFoundException(super.name, args));

            if (node.getHandleMethod() == null) {
                return new CommandResult(false, "do you want: " + speculativeCommand(args, node) + " ?", new CommandFoundException(super.name, args));
            }

            return commandExecute(node, sender, args);
        }
    }

    private CommandResult commandExecute(CommandNode node, CommandSender sender, String[] args) {
        ArrayList<CommandNode> commandNodes = new ArrayList<>();
        commandNodes.add(node);
        CommandNode parent = node;
        while (parent.getParent() != null) {
            commandNodes.add(parent.getParent());
            parent = parent.getParent();
        }

        ArrayList<Object> instancedArgs = new ArrayList<>();

        int argsIndex = 0;
        for (int i = commandNodes.size() - 1; i >= 0; i--) {
            CommandNode node1 = commandNodes.get(i);
            Argument argument = node1.argument;
            if (argument == null)
                continue;
            ParseResult result = argument.parseArgs(Arrays.copyOfRange(args, argsIndex, args.length));
            if (result.result == null)
                continue;
            instancedArgs.add(result.result);
            argsIndex += result.uesdArgsNum;
        }

        int senderIndex = getSenderIndex(Arrays.asList(node.getHandleMethod().getParameters()));
        if (senderIndex != -1)
            instancedArgs.add(senderIndex, sender);

        try {
            CommandResult result;
            Object o = node.getHandleMethod().invoke(node.getInstance(), instancedArgs.toArray());
            if (o instanceof CommandResult)
                result = (CommandResult) o;
            else if (o instanceof Boolean) {
                result = new CommandResult((Boolean) o);
            } else
                result = new CommandResult(true);
            return result;

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return new CommandResult(false, new RuntimeException("command should execute"));
    }

    private String speculativeCommand(String[] args, CommandNode node) {
        StringBuilder sb = new StringBuilder(arrayArgs(args));
        Collection<CommandNode> children = node.getChildren();

        for (CommandNode child : children) {
            if (child.argument == null)
                continue;
            sb.append(child.argument.getInputHelp()).append("/");
        }
        String command = sb.toString();
        return command.substring(0, command.length() - 1);
    }

    private String arrayArgs(String[] args) {
        StringBuilder sb = new StringBuilder("/");
        sb.append(super.name).append(" ");
        for (String arg : args) {
            sb.append(arg).append(" ");
        }
        return sb.toString();
    }


    private boolean hasPermissions(Permissible permissible) {
        for (String s : permissions)
            if (!permissible.hasPermission(s))
                return false;
        return true;
    }

    private CommandNode matchNode(CommandNode parent, String[] args) {
        if (args == null || args.length == 0) {
            return parent;
        }
        for (CommandNode child : parent.getChildren()) {
            ParseResult result = child.argument.parseArgs(args);
            if (!result.fail) {
                CommandNode node = matchNode(child, Arrays.copyOfRange(args, result.uesdArgsNum, args.length));
                if (node == null)
                    continue;
                return node;
            }
        }
        return null;
    }

    @Override
    public Set<String> complete(CommandSender sender, String[] args) {
        HashSet<String> completeSet = new HashSet<>();

        String lastArg = "";

        if(args!=null&&args.length>0)
            lastArg = args[args.length-1];

        for(CommandNode node : commandNode.getChildren()){
            completeSet.addAll(node.getCompleter().complete(sender,this.name,args));
        }
        return completeSet;
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

    public static List<Command> as(CommandManager commandManager, ArgumentManager argumentManager, CompleteManager completeManager, Object commandHandler) {
        List<Command> commands = new ArrayList<>();

        for (Method method : commandHandler.getClass().getMethods()) {

            unknowndomain.command.anno.Command command = method.getAnnotation(unknowndomain.command.anno.Command.class);
            if (command == null)
                continue;

            AnnotationCommand annotationCommand = (AnnotationCommand) commandManager.getCommand(command.value());

            for (Command command1 : commands)
                if (command1.name.equals(command.value()))
                    annotationCommand = (AnnotationCommand) command1;

            if (annotationCommand == null)
                annotationCommand = new AnnotationCommand(command.value(), command.desc(), command.helpMessage());

            String[] permissions;

            Permission permission = method.getAnnotation(Permission.class);
            if (permission != null)
                permissions = permission.value();
            else permissions = new String[0];

            HashSet<String> permissionsSet = new HashSet<>();
            permissionsSet.addAll(Arrays.asList(permissions));
            annotationCommand.permissions = permissionsSet;

            List<Parameter> parameters = new ArrayList<>(Arrays.asList(method.getParameters()));

            int senderIndex = getSenderIndex(parameters);

            List<Argument> arguments = parseParameters(argumentManager, parameters);
            CommandNode node = annotationCommand.commandNode;
            for (int i = 0; i < arguments.size(); i++) {

                CommandNode child = null;
                for (CommandNode node1 : node.getChildren()) {
                    if (node1.argument != null) {
                        Argument argument = arguments.get(i);
                        if (argument == null)
                            continue;
                        if (node1.argument.getName().equals(argument.getName()))
                            child = node1;
                    }
                }
                if (child == null) {
                    child = new CommandNode(arguments.get(i));
                    node.addChild(child);
                }

                if (senderIndex == i) {
                    child.argument = new Argument() {
                        @Override
                        public String getName() {
                            return "sender";
                        }

                        @Override
                        public Class responsibleClass() {
                            return null;
                        }

                        @Override
                        public ParseResult parseArgs(String[] args) {
                            return new ParseResult(null, 0, false);
                        }

                        @Override
                        public String getInputHelp() {
                            return null;
                        }
                    };
                }


                unknowndomain.command.completion.Completer completer = null;
                if (child.argument != null && child.getCompleter() == null) {
                    Completer completerAnno = parameters.get(i).getAnnotation(Completer.class);
                    if (completerAnno != null)
                        completer = completeManager.getCompleter(completerAnno.value());
                    else completer = completeManager.getCompleter(child.argument.responsibleClass());
                    child.setCompleter(completer);
                }

                node = child;
            }

            node.setInstance(commandHandler);
            node.setHandleMethod(method);

            commands.add(annotationCommand);
        }
        return commands;
    }

    private static int getSenderIndex(List<Parameter> parameters) {
        for (int i = 0; i < parameters.size(); i++) {
            if (parameters.get(i).getAnnotation(Sender.class) != null)
                return i;
        }
        return -1;
    }

    private static List<Argument> parseParameters(ArgumentManager manager, List<Parameter> parameters) {

        ArrayList<Argument> arguments = new ArrayList<>();

        for (Parameter parameter : parameters) {
            Argument argument;
            ArgumentHandler argumentHandler = parameter.getAnnotation(ArgumentHandler.class);
            if (argumentHandler != null)
                argument = manager.getArgument(argumentHandler.value());
            else argument = manager.getArgument(parameter.getType());
            arguments.add(argument);
        }
        return arguments;
    }


}
