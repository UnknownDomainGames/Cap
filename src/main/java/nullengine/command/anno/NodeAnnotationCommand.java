package nullengine.command.anno;

import nullengine.command.*;
import nullengine.command.Command;
import nullengine.command.argument.ArgumentManager;
import nullengine.command.argument.SimpleArgumentManager;
import nullengine.command.exception.CommandWrongUseException;
import nullengine.command.exception.PermissionNotEnoughException;
import nullengine.command.suggestion.SimpleSuggesterManager;
import nullengine.command.suggestion.SuggesterManager;
import nullengine.command.util.CommandNodeUtil;
import nullengine.command.util.SuggesterHelper;
import nullengine.command.util.node.CommandNode;
import nullengine.command.util.node.EmptyArgumentNode;
import nullengine.command.util.node.Nodeable;
import nullengine.command.util.node.SenderNode;
import nullengine.permission.Permissible;

import java.util.*;
import java.util.stream.Collectors;

public class NodeAnnotationCommand extends Command implements Nodeable {

    protected final static ArgumentManager staticArgumentManage = new SimpleArgumentManager();

    protected final static SuggesterManager staticSuggesterManager = new SimpleSuggesterManager();

    public static final CommandBuilderGetter<ClassAnnotationCommand.ClassAnnotationBuilder> CLASS = new ClassBuilderGetter();

    public static final CommandBuilderGetter<MethodAnnotationCommand.AnnotationCommandBuilder> METHOD = new MethodBuilderGetter();

    private CommandNode node = new EmptyArgumentNode();

    private List<CommandNode> canExecuteNodes = new ArrayList<>();

    public NodeAnnotationCommand(String name, String description, String helpMessage) {
        super(name, description, helpMessage);
    }

