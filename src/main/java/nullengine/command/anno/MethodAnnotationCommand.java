package nullengine.command.anno;

import com.google.common.collect.Lists;
import nullengine.command.Command;
import nullengine.command.CommandManager;
import nullengine.command.CommandSender;
import nullengine.command.argument.ArgumentManager;
import nullengine.command.argument.SimpleArgumentManager;
import nullengine.command.completion.CompleteManager;
import nullengine.command.completion.Completer;
import nullengine.command.completion.SimpleCompleteManager;
import nullengine.command.exception.CommandWrongUseException;
import nullengine.command.exception.PermissionNotEnoughException;
import nullengine.command.util.CommandNodeUtil;
import nullengine.command.util.node.*;
import nullengine.permission.Permissible;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class MethodAnnotationCommand extends Command implements Nodeable {

    private CommandNode annotationNode = new EmptyArgumentNode();

    private List<CommandNode> canExecuteNodes = new ArrayList<>();

    private MethodAnnotationCommand(String name, String description, String helpMessage) {
        super(name, description, helpMessage);
    }

    private void flush() {
        canExecuteNodes.clear();

        List<CommandNode> nodes = new LinkedList<>();
        nodes.add(annotationNode);

        while (!nodes.isEmpty()) {
            CommandNode node = nodes.remove(0);
            if (node.canExecuteCommand())
                canExecuteNodes.add(node);
            nodes.addAll(node.getChildren());
        }
    }

    public void execute(CommandSender sender, String[] args) {
        if (args == null || args.length == 0) {

            if (annotationNode.canExecuteCommand()) {
                if (!hasPermission(sender, annotationNode.getNeedPermission()))
                    throw new PermissionNotEnoughException(getName(), annotationNode.getNeedPermission().toArray(new String[0]));
                annotationNode.getExecutor().accept(Collections.EMPTY_LIST);
                return;
            } else {
                CommandNode commandNode = parseArgs(sender, args);
                if (commandNode != null && commandNode.canExecuteCommand()) {
                    List<Object> list = commandNode.collect();
                    Collections.reverse(list);
                    commandNode.getExecutor().accept(list);
                    return;
                }
                throw new CommandWrongUseException(getName(), args);
            }

        } else {
            CommandNode parseResult = parseArgs(sender, args);

            if (CommandNodeUtil.getTotalNeedArgs(parseResult) != args.length) {
                throw new CommandWrongUseException(getName(), args);
            }
            if (parseResult.canExecuteCommand()) {
                if (!hasPermission(sender, parseResult.getNeedPermission()))
                    throw new PermissionNotEnoughException(getName(), annotationNode.getNeedPermission().toArray(new String[0]));
                List list = parseResult.collect();
                Collections.reverse(list);
                parseResult.getExecutor().accept(list);
                return;
            } else {
                throw new CommandWrongUseException(getName(), args);
            }
        }
    }

    @Override
    public nullengine.command.completion.Completer.CompleteResult complete(CommandSender sender, String[] args) {
        String[] removeLast = Arrays.copyOfRange(args, 0, args.length - 1);

        CommandNode result;
        if (removeLast.length == 0) {
            result = annotationNode;
        } else
            result = parseArgs(sender, removeLast);

        List<String> list = new ArrayList<>();

        for (CommandNode child : result.getChildren()) {
            list.addAll(child.getCompleter().complete(sender, getName(), args).getComplete());
        }

        List<CommandNode> nodes = CommandNodeUtil.getShortestPath(result);

        List<String> tips = nodes.stream().map(node -> node.getTip() == null ? "" : node.getTip()).collect(Collectors.toList());

        return new Completer.CompleteResult(list, tips);
    }

    @Override
    public boolean handleUncaughtException(Exception e, CommandSender sender, String[] args) {
        return false;
    }

    private boolean hasPermission(Permissible permissible, Collection<String> needPermission) {
        for (String s : needPermission) {
            if (!permissible.hasPermission(s))
                return false;
        }
        return true;
    }

    private CommandNode parseArgs(CommandSender sender, String[] args) {

        ArrayList<CommandNode> filterExecuteNodes = new ArrayList<>();

        for (CommandNode executeNode : canExecuteNodes) {
            if (CommandNodeUtil.getTotalNeedArgs(executeNode) >= args.length)
                filterExecuteNodes.add(executeNode);
        }

        CommandNode bestResult = null;

        for (CommandNode executeNode : filterExecuteNodes) {
            List<CommandNode> nodeList = CommandNodeUtil.getLinkedFromParent2Child(executeNode);
            int i = 0;
            for (CommandNode node : nodeList) {
                if(i+node.getNeedArgs()>args.length)
                    break;
                boolean success = node.parse(sender,this.getName(),Arrays.copyOfRange(args,i,i+node.getNeedArgs()));
                if(success){

                    if(getParentNum(node)>=getParentNum(bestResult)){
                        bestResult = node;
                    }
                    i+=node.getNeedArgs();

                }else{
                    break;
                }
            }
        }

        return bestResult;
    }

    private int getParentNum(CommandNode node) {
        if (node == null)
            return 0;
        int i = 0;
        while (node.getParent() != null) {
            i++;
            node = node.getParent();
        }
        return i;
    }

    @Override
    public CommandNode getNode() {
        return annotationNode;
    }

    private class NodeWrapper {

        private CommandNode node;
        private int deep;

        public NodeWrapper(CommandNode node, int deep) {
            this.node = node;
            this.deep = deep;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NodeWrapper that = (NodeWrapper) o;
            return deep == that.deep &&
                    Objects.equals(node, that.node);
        }

        @Override
        public int hashCode() {
            return Objects.hash(node, deep);
        }
    }

    public static AnnotationCommandBuilder getBuilder(CommandManager commandManager) {
        return new AnnotationCommandBuilder(commandManager);
    }

    public static class AnnotationCommandBuilder {

        private static ArgumentManager staticArgumentManage = new SimpleArgumentManager();

        private static CompleteManager staticCompleteManager = new SimpleCompleteManager();

        private Set<Object> commandHandler = new HashSet<>();

        private ArgumentManager argumentManager = staticArgumentManage;

        private CommandManager commandManager;

        private CompleteManager completeManager = staticCompleteManager;

        private AnnotationCommandBuilder(CommandManager commandManager) {
            this.commandManager = commandManager;
        }

        public AnnotationCommandBuilder setArgumentManager(ArgumentManager argumentManager) {
            this.argumentManager = argumentManager;
            return this;
        }

        public AnnotationCommandBuilder addCommandHandler(Object object) {
            commandHandler.add(object);
            return this;
        }

        public AnnotationCommandBuilder setCompleteManager(CompleteManager completeManager) {
            this.completeManager = completeManager;
            return this;
        }

        private List<Command> build() {
            return commandHandler.stream().map(object -> parse(object)).collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
        }

        public void register() {
            build().stream()
                    .filter(command -> !commandManager.hasCommand(command.getName()))
                    .forEach(command -> commandManager.registerCommand(command));
        }

        private List<Command> parse(Object o) {

            ArrayList<Command> list = new ArrayList();

            CommandNodeUtil.AnnotationUtil annotationUtil = CommandNodeUtil.getAnnotationUtil(argumentManager, completeManager);

            for (Method method : o.getClass().getMethods()) {

                nullengine.command.anno.Command commandAnnotation = method.getAnnotation(nullengine.command.anno.Command.class);

                if (commandAnnotation == null)
                    continue;

                Command command = commandManager.getCommand(commandAnnotation.value()).orElse(null);


                if (command != null && !(command instanceof Nodeable)) {
                    throw new RuntimeException("command already exist " + command.getName() + " and not Nodeable");
                }
                if (command == null) {
                    for (Command parsedCommand : list) {
                        if (parsedCommand.getName().equals(commandAnnotation.value()))
                            command = parsedCommand;
                    }
                }

                Nodeable nodeable = (Nodeable) command;

                if (nodeable == null)
                    nodeable = new MethodAnnotationCommand(commandAnnotation.value(), commandAnnotation.desc(), commandAnnotation.helpMessage());

                List<CommandNode> node = Lists.newArrayList(nodeable.getNode());

                for (Parameter parameter : method.getParameters()) {
                    List<CommandNode> children = annotationUtil.parseParameter(parameter);
                    for (CommandNode parent : node)
                        for (CommandNode child : children)
                            CommandNodeUtil.addChildren(parent, child);
                    node = children;
                }

                node.forEach(commandNode -> commandNode.setExecutor((objects -> {
                    try {
                        method.invoke(o, objects.toArray());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                })));

                Permission permission = method.getAnnotation(Permission.class);
                if (permission != null) {
                    node.forEach(commandNode -> commandNode.setNeedPermission(new HashSet<>(Arrays.asList(permission.value()))));
                }

                if (nodeable instanceof MethodAnnotationCommand)
                    ((MethodAnnotationCommand) nodeable).flush();
                list.add((Command) nodeable);
            }

            return list;
        }

    }
}