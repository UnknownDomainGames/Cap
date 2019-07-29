package unknowndomain.command.anno;

import unknowndomain.command.Command;
import unknowndomain.command.CommandManager;
import unknowndomain.command.CommandSender;
import unknowndomain.command.anno.node.*;
import unknowndomain.command.argument.Argument;
import unknowndomain.command.argument.ArgumentManager;
import unknowndomain.command.argument.SimpleArgumentManager;
import unknowndomain.command.completion.CompleteManager;
import unknowndomain.command.exception.CommandNotFoundException;
import unknowndomain.command.exception.CommandWrongUseException;
import unknowndomain.command.exception.PermissionNotEnoughException;
import unknowndomain.permission.Permissible;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class AnnotationCommand extends Command {

    private CommandNode annotationNode = new ArgumentNode(null) {
        @Override
        public int getNeedArgs() {
            return 0;
        }

        @Override
        public List<Object> collect() {
            return Collections.emptyList();
        }
    };

    private AnnotationCommand(String name, String description, String helpMessage) {
        super(name, description, helpMessage);
    }

    public void execute(CommandSender sender, String[] args) {

        if (args == null || args.length == 0) {

            if (annotationNode.canExecuteCommand()) {
                if (!hasPermission(sender, annotationNode.getNeedPermission()))
                    throw new PermissionNotEnoughException(getName(), annotationNode.getNeedPermission().toArray(new String[0]));
                annotationNode.execute();

            } else throw new CommandWrongUseException(getName());

        } else {
            CommandNode parseResult = parseArgs(sender, args);

            if (sumNeedArgs(parseResult) != args.length) {
                throw new CommandWrongUseException(getName());
            }

            if (parseResult.canExecuteCommand()) {
                try {
                    if (!hasPermission(sender, parseResult.getNeedPermission()))
                        throw new PermissionNotEnoughException(getName(), annotationNode.getNeedPermission().toArray(new String[0]));
                    List list = parseResult.collect();
                    Collections.reverse(list);
                    parseResult.getMethod().invoke(parseResult.getInstance(), list.toArray());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                throw new CommandWrongUseException(getName());
            }
        }
        throw new CommandWrongUseException(getName());
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
    public List<String> complete(CommandSender sender, String[] args) {
        String[] removeLast = Arrays.copyOfRange(args, 0, args.length - 1);

        CommandNode result;
        if (removeLast.length == 0) {
            result = annotationNode;
        } else
            result = parseArgs(sender, removeLast);

        List<String> list = new ArrayList<>();

        for (CommandNode child : result.getChildren()) {
            list.addAll(child.getCompleter().complete(sender, getName(), args));
        }
        return list;
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

        private static ArgumentManager staticArgumentManage = new SimpleArgumentManager();

        private Set<Object> commandHandler = new HashSet<>();

        private ArgumentManager argumentManager = staticArgumentManage;

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

            for (Method method : o.getClass().getMethods()) {

                unknowndomain.command.anno.Command commandAnnotation = method.getAnnotation(unknowndomain.command.anno.Command.class);

                if (commandAnnotation == null)
                    continue;

                Command command = commandManager.getCommand(commandAnnotation.value()).orElseThrow(() -> new CommandNotFoundException(commandAnnotation.value()));


                if (command != null && !(command instanceof AnnotationCommand)) {
                    throw new RuntimeException("command already exist " + command.getName() + " and not AnnotationCommand");
                }
                if (command == null) {
                    for (Command parsedCommand : list) {
                        if (parsedCommand.getName().equals(commandAnnotation.value()))
                            command = parsedCommand;
                    }
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
                child = handleSender(type.getType(), node, sender);
            else if (required != null) {
                child = handleRequired(required, node);
            } else {
                child = handleArgument(type, node);
            }

            Completer completer = type.getAnnotation(Completer.class);
            if (completer != null)
                child.setCompleter(completeManager.getCompleter(completer.value()));

            return child;
        }

        private SenderNode handleSender(Class<?> type, CommandNode node, Sender sender) {
            Class<? extends CommandSender>[] allowedSenders = sender.value();
            CommandNode child;
            if (allowedSenders != null && allowedSenders.length != 0) {
                child = new SenderNode((Class<? extends CommandSender>) type);
                return (SenderNode) foundChildOrAdd(node, child);
            } else {
                child = new SenderNode((Class<? extends CommandSender>) type);
            }
            return (SenderNode) foundChildOrAdd(node, child);
        }

        private CommandNode foundChildOrAdd(CommandNode node, CommandNode child) {

            for (CommandNode loopChild : node.getChildren()) {
                if (loopChild.equals(child))
                    return loopChild;
            }

            node.addChild(child);

            return child;
        }

        private RequiredNode handleRequired(Required required, CommandNode node) {
            CommandNode child = new RequiredNode(required.value());
            return (RequiredNode) foundChildOrAdd(node, child);
        }

        private ArgumentNode handleArgument(Parameter parameter, CommandNode node) {
            return (ArgumentNode) foundChildOrAdd(node, parseParameter2Argument(parameter));
        }

        private CommandNode foundChildOrAdd(CommandNode node, List<ArgumentNode> children) {
            for (ArgumentNode argument : children) {
                node = foundChildOrAdd(node, argument);
            }
            return node;
        }

        private List<ArgumentNode> parseParameter2Argument(Parameter parameter) {
            ArrayList<ArgumentNode> argumentNodes = new ArrayList<>();

            Argument argument;
            ArgumentHandler argumentHandler = parameter.getAnnotation(ArgumentHandler.class);

            Class type = packing(parameter.getType());

            if (argumentHandler != null)
                argument = argumentManager.getArgument(argumentHandler.value());
            else argument = argumentManager.getArgument(type);

            if (argument == null) {
                Generator generator = null;
                Constructor noteConstructor = null;

                Class clazz = type;

                for (Constructor constructor : clazz.getConstructors()) {

                    if (generator != null)
                        break;

                    generator = (Generator) constructor.getAnnotation(Generator.class);
                    noteConstructor = constructor;
                }

                if (generator != null) {


                    List<ArgumentNode> argumentNodeArray = buildArgumentNodes(noteConstructor);

                    ArgumentNode last = argumentNodeArray.get(argumentNodeArray.size() - 1);

                    Constructor finalNoteConstructor = noteConstructor;

                    int args = finalNoteConstructor.getParameterCount();

                    if (finalNoteConstructor.getDeclaringClass().isMemberClass())
                        args--;

                    MultiArgumentNode multiArgumentNode = new MultiArgumentNode(last.getArgument(), objects -> {
                        try {
                            if (finalNoteConstructor.getDeclaringClass().isMemberClass()) {
                                ArrayList list = new ArrayList();
                                list.add(tryInstanceOuterClass(finalNoteConstructor.getDeclaringClass().getDeclaringClass()));
                                list.addAll(Arrays.asList(objects));
                                return finalNoteConstructor.newInstance(list.toArray());
                            } else
                                return finalNoteConstructor.newInstance(objects);
                        } catch (Exception e) {
                        }
                        return null;
                    }, args);

                    argumentNodeArray.set(argumentNodeArray.size() - 1, multiArgumentNode);

                    argumentNodes.addAll(argumentNodeArray);
                }

            } else argumentNodes.add(new ArgumentNode(argument));

            if (argumentNodes.isEmpty())
                if (argumentHandler != null)
                    throw new RuntimeException("argument not found: {" + parameter + "} argument:" + argumentHandler.value());
                else
                    throw new RuntimeException("argument not found: {" + parameter + "}");

            return argumentNodes;
        }

        private Object tryInstanceOuterClass(Class declaringClass) {
            if (declaringClass.isMemberClass()) {
                try {
                    return declaringClass.getConstructor(declaringClass.getDeclaringClass()).newInstance(tryInstanceOuterClass(declaringClass.getDeclaringClass()));
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    return declaringClass.getConstructor().newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
            throw new RuntimeException("no constructor to instance");
        }

        private Class packing(Class clazz) {

            switch (clazz.getName()) {
                case "int":
                    return Integer.class;
                case "float":
                    return Float.class;
                case "boolean":
                    return Boolean.class;
                case "double":
                    return Double.class;
                case "char":
                    return Character.class;
                case "long":
                    return Long.class;
                case "short":
                    return Short.class;
            }
            return clazz;
        }

        private List<ArgumentNode> buildArgumentNodes(Constructor noteConstructor) {
            ArrayList<ArgumentNode> list = new ArrayList();

            Class clazz = noteConstructor.getDeclaringClass();

            boolean isMember = clazz.isMemberClass();

            int i = 0;

            //the first field in constructor of member class is who holds this member class
            if (isMember)
                i++;

            for (; i < noteConstructor.getParameters().length; i++) {

                list.addAll(parseParameter2Argument(noteConstructor.getParameters()[i]));
            }

            return list;
        }

    }
}