    public void flush() {
        canExecuteNodes.clear();

        List<CommandNode> nodes = new LinkedList<>();
        nodes.add(node);
        while (!nodes.isEmpty()) {
            CommandNode node = nodes.remove(0);
            if (node.canExecuteCommand()) {
                canExecuteNodes.add(node);
            }
            nodes.addAll(node.getChildren());
        }
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args == null || args.length == 0) {
            if (node.canExecuteCommand()) {
                if (!hasPermission(sender, node.getNeedPermission())) {
                    permissionNotEnough(sender,node.getNeedPermission().toArray(new String[0]));
                    return;
                }
                node.getExecutor().accept(Collections.EMPTY_LIST);
                return;
            } else {
                CommandNode commandNode = parseArgs(sender, args);
                if (commandNode != null && commandNode.canExecuteCommand()) {
                    execute(commandNode);
                    return;
                }
                commandNode = sameCheck(commandNode);
                if (commandNode == null){
                    commandWrongUse(sender,args);
                    return;
                }
                execute(commandNode);
            }
        } else {
            CommandNode parseResult = parseArgs(sender, args);
            if (CommandNodeUtil.getRequiredArgsAmountFromParent2Child(parseResult) != args.length) {
                commandWrongUse(sender,args);
                return;
            }
            if (parseResult.canExecuteCommand()) {
                if (!hasPermission(sender, parseResult.getNeedPermission())){
                    permissionNotEnough(sender,parseResult.getNeedPermission().toArray(new String[0]));
                    return;
                }
            } else {
                parseResult = sameCheck(parseResult);
                if (parseResult == null){
                    commandWrongUse(sender,args);
                    return;
                }
            }
            execute(parseResult);
        }
    }

    private CommandNode sameCheck(CommandNode node) {
        if (node.getParent() == null)
            return null;
        CommandNode parent = node.getParent();
        for (CommandNode child : parent.getChildren()) {
            if (child == node)
                continue;
            if (child.same(node) && child.canExecuteCommand())
                return child;
        }
        return null;
    }

    private void execute(CommandNode node) {
        List list = node.collect();
        Collections.reverse(list);
        node.getExecutor().accept(list);
    }

    private void permissionNotEnough(CommandSender sender, String[] permission) {
        sender.handleException(CommandException.exception(new PermissionNotEnoughException(this.getName(),permission), this));
    }

    private void commandWrongUse(CommandSender sender,String[] args){
        sender.handleException(CommandException.exception(new CommandWrongUseException(this.getName(),args),this,args));
    }

    private boolean hasPermission(Permissible permissible, Collection<String> needPermission) {
        for (String s : needPermission) {
            if (!permissible.hasPermission(s)) {
                return false;
            }
        }
        return true;
    }

    private CommandNode parseArgs(CommandSender sender, String[] args) {

        ArrayList<CommandNode> filterExecuteNodes = new ArrayList<>();

        for (CommandNode executeNode : canExecuteNodes) {
            if (CommandNodeUtil.getRequiredArgsAmountFromParent2Child(executeNode) >= args.length) {
                filterExecuteNodes.add(executeNode);
            }
        }

        CommandNode bestResult = null;
        int bestResultDepth = 0;

        ArrayCopy<String> arrayCopy = new ArrayCopy<>(args);

        for (CommandNode executeNode : filterExecuteNodes) {
            List<CommandNode> nodeList = CommandNodeUtil.getLinkedFromParent2Child(executeNode);

            int i = 0;
            for (CommandNode node : nodeList) {
                if (i + node.getRequiredArgsNum() > args.length) {
                    break;
                }
                boolean success = node.parse(sender, this.getName(), arrayCopy.copyOfRange(i, i + node.getRequiredArgsNum()));
                if (!success) {
                    break;
                }
                if (CommandNodeUtil.getDepthOn(node) >= bestResultDepth) {
                    bestResult = node;
                    bestResultDepth = CommandNodeUtil.getDepthOn(bestResult);
                }
                i += node.getRequiredArgsNum();
            }
        }
        return bestResult;
    }

    @Override
    public List<String> suggest(CommandSender sender, String[] args) {
        CommandNode result = suggestParse(sender, args);
        if (result == null)
            return Collections.EMPTY_LIST;
        Set<String> list = new HashSet<>();
        for (CommandNode child : result.getChildren()) {
            if (child.getSuggester() != null) {
                list.addAll(child.getSuggester().suggest(sender, getName(), args));
            }
        }
        return SuggesterHelper.filterStartWith(new ArrayList<>(list),args[args.length-1]);
    }

    protected CommandNode suggestParse(CommandSender sender, String[] args) {
        String[] removeLast = args;
        if (args != null && args.length > 0) {
            removeLast = Arrays.copyOfRange(args, 0, args.length - 1);
        }

        CommandNode result;
        if (removeLast.length == 0) {
            result = node;
        } else {
            result = parseArgs(sender, removeLast);
        }

        while (result instanceof SenderNode) {
            if (result.getParent() != null) {
                result = result.getParent();
            }
        }
        return result;
    }

    @Override
    public List<String> getTips(CommandSender sender, String[] args) {
        CommandNode result = suggestParse(sender, args);
        if (result == null)
            return Collections.EMPTY_LIST;
        if (CommandNodeUtil.getRequiredArgsAmountFromParent2Child(result) != args.length - 1 || result == null || result.getChildren().isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<CommandNode> nodes = CommandNodeUtil.getShortestPath(result);
        List<String> tips = nodes.stream()
                .filter(node1 -> !(node1 instanceof SenderNode)).map(node -> node.hasTip() ? node.getTip() : "")
                .collect(Collectors.toList());
        return tips;
    }

    @Override
    public ArgumentCheckResult checkLastArgument(CommandSender sender, String[] args) {
        if (args == null || args.length == 0) {
            return ArgumentCheckResult.Valid();
        }
        int index = args.length;
        for (CommandNode node : getNodesOnArgumentIndex(index)) {
            if (node.parse(sender, this.getName(), args[index - 1])) {
                return ArgumentCheckResult.Valid();
            }
        }
        return ArgumentCheckResult.Error("/" + this.getName() + " " + formatArgs(args) + " <- wrong");
    }

    private String formatArgs(String[] args) {
        return Arrays.stream(args).map(s -> s + " ").collect(Collectors.joining());
    }

    private List<CommandNode> getNodesOnArgumentIndex(int index) {
        return canExecuteNodes.stream()
                .map(node1 -> CommandNodeUtil
                        .getLinkedFromParent2Child(node1)
                        .stream()
                        .filter(node2 -> node2.getRequiredArgsNum() > 0)
                        .filter(node2 -> CommandNodeUtil.getRequiredArgsAmountFromParent2Child(node2) == index)
                        .findFirst().orElse(null)
                ).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public CommandNode getNode() {
        return node;
    }

    private class ArrayCopy<T> {

        private HashMap<Integer, T[]> cacheMap = new HashMap<>();
        T[] arrays;

        public ArrayCopy(T[] arrays) {
            this.arrays = arrays;
        }

        public T[] copyOfRange(int i, int to) {
            int key = geyKey(i, to);
            if (cacheMap.containsKey(key))
                return cacheMap.get(key);
            T[] copy = Arrays.copyOfRange(arrays, i, to);
            cacheMap.put(key, copy);
            return copy;
        }

        private int geyKey(int i, int i2) {
            return i * 100 + i2;
        }
    }

    private static class ClassBuilderGetter extends CommandBuilderGetter<ClassAnnotationCommand.ClassAnnotationBuilder> {

        public ClassAnnotationCommand.ClassAnnotationBuilder getBuilder(CommandManager commandManager) {
            return ClassAnnotationCommand.getBuilder(commandManager);
        }
    }

    private static class MethodBuilderGetter extends CommandBuilderGetter<MethodAnnotationCommand.AnnotationCommandBuilder> {

        public MethodAnnotationCommand.AnnotationCommandBuilder getBuilder(CommandManager commandManager) {
            return MethodAnnotationCommand.getBuilder(commandManager);
        }
    }
}
