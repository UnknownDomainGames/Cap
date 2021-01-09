package engine.command.anno;

import engine.command.Command;
import engine.command.*;
import engine.command.argument.ArgumentManager;
import engine.command.argument.SimpleArgumentManager;
import engine.command.suggestion.SimpleSuggesterManager;
import engine.command.suggestion.SuggesterManager;
import engine.command.util.CommandNodeUtil;
import engine.command.util.StringArgs;
import engine.command.util.SuggesterHelper;
import engine.command.util.Type;
import engine.command.util.context.DequeContext;
import engine.command.util.node.CommandNode;
import engine.command.util.node.EmptyArgumentNode;
import engine.command.util.node.Nodeable;
import engine.command.util.node.SenderNode;
import engine.permission.Permissible;

import java.util.*;
import java.util.stream.Collectors;

public class NodeAnnotationCommand extends Command implements Nodeable {

    private static final PermissionWrapper TRUE = new PermissionWrapper(null, "") {
        @Override
        public boolean hasPermission() {
            return true;
        }
    };

    private static final PermissionWrapper FALSE = new PermissionWrapper(null, "") {
        @Override
        public boolean hasPermission() {
            return false;
        }
    };

    protected final static ArgumentManager staticArgumentManage = new SimpleArgumentManager();

    protected final static SuggesterManager staticSuggesterManager = new SimpleSuggesterManager();

    public static final CommandBuilderGetter<ClassAnnotationCommand.ClassAnnotationBuilder> CLASS = new ClassBuilderGetter();

    public static final CommandBuilderGetter<MethodAnnotationCommand.AnnotationCommandBuilder> METHOD = new MethodBuilderGetter();

    private CommandNode node = new EmptyArgumentNode();

