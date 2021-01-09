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
import engine.command.util.context.ContextNode;
import engine.command.util.context.LinkedContext;
import engine.command.util.node.*;
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
        SimpleLinkedContext context = new SimpleLinkedContext(sender);
        if (args == null || args.length == 0) {
            if (node.canExecuteCommand()) {
                if (!hasPermission(sender, node.getPermissionExpression())) {
                    permissionNotEnough(sender, args, node.getPermissionExpression());
                    return;
                }
                node.getExecutor().accept(List.of());
            } else {
                Map.Entry<CommandNode, SimpleLinkedContext> parseResult = parseArgs(context, args);
                if (parseResult != null) {
                    CommandNode resultNode = parseResult.getKey();
                    if (resultNode.canExecuteCommand()) {
                        if (!hasPermission(sender, resultNode.getPermissionExpression())) {
                            permissionNotEnough(sender, args, resultNode.getPermissionExpression());
                            return;
                        }
                        execute(parseResult.getValue(), resultNode);
                        return;
                    }
                }
                commandWrongUsage(sender, args);
            }
        } else {
            Map.Entry<CommandNode, SimpleLinkedContext> parseResult = parseArgs(context, args);
            CommandNode resultNode = parseResult.getKey();
            if (CommandNodeUtil.getRequiredArgsSumFromParent2Child(resultNode) != args.length) {
                commandWrongUsage(sender, args);
                return;
            }
            if (!resultNode.canExecuteCommand()) {
                commandWrongUsage(sender, args);
                return;
            }
            if (!hasPermission(sender, resultNode.getPermissionExpression())) {
                permissionNotEnough(sender, args, resultNode.getPermissionExpression());
                return;
            }
            execute(parseResult.getValue(), resultNode);
        }
    }

    private void execute(SimpleLinkedContext context, CommandNode node) {
        ContextNode contextNode = context.root.getNext();
        while (contextNode != null) {
            contextNode.getOwner().collect(contextNode);
            contextNode = contextNode.getNext();
        }
        node.getExecutor().accept(context.valueToArray());
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

    private Map.Entry<CommandNode, SimpleLinkedContext> parseArgs(SimpleLinkedContext context, String[] args) {

        StringArgs stringArgs = new StringArgs(args);

        HashMap<CommandNode, SimpleLinkedContext> results = new HashMap<>();

        //解析递归从根Node开始
        parse(node, context, stringArgs, results);

        Map.Entry<CommandNode, SimpleLinkedContext> entry = null;
        CommandNode bestResult = null;
        int bestNodeDepth = 0;
        //筛选最佳结果
        for (Map.Entry<CommandNode, SimpleLinkedContext> result : results.entrySet()) {
            int depth = CommandNodeUtil.getDepth(result.getKey());
            if (bestNodeCheck(bestResult, bestNodeDepth, result.getKey(), depth)) {
                bestResult = result.getKey();
                bestNodeDepth = depth;
                entry = result;
            }
        }
        return entry;
    }

    private void parse(CommandNode node, SimpleLinkedContext context, StringArgs stringArgs, HashMap<CommandNode, SimpleLinkedContext> result) {
        //如果当前的Args指针+Node需要的指针小于等于Args的长度
        if (stringArgs.getIndex() + node.getRequiredArgsNum() <= stringArgs.getLength()) {
            ParseResult parseResult = node.parse(context, stringArgs);
            if (parseResult.isFail()) {
                result.put(node.getParent(), (SimpleLinkedContext) context.clone());
                return;
            } else if (parseResult.getValue() != null) {
                context.add(node, parseResult.getValue());
            }
            //所有叶子节点都是可执行节点，如果不是那肯定是构建树时出了问题
            //假如Node能执行命令，则必然是叶子节点，直接加入待选结果
            if (node.canExecuteCommand()) {
                result.put(node, (SimpleLinkedContext) context.clone());
            } else {
                //保存当前指针
                int index = stringArgs.getIndex();
                for (CommandNode child : node.getChildren()) {
                    //子节点递归解析
                    parse(child, context, stringArgs, result);
                    //重设指针
                    stringArgs.setIndex(index);
                    ContextNode lastNode = context.getLastNode();

                    while (lastNode.getOwner() != null && !lastNode.getOwner().equals(node)) {
                        lastNode = lastNode.getPre();
                    }
                    lastNode.setNext(null);
                }
            }
        } else {
            //假如不满足条件，则直接将父Node加入待选结果
            result.put(node.getParent(), (SimpleLinkedContext) context.clone());
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
        HashMap<CommandNode, SimpleLinkedContext> results = new HashMap<>();
        parse(node, new SimpleLinkedContext(sender), stringArgs, results);
        HashSet<String> suggests = new HashSet<>();
        for (CommandNode node : results.keySet().stream().filter(node1 -> leafNodePermissionEnough(sender, node1) && CommandNodeUtil.getRequiredArgsSumFromParent2Child(node1) == args.length - 1).collect(Collectors.toList())) {
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
        HashMap<CommandNode, SimpleLinkedContext> results = new HashMap<>();
        parse(node, new SimpleLinkedContext(sender), stringArgs, results);

        CommandNode nearestNode = null;
        int nearestDepth = Integer.MAX_VALUE;

        for (CommandNode result : results.keySet()) {
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
        HashMap<CommandNode, SimpleLinkedContext> results = new HashMap<>();
        parse(node, new SimpleLinkedContext(sender), stringArgs, results);

        StringArgs args1 = new StringArgs(args);
        for (CommandNode node : results.keySet().stream().filter(node1 -> leafNodePermissionEnough(sender, node1)).collect(Collectors.toList())) {
            for (CommandNode child : node.getChildren()) {
                args1.setIndex(args.length - 1);
                if (child.parse(results.get(node), args1).isSuccess())
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

    private static class SimpleLinkedContext implements LinkedContext {

        private final CommandSender sender;
        private final SimpleContextNode root = new SimpleContextNode(null, null);

        public SimpleLinkedContext(CommandSender sender) {
            this.sender = sender;
        }

        @Override
        public CommandSender getSender() {
            return sender;
        }

        @Override
        public int first(Type type) {
            for (int i = 0; i < size(); i++) {
                if (getTypeAt(i).is(type))
                    return i;
            }
            return -1;
        }

        @Override
        public int last(Type type) {
            for (int i = size() - 1; i >= 0; i--) {
                if (getTypeAt(i).is(type))
                    return i;
            }
            return -1;
        }

        @Override
        public Type getTypeAt(int index) {
            return Type.of(getValueAt(index).getClass());
        }

        @Override
        public Object getValueAt(int index) {
            return getNodeAt(index).getValue();
        }

        private ContextNode getNodeAt(int index) {
            ContextNode node = root.getNext();
            while (index != 0) {
                index--;
                if (node == null) {
                    throw new IllegalStateException();
                }
                node = node.getNext();
            }
            return node;
        }

        @Override
        public int size() {
            int i = 0;
            ContextNode node = root.getNext();
            if (node != null) {
                i++;
                node = node.getNext();
            }
            return i;
        }

        private ContextNode getLastNode() {
            ContextNode node = root;
            while (node.getNext() != null) {
                node = node.getNext();
            }
            return node;
        }

        @Override
        public void add(CommandNode handler, Object object) {
            setNext(getLastNode(), new SimpleContextNode(handler, object));
        }

        private void setNext(ContextNode node, ContextNode next) {
            node.setNext(next);
            next.setPre(node);
        }

        @Override
        public void add(CommandNode handler, int index, Object object) {
            ContextNode node = new SimpleContextNode(handler, object);
            ContextNode nodeAt = getNodeAt(index);
            if (nodeAt.getNext() != null) {
                ContextNode next = nodeAt.getNext();
                setNext(nodeAt, node);
                setNext(node, next);
                return;
            }
            setNext(nodeAt, node);
        }

        @Override
        public void remove(int index) {
            ContextNode nodeAt = getNodeAt(index);
            ContextNode pre = nodeAt.getPre();
            if (nodeAt.getNext() != null) {
                ContextNode next = nodeAt.getNext();
                setNext(pre, next);
            } else {
                pre.setNext(null);
            }
        }

        @Override
        public void removeLast() {
            ContextNode pre = getLastNode().getPre();
            pre.setNext(null);
        }

        @Override
        public List<Object> valueToArray() {
            List<Object> list = new ArrayList<>();
            ContextNode node = root.getNext();
            while (node != null) {
                list.add(node.getValue());
                node = node.getNext();
            }
            return list;
        }

        @Override
        public LinkedContext clone() {
            SimpleLinkedContext simpleLinkedContext = new SimpleLinkedContext(sender);
            if (this.root.getNext() != null)
                simpleLinkedContext.root.setNext(this.root.getNext().clone());
            return simpleLinkedContext;
        }

        private static class SimpleContextNode implements ContextNode {

            private CommandNode owner;
            private Object value;

            private ContextNode next;
            private ContextNode pre;

            public SimpleContextNode(CommandNode owner, Object value) {
                this.owner = owner;
                this.value = value;
            }

            @Override
            public Object getValue() {
                return value;
            }

            @Override
            public void setNext(ContextNode node) {
                this.next = node;
            }

            @Override
            public void setPre(ContextNode node) {
                this.pre = node;
            }

            @Override
            public ContextNode getNext() {
                return next;
            }

            @Override
            public ContextNode getPre() {
                return pre;
            }

            @Override
            public CommandNode getOwner() {
                return owner;
            }

            @Override
            public void setValue(Object value) {
                this.value = value;
            }

            @Override
            public String toString() {
                return "SimpleContextNode{" +
                        "owner=" + owner +
                        ", value=" + value +
                        '}';
            }

            public SimpleContextNode clone() {
                SimpleContextNode simpleContextNode = new SimpleContextNode(owner, value);
                if (this.next != null) {
                    ContextNode cloneNext = this.next.clone();
                    simpleContextNode.setNext(cloneNext);
                    cloneNext.setPre(simpleContextNode);
                }
                return simpleContextNode;
            }
        }
    }

}
