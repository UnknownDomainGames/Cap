package unknowndomain.command.anno;

import unknowndomain.command.*;
import unknowndomain.command.Command;
import unknowndomain.command.anno.node.ArgumentNode;
import unknowndomain.command.anno.node.CommandNode;
import unknowndomain.command.exception.CommandException;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class AnnotationCommand extends Command {

    private CommandNode annotationNode = new ArgumentNode(null);

    private AnnotationCommand(String name, String description, String helpMessage) {
        super(name, description, helpMessage);
    }

    public CommandResult execute(CommandSender sender, String[] args) {

        if (args == null || args.length == 0) {

            if (annotationNode.canExecuteCommand()) {

                return annotationNode.execute();

            } else return new CommandResult(false);

        } else {
            List<ParseEntry> parseResult;
            try {
                 parseResult = parseArgs(sender, args);
            } catch (CommandException e) {
                return new CommandResult(e);
            }

            if (parseResult.stream().map(ParseEntry::getValue).mapToInt(CommandNode::getNeedArgs).sum() != args.length) {
                return new CommandResult(false);
            }

            ParseEntry lastEntry = parseResult.get(parseResult.size() - 1);

            if (parseResult.get(parseResult.size() - 1).getValue().canExecuteCommand()) {
                try {
                    Object o = lastEntry.getValue().getMethod().invoke(lastEntry.getValue().getInstance(), parseResult.stream().map(ParseEntry::getKey).toArray());

                    if (o instanceof CommandResult) {
                        return (CommandResult) o;
                    } else if (o instanceof Boolean) {
                        return new CommandResult((Boolean) o);
                    } else return new CommandResult(true);

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {

                return new CommandResult(false);

            }
        }
        return new CommandResult(false);
    }

    private List<ParseEntry> parseArgs(CommandSender sender, String[] args) {
        HashMap<NodeWrppaer, Integer> ignore = new HashMap<>();

        List<ParseEntry> result = new ArrayList<>();

        List<ParseEntry> bestResult = new ArrayList<>();

        CommandNode node = annotationNode;

        for (int index = 0; index < args.length; ) {

            Object o = null;

            int ignoreCount = 0;

            for (CommandNode child : node.getChildren()) {

                if (o != null)
                    break;

                if (index + child.getNeedArgs() > args.length)
                    continue;

                String[] needArgs = Arrays.copyOfRange(args, index, index + child.getNeedArgs());

                Object parseResult = child.parseArgs(sender, this.name, needArgs);

                if (parseResult == null)
                    continue;

                if (ignore.getOrDefault(new NodeWrppaer(child, index), 0) > ignoreCount++)
                    continue;

                o = parseResult;

                node = child;
            }

            if (o == null) {
                if (node.getParent() == null) {

                    return bestResult;
                }

                result.remove(result.size() - 1);
                index -= node.getNeedArgs();
                ignore.put(new NodeWrppaer(node, index), ignore.getOrDefault(node, 0) + 1);

                node = node.getParent();

            } else {
                result.add(new ParseEntry(o, node));
                index += node.getNeedArgs();

                if (bestResult.size() < result.size()) {
                    bestResult.clear();
                    bestResult.addAll(result);
                }
            }

        }

        return bestResult;
    }

    private class NodeWrppaer {

        private CommandNode node;
        private int deep;

        public NodeWrppaer(CommandNode node, int deep) {
            this.node = node;
            this.deep = deep;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NodeWrppaer that = (NodeWrppaer) o;
            return deep == that.deep &&
                    Objects.equals(node, that.node);
        }

        @Override
        public int hashCode() {
            return Objects.hash(node, deep);
        }
    }

    private class ParseEntry implements Map.Entry {

        private Object parseResult;

        private CommandNode node;

        public ParseEntry(Object parseResult, CommandNode node) {
            this.parseResult = parseResult;
            this.node = node;
        }

        @Override
        public Object getKey() {
            return parseResult;
        }

        @Override
        public CommandNode getValue() {
            return node;
        }

        @Override
        public Object setValue(Object value) {
            return null;
        }
    }
}