    public NodeAnnotationCommand(String name, String description, String helpMessage) {
        super(name, description, helpMessage);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args == null || args.length == 0) {
            if (node.canExecuteCommand()) {
                if (!hasPermission(sender, node.getPermissionExpression())) {
                    permissionNotEnough(sender, args, node.getPermissionExpression());
                    return;
                }
                node.getExecutor().accept(List.of());
            } else {
                CommandNode parseResult = parseArgs(sender, args);
                if (parseResult != null && parseResult.canExecuteCommand()) {
                    if (!hasPermission(sender, parseResult.getPermissionExpression())) {
                        permissionNotEnough(sender, args, parseResult.getPermissionExpression());
                        return;
                    }
                    execute(parseResult);
                    return;
                }
                commandWrongUsage(sender, args);
            }
        } else {
            CommandNode parseResult = parseArgs(sender, args);
            if (CommandNodeUtil.getRequiredArgsSumFromParent2Child(parseResult) != args.length) {
                commandWrongUsage(sender, args);
                return;
            }
            if (!parseResult.canExecuteCommand()) {
                commandWrongUsage(sender, args);
                return;
            }
            if (!hasPermission(sender, parseResult.getPermissionExpression())) {
                permissionNotEnough(sender, args, parseResult.getPermissionExpression());
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

    private void permissionNotEnough(CommandSender sender, String[] args, String requiredPermissions) {
        sender.sendCommandException(
                new CommandException(CommandException.Type.PERMISSION_NOT_ENOUGH, sender, this, args, requiredPermissions));
    }

    private void commandWrongUsage(CommandSender sender, String[] args) {
        sender.sendCommandException(
                new CommandException(CommandException.Type.COMMAND_WRONG_USAGE, sender, this, args, null));
    }

    private boolean hasPermission(Permissible permissible, String permissionExpression) {
        if (permissionExpression == null)
            return true;
        Stack<PermissionWrapper> permissionWrapperStack = new Stack<>();
        Stack<Character> operatorStack = new Stack<>();

        StringBuilder stringBuilder = new StringBuilder();

        for (char c : permissionExpression.toCharArray()) {
            switch (c) {
                case '(':
                case '!':
                    stringBuilder.delete(0, stringBuilder.length());
                    operatorStack.push(c);
                    break;
                case '|': {
                    String s = stringBuilder.toString();
                    if (!s.isBlank()) {
                        permissionWrapperStack.push(new PermissionWrapper(permissible, s.trim()));
                    }
                    stringBuilder.delete(0, stringBuilder.length());

                    if (!operatorStack.isEmpty()) {
                        char peek = operatorStack.peek().charValue();
                        if (peek == c || '(' == peek) {
                            operatorStack.push(c);
                            break;
                        }
                        while (!operatorStack.isEmpty() && peek != '(' && (peek == '|' || peek == '&' || peek == '!')) {
                            computePermission(operatorStack.pop(), permissionWrapperStack);
                            peek = operatorStack.peek();
                        }
                    }

                    operatorStack.push(c);
                    break;
                }
                case '&': {
                    String s = stringBuilder.toString();
                    if (!s.isBlank()) {
                        permissionWrapperStack.push(new PermissionWrapper(permissible, s.trim()));
                    }
                    stringBuilder.delete(0, stringBuilder.length());
                    operatorStack.push(c);
                    break;
                }
                case ')': {
                    String s = stringBuilder.toString();
                    if (!s.isBlank()) {
                        permissionWrapperStack.push(new PermissionWrapper(permissible, s.trim()));
                    }
                    stringBuilder.delete(0, stringBuilder.length());

                    char peek = operatorStack.peek().charValue();
                    while (peek != '(') {
                        computePermission(operatorStack.pop(), permissionWrapperStack);
                        peek = operatorStack.peek();
                    }
                    operatorStack.pop();
                    break;
                }
                default:
                    stringBuilder.append(c);
            }
        }

        String s = stringBuilder.toString();
        if (!s.isBlank())
            permissionWrapperStack.push(new PermissionWrapper(permissible, s.trim()));

        while (!operatorStack.isEmpty())
            computePermission(operatorStack.pop(), permissionWrapperStack);

        return permissionWrapperStack.peek().hasPermission();
    }

    private void computePermission(char operator, Stack<PermissionWrapper> stack) {
        switch (operator) {
            case '!':
                stack.push(stack.pop().hasPermission() ? FALSE : TRUE);
                break;
            case '|': {
                PermissionWrapper pop2 = stack.pop();
                PermissionWrapper pop1 = stack.pop();
                stack.push(pop1.hasPermission() || pop2.hasPermission() ? TRUE : FALSE);
                break;
            }
            case '&': {
                PermissionWrapper pop2 = stack.pop();
                PermissionWrapper pop1 = stack.pop();
                stack.push(pop1.hasPermission() && pop2.hasPermission() ? TRUE : FALSE);
                break;
            }
            default:
                throw new IllegalStateException("operator: " + operator);
        }
    }

    private static class PermissionWrapper {
        Permissible permissible;

        String permission;

        public PermissionWrapper(Permissible permissible, String permission) {
            this.permissible = permissible;
            this.permission = permission;
        }

        public boolean hasPermission() {
            return permissible.hasPermission(permission);
        }

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

    private class LinkedDequeContext extends LinkedList<Object> implements DequeContext {

        private final CommandSender sender;

        public LinkedDequeContext(CommandSender sender) {
            this.sender = sender;
        }

        @Override
        public CommandSender getSender() {
            return sender;
        }

        @Override
        public int length() {
            return this.size();
        }

        @Override
        public int first(Type type) {
            for (int i = 0; i < size(); i++) {
                if (typeAt(i).is(type))
                    return i;
            }
            return -1;
        }

        @Override
        public int last(Type type) {
            for (int i = size() - 1; i >= 0; i--) {
                if (typeAt(i).is(type))
                    return i;
            }
            return -1;
        }

        @Override
        public Type typeAt(int index) {
            return Type.of(get(index).getClass());
        }

        @Override
        public Object valueAt(int index) {
            return get(index);
        }
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
            //假如两者都能执行命令，则检查合计优先级
            else if (bestNode.canExecuteCommand() && checkNode.canExecuteCommand()) {
                return CommandNodeUtil.getLinkedFromParent2Child(checkNode).stream().mapToInt(CommandNode::priority).sum() >
                        CommandNodeUtil.getLinkedFromParent2Child(bestNode).stream().mapToInt(CommandNode::priority).sum();
            } else if (bestNode.canExecuteCommand() && !checkNode.canExecuteCommand()) {
                return false;
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
        for (CommandNode node : results.stream().filter(node1 -> leafNodePermissionEnough(sender, node1) && CommandNodeUtil.getRequiredArgsSumFromParent2Child(node1) == args.length - 1).collect(Collectors.toList())) {
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
            if (commandNode.getPermissionExpression() == null || sender.hasPermission(commandNode.getPermissionExpression()))
                return true;
        }
        return false;
    }

    @Override
    public List<String> getTips(CommandSender sender, String[] args) {
        StringArgs stringArgs = new StringArgs(Arrays.copyOfRange(args, 0, args.length - 1));
        HashSet<CommandNode> results = new HashSet<>();
        parse(node, sender, stringArgs, results);

        CommandNode nearestNode = null;
        int nearestDepth = Integer.MAX_VALUE;

        for (CommandNode result : results) {
            for (CommandNode child : result.getChildren()) {
                int depth = CommandNodeUtil.getDepth(child);
                if (depth < nearestDepth) {
                    nearestNode = result;
                    nearestDepth = depth;
                }
            }
        }

        if (nearestNode == null || nearestNode.getChildren().isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<CommandNode> nodes = CommandNodeUtil.getShortestPath(nearestNode);
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
        StringArgs stringArgs = new StringArgs(Arrays.copyOfRange(args, 0, args.length - 1));
        HashSet<CommandNode> results = new HashSet<>();
        parse(node, sender, stringArgs, results);

        StringArgs args1 = new StringArgs(args);
        for (CommandNode node : results.stream().filter(node1 -> leafNodePermissionEnough(sender, node1)).collect(Collectors.toList())) {
            for (CommandNode child : node.getChildren()) {
                args1.setIndex(args.length - 1);
                if (child.parse(sender, args1))
                    return ArgumentCheckResult.Valid();
            }
        }
        return ArgumentCheckResult.Error("/" + this.getName() + " " + formatArgs(args) + " <- wrong");
    }

    private String formatArgs(String[] args) {
        return Arrays.stream(args).map(s -> s + " ").collect(Collectors.joining());
    }

    @Override
    public CommandNode getNode() {
        return node;
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
