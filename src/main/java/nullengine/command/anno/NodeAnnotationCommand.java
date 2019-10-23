package nullengine.command.anno;

import nullengine.command.ArgumentCheckResult;
import nullengine.command.Command;
import nullengine.command.CommandSender;
import nullengine.command.completion.Completer;
import nullengine.command.exception.CommandWrongUseException;
import nullengine.command.exception.PermissionNotEnoughException;
import nullengine.command.util.CommandNodeUtil;
import nullengine.command.util.node.CommandNode;
import nullengine.command.util.node.EmptyArgumentNode;
import nullengine.command.util.node.Nodeable;
import nullengine.command.util.node.SenderNode;
import nullengine.permission.Permissible;

import java.util.*;
import java.util.stream.Collectors;

public class NodeAnnotationCommand extends Command implements Nodeable {

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
            if (node.canExecuteCommand())
                canExecuteNodes.add(node);
            nodes.addAll(node.getChildren());
        }
    }


    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (args == null || args.length == 0) {

            if (node.canExecuteCommand()) {
                if (!hasPermission(sender, node.getNeedPermission()))
                    throw new PermissionNotEnoughException(getName(), node.getNeedPermission().toArray(new String[0]));
                node.getExecutor().accept(Collections.EMPTY_LIST);
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
                    throw new PermissionNotEnoughException(getName(), node.getNeedPermission().toArray(new String[0]));
                List list = parseResult.collect();
                Collections.reverse(list);
                parseResult.getExecutor().accept(list);
                return;
            } else {
                throw new CommandWrongUseException(getName(), args);
            }
        }
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
                if (i + node.getNeedArgs() > args.length)
                    break;
                boolean success = node.parse(sender, this.getName(), Arrays.copyOfRange(args, i, i + node.getNeedArgs()));
                if (success) {

                    if (getParentNum(node) >= getParentNum(bestResult)) {
                        bestResult = node;
                    }
                    i += node.getNeedArgs();

                } else {
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
    public Completer.CompleteResult complete(CommandSender sender, String[] args) {

        String[] removeLast = args;
        if (args != null && args.length > 0)
            Arrays.copyOfRange(args, 0, args.length - 1);

        CommandNode result;
        if (removeLast.length == 0) {
            result = node;
        } else
            result = parseArgs(sender, removeLast);

        if (result instanceof SenderNode && result.getParent() != null)
            result = result.getParent();

        List<String> list = new ArrayList<>();

        for (CommandNode child : result.getChildren()) {
            if (child.getCompleter() != null)
                list.addAll(child.getCompleter().complete(sender, getName(), args).getComplete());
        }

        List<CommandNode> nodes = CommandNodeUtil.getShortestPath(result);
        List<String> tips = nodes.stream().map(node -> node.hasTip() ? node.getTip() : "").collect(Collectors.toList());

        return new Completer.CompleteResult(list, tips);
    }

    @Override
    public ArgumentCheckResult checkArguments(CommandSender sender, String[] args) {

        return null;
    }

    @Override
    public boolean handleUncaughtException(Exception e, CommandSender sender, String[] args) {
        return false;
    }

    @Override
    public CommandNode getNode() {
        return node;
    }


}
