package nullengine.command.anno;

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

public class MethodAnnotationCommand extends Command implements Nodeable{

    private CommandNode annotationNode = new EmptyArgumentNode();

    private MethodAnnotationCommand(String name, String description, String helpMessage) {
        super(name, description, helpMessage);
    }

    public void execute(CommandSender sender, String[] args) {

        if (args == null || args.length == 0) {

            if (annotationNode.canExecuteCommand()) {
                if (!hasPermission(sender, annotationNode.getNeedPermission()))
                    throw new PermissionNotEnoughException(getName(), annotationNode.getNeedPermission().toArray(new String[0]));
                annotationNode.getExecutor().accept(Collections.EMPTY_LIST);
                return;
            } else{
                CommandNode commandNode = parseArgs(sender,args);
                if(commandNode!=null&&commandNode.canExecuteCommand()){
                    List<Object> list = commandNode.collect();
                    Collections.reverse(list);
                    commandNode.getExecutor().accept(list);
                    return;
                }
                throw new CommandWrongUseException(getName(),args);
            }

        } else {
            CommandNode parseResult = parseArgs(sender, args);

            if (sumNeedArgs(parseResult) != args.length) {
                throw new CommandWrongUseException(getName(),args);
            }

            if (parseResult.canExecuteCommand()) {
                if (!hasPermission(sender, parseResult.getNeedPermission()))
                    throw new PermissionNotEnoughException(getName(), annotationNode.getNeedPermission().toArray(new String[0]));
                List list = parseResult.collect();
                Collections.reverse(list);
                parseResult.getExecutor().accept(list);
                return;
            } else {
                throw new CommandWrongUseException(getName(),args);
            }
        }
    }

    private int sumNeedArgs(CommandNode node) {
        int needAges = 0;
        CommandNode node1 = node;
        needAges += node1.getNeedArgs();
        while (node1.getParent() != null) {
            node1 = node1.getParent();
            needAges += node1.getNeedArgs();
        }
        return needAges;
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

        List<String> tips = nodes.stream().map(node->node.getTip()==null?"":node.getTip()).collect(Collectors.toList());

        return new Completer.CompleteResult(list,tips);
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
        HashMap<NodeWrapper, Integer> ignore = new HashMap<>();

        CommandNode node = annotationNode;

        if(args==null||args.length==0){
            for(CommandNode child : node.getChildren()){
                if(child.parse(sender,this.getName(),args))
                    return child;
            }
        }

        CommandNode bestResult = node;

        for (int index = 0; index < args.length; ) {

            int ignoreCount = 0;

            CommandNode before = node;


            for (CommandNode child : node.getChildren()) {

                if (index + child.getNeedArgs() > args.length)
                    continue;

                String[] needArgs = Arrays.copyOfRange(args, index, index + child.getNeedArgs());

                boolean success = child.parse(sender, getName(), needArgs);

                if (success) {
                    if (ignore.getOrDefault(new NodeWrapper(child, index), 0) > ignoreCount++)
                        continue;

                    node = child;
                }
                continue;

            }

            if (before == node) {
                if (node.getParent() == null)
                    return bestResult;

                index -= node.getNeedArgs();
                ignore.put(new NodeWrapper(node, index), ignore.getOrDefault(node, 0) + 1);
                node = node.getParent();

            } else {
                index += node.getNeedArgs();
                if (getParentNum(node) >= getParentNum(bestResult))
                    bestResult = node;
            }

        }

        return bestResult;
    }

    private int getParentNum(CommandNode node) {
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

            CommandNodeUtil.AnnotationUtil annotationUtil = CommandNodeUtil.getAnnotationUtil(argumentManager,completeManager);

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

                Nodeable annotationCommand = (Nodeable) command;

                if (annotationCommand == null)
                    annotationCommand = new MethodAnnotationCommand(commandAnnotation.value(), commandAnnotation.desc(), commandAnnotation.helpMessage());

                CommandNode node = annotationCommand.getNode();

                for (Parameter parameter : method.getParameters()) {
                    CommandNode child = annotationUtil.parseParameter(parameter);
                    CommandNodeUtil.addChildren(node,child);
                    node = child;
                }

                node.setExecutor((objects -> {
                    try {
                        method.invoke(o,objects.toArray());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }));


                list.add((Command) annotationCommand);
            }

            return list;
        }

    }
}