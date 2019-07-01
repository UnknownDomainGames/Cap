package unknowndomain.command.anno;

import unknowndomain.command.Command;
import unknowndomain.command.CommandManager;
import unknowndomain.command.CommandResult;
import unknowndomain.command.CommandSender;
import unknowndomain.command.anno.node.ArgumentNode;
import unknowndomain.command.anno.node.CommandNode;
import unknowndomain.command.anno.node.RequiredNode;
import unknowndomain.command.anno.node.SenderNode;
import unknowndomain.command.argument.Argument;
import unknowndomain.command.argument.ArgumentManager;
import unknowndomain.command.completion.CompleteManager;
import unknowndomain.command.exception.CommandException;
import unknowndomain.command.exception.PermissionNotEnoughException;
import unknowndomain.permission.Permissible;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
                    if (!hasPermission(sender.getPermissible(), lastEntry.node.getNeedPermission()))
                        return new CommandResult(new PermissionNotEnoughException(this.name));
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

    private boolean hasPermission(Permissible permissible, Collection<String> needPermission) {
        for (String s : needPermission) {
            if (!permissible.hasPermission(s))
                return false;
        }
        return true;
    }

    private List<ParseEntry> parseArgs(CommandSender sender, String[] args) {
        HashMap<NodeWrapper, Integer> ignore = new HashMap<>();

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

                if (ignore.getOrDefault(new NodeWrapper(child, index), 0) > ignoreCount++)
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
                ignore.put(new NodeWrapper(node, index), ignore.getOrDefault(node, 0) + 1);

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

    public static AnnotationCommandBuilder getBuilder(CommandManager commandManager) {
        return new AnnotationCommandBuilder(commandManager);
    }

    public static class AnnotationCommandBuilder {

        private Set<Object> commandHandler = new HashSet<>();

        private ArgumentManager argumentManager;

        private CommandManager commandManager;

        private CompleteManager completeManager;

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

        public List<Command> build() {
            return commandHandler.stream().map(object -> parse(object)).collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
        }

        private List<Command> parse(Object o) {

            ArrayList list = new ArrayList();

            for (Method method : o.getClass().getMethods()) {

                unknowndomain.command.anno.Command commandAnnotation = method.getAnnotation(unknowndomain.command.anno.Command.class);

                if (commandAnnotation == null)
                    continue;

                Command command = commandManager.getCommand(commandAnnotation.value());

                if (command != null && !(command instanceof AnnotationCommand)) {
                    throw new RuntimeException("command already exist " + command.name + " and not AnnotationCommand");
                }

                AnnotationCommand annotationCommand = (AnnotationCommand) command;

                if (annotationCommand == null)
                    annotationCommand = new AnnotationCommand(commandAnnotation.value(), commandAnnotation.desc(), commandAnnotation.helpMessage());

                CommandNode node = annotationCommand.annotationNode;

                for (Parameter parameter : method.getParameters()) {
                    node = getChildOrCreateAndAdd(parameter, node);
                }

                Permission permission = method.getAnnotation(Permission.class);
                HashSet<String> permissions = new HashSet();
                if (permission != null)
                    permissions.addAll(Arrays.asList(permission.value()));

                node.setNeedPermission(permissions);

                node.setInstance(o);
                node.setMethod(method);

                list.add(annotationCommand);
            }

            return list;
        }

        private CommandNode getChildOrCreateAndAdd(Parameter type, CommandNode node) {
            Sender sender = type.getAnnotation(Sender.class);
            Required required = type.getAnnotation(Required.class);

            CommandNode child = null;

            if (sender != null)
                child = handleSender(type.getType(), node);
            else if (required != null) {
                child = handleRequired(required, node);
            } else {
                child = handleArgument(type.getType(), node, type.getAnnotation(ArgumentHandler.class));
            }

            Completer completer = type.getAnnotation(Completer.class);
            if (completer != null)
                child.setCompleter(completeManager.getCompleter(completer.value()));

            return child;
        }

        private CommandNode handleSender(Class<?> type, CommandNode node) {
            CommandNode child = new SenderNode((Class<? extends CommandSender>) type);
            return foundChildOrAdd(node, child);
        }

        private CommandNode foundChildOrAdd(CommandNode node, CommandNode child) {

            for (CommandNode loopChild : node.getChildren()) {
                if (loopChild.equals(child))
                    return loopChild;
            }

            node.addChild(child);

            return child;
        }

        private CommandNode handleRequired(Required required, CommandNode node) {
            CommandNode child = new RequiredNode(required.value());
            return foundChildOrAdd(node, child);
        }

        private CommandNode handleArgument(Class<?> type, CommandNode node, ArgumentHandler annotation) {
            Argument argument;

            if (annotation != null)
                argument = argumentManager.getArgument(annotation.value());
            else argument = argumentManager.getArgument(type);

            if (argument == null)
                if (annotation != null)
                    throw new RuntimeException("argument not found:" + type + " argument:" + annotation.value());
                else
                    throw new RuntimeException("argument not found:" + type);

            CommandNode child = new ArgumentNode(argument);

            return foundChildOrAdd(node, child);
        }
    }
}