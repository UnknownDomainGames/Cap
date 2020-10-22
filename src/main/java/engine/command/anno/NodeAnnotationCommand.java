package nullengine.command.anno;

import nullengine.command.Command;
import nullengine.command.*;
import nullengine.command.argument.ArgumentManager;
import nullengine.command.argument.SimpleArgumentManager;
import nullengine.command.exception.CommandWrongUseException;
import nullengine.command.exception.PermissionNotEnoughException;
import nullengine.command.suggestion.SimpleSuggesterManager;
import nullengine.command.suggestion.SuggesterManager;
import nullengine.command.util.CommandNodeUtil;
import nullengine.command.util.StringArgs;
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

        Collections.sort(canExecuteNodes);
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args == null || args.length == 0) {
            if (node.canExecuteCommand()) {
                if (!hasPermission(sender, node.getNeedPermission())) {
                    permissionNotEnough(sender, node.getNeedPermission().toArray(new String[0]));
                    return;
                }
                node.getExecutor().accept(Collections.EMPTY_LIST);
            } else {
                CommandNode parseResult = parseArgs(sender, args);
                if (parseResult != null && parseResult.canExecuteCommand()) {
                    if (!hasPermission(sender, parseResult.getNeedPermission())) {
                        permissionNotEnough(sender, parseResult.getNeedPermission().toArray(new String[0]));
                        return;
                    }
                    execute(parseResult);
                    return;
                }
                commandWrongUse(sender, args);
            }
        } else {
            CommandNode parseResult = parseArgs(sender, args);
            if (CommandNodeUtil.getRequiredArgsSumFromParent2Child(parseResult) != args.length) {
                commandWrongUse(sender, args);
                return;
            }
            if (!parseResult.canExecuteCommand()) {
                commandWrongUse(sender, args);
                return;
            }
            if (!hasPermission(sender, parseResult.getNeedPermission())) {
                permissionNotEnough(sender, parseResult.getNeedPermission().toArray(new String[0]));
                return;
            }
            execute(parseResult);
        }
    }

    private void execute(CommandNode node) {
        List list = node.collect();
        Collections.reverse(list);
        node.getExecutor().accept(list);
    }

    private void permissionNotEnough(CommandSender sender, String[] permission) {
        sender.handleException(CommandException.exception(new PermissionNotEnoughException(this.getName(), permission), this));
    }

    private void commandWrongUse(CommandSender sender, String[] args) {
        sender.handleException(CommandException.exception(new CommandWrongUseException(this.getName(), args), this, args));
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

        StringArgs stringArgs = new StringArgs(args);

        HashSet<CommandNode> results = new HashSet<>();

        //解析递归从根Node开始
        parse(node, sender, stringArgs, results);

        CommandNode bestResult = null;
        int bestNodeDepth = 0;

        //筛选最佳结果
        for (CommandNode result : results) {
            int depth = CommandNodeUtil.getDepth(result);
            if (bestNodeCheck(bestResult, bestNodeDepth, result, depth)) {
                bestResult = result;
                bestNodeDepth = depth;
            }
        }

        return bestResult;
    }

    private void parse(CommandNode node, CommandSender sender, StringArgs stringArgs, Set<CommandNode> result) {
        //如果当前的Args指针+Node需要的指针小于等于Args的长度，并且Node解析成功
        if (stringArgs.getIndex() + node.getRequiredArgsNum() <= stringArgs.getLength() && node.parse(sender, stringArgs)) {
            //所有叶子节点都是可执行节点，如果不是那肯定是构建树时出了问题
            //假如Node能执行命令，则必然是叶子节点，直接加入待选结果
            if (node.canExecuteCommand()) {
                result.add(node);
            } else {
                //保存当前指针
                int index = stringArgs.getIndex();
                for (CommandNode child : node.getChildren()) {
                    //子节点递归解析
                    parse(child, sender, stringArgs, result);
                    //重设指针
                    stringArgs.setIndex(index);
                }
            }
        } else {
            //假如不满足条件，则直接将父Node加入待选结果
            result.add(node.getParent());
        }
    }

    private boolean bestNodeCheck(CommandNode bestNode, int bestNodeDepth, CommandNode checkNode, int checkNodeDepth) {
        if (bestNode == null)
            return true;
        //假如检查的节点的深度比最佳节点的深度深
        if (checkNodeDepth > bestNodeDepth)
            return true;
        else if (checkNodeDepth == bestNodeDepth) {
            //假如最佳节点不能执行命令，但检查节点可以
            if (!bestNode.canExecuteCommand() && checkNode.canExecuteCommand()) {
                return true;
            }
            return checkNode.priority() > bestNode.priority();
        }
        return false;
    }

    @Override
    public List<String> suggest(CommandSender sender, String[] args) {
        StringArgs stringArgs = new StringArgs(Arrays.copyOfRange(args, 0, args.length - 1));
        HashSet<CommandNode> results = new HashSet<>();
        parse(node, sender, stringArgs, results);
        HashSet<String> suggests = new HashSet<>();
        for (CommandNode node : results.stream().filter(node1 -> leafNodePermissionEnough(sender, node1)).collect(Collectors.toList())) {
            for (CommandNode child : node.getChildren()) {
                if (child.getSuggester() != null) {
                    suggests.addAll(child.getSuggester().suggest(sender, getName(), args));
                }
            }
        }
        return SuggesterHelper.filterStartWith(new ArrayList<>(suggests), args[args.length - 1]);
    }

    private boolean leafNodePermissionEnough(CommandSender sender, CommandNode node) {
        Collection<? extends CommandNode> allLeafNode = CommandNodeUtil.getAllLeafNode(node);
        for (CommandNode commandNode : allLeafNode) {
            if (sender.hasPermission(commandNode.getNeedPermission()))
                return true;
        }
        return false;
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

        result = optimizeSuggesterNode(result);

        while (result instanceof SenderNode) {
            if (result.getParent() != null) {
                result = result.getParent();
                continue;
            }
        }

        return result;
    }

    private CommandNode optimizeSuggesterNode(CommandNode node) {
        if (node.getChildren().isEmpty()) {
            List<CommandNode> listWithOutSender = getNodeLinkWithOutSender(CommandNodeUtil.getLinkedFromParent2Child(node));
            for (CommandNode canExecuteNode : canExecuteNodes) {
                List<CommandNode> matchList = getNodeLinkWithOutSender(CommandNodeUtil.getLinkedFromParent2Child(canExecuteNode));
                if (matchList(listWithOutSender, matchList)) {
                    return matchList.get(listWithOutSender.size() - 1);
                }
            }
        }
        return node;
    }

    private boolean matchList(List<CommandNode> list, List<CommandNode> matchList) {
        if (list.size() >= matchList.size())
            return false;
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).same(matchList.get(i)))
                return false;
        }
        return true;
    }

    private List<CommandNode> getNodeLinkWithOutSender(List<CommandNode> nodeList) {
        return nodeList.stream().filter(node1 -> !(node1 instanceof SenderNode)).collect(Collectors.toList());
    }

    @Override
    public List<String> getTips(CommandSender sender, String[] args) {
        CommandNode result = suggestParse(sender, args);
        if (result == null)
            return Collections.EMPTY_LIST;
        if (CommandNodeUtil.getRequiredArgsSumFromParent2Child(result) != args.length - 1 || result == null || result.getChildren().isEmpty()) {
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
//            if (node.parse(sender, this.getName(), args[index - 1])) {
//                return ArgumentCheckResult.Valid();
//            }
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
                        .filter(node2 -> CommandNodeUtil.getRequiredArgsSumFromParent2Child(node2) == index)
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
