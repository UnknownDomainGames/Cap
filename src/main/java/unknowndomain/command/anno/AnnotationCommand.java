package unknowndomain.command.anno;

import unknowndomain.command.Command;
import unknowndomain.command.CommandManager;
import unknowndomain.command.CommandResult;
import unknowndomain.command.CommandSender;
import unknowndomain.command.anno.node.*;
import unknowndomain.command.argument.Argument;
import unknowndomain.command.argument.ArgumentManager;
import unknowndomain.command.argument.SimpleArgumentManager;
import unknowndomain.command.completion.CompleteManager;
import unknowndomain.command.exception.CommandException;
import unknowndomain.command.exception.PermissionNotEnoughException;
import unknowndomain.permission.Permissible;

import javax.management.InstanceNotFoundException;
import java.lang.reflect.Constructor;
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

        public void register(){
            for(Command command : build()){
                if(!commandManager.hasCommand(command.name))
                    commandManager.registerCommand(command);
            }

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
            Generator generator = type.getAnnotation(Generator.class);

            CommandNode child = null;

            if (sender != null)
                child = handleSender(type.getType(), node);
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

        private SenderNode handleSender(Class<?> type, CommandNode node) {
            CommandNode child = new SenderNode((Class<? extends CommandSender>) type);
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

        private ArgumentNode parseParameter2Argument(Parameter parameter) {
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

                    ArgumentNode[] argumentNodes = buildArgumentNodes(noteConstructor);

                    Constructor finalNoteConstructor = noteConstructor;
                    return new MultiArgumentNode(objects -> {
                        try {
                            if (finalNoteConstructor.getDeclaringClass().isMemberClass()) {
                                ArrayList list = new ArrayList();
                                list.add(tryInstanceOuterClass(finalNoteConstructor.getDeclaringClass().getDeclaringClass()));
                                list.addAll(Arrays.asList(objects));
                                return finalNoteConstructor.newInstance(list.toArray());
                            } else
                                return finalNoteConstructor.newInstance(objects);
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }, argumentNodes);

                }

            }

            if (argument == null)
                if (argumentHandler != null)
                    throw new RuntimeException("argument not found: {" + parameter + "} argument:" + argumentHandler.value());
                else
                    throw new RuntimeException("argument not found: {" + parameter + "}");

            return new ArgumentNode(argument);
        }

        private Object tryInstanceOuterClass(Class declaringClass) {
            if(declaringClass.isMemberClass()) {
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
            }else {
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

        private ArgumentNode[] buildArgumentNodes(Constructor noteConstructor) {
            ArrayList<ArgumentNode> list = new ArrayList();

            Class clazz = noteConstructor.getDeclaringClass();

            boolean isMember = clazz.isMemberClass();

            int i = 0;

            //the first field in constructor of member class is who holds this member class
            if (isMember)
                i++;

            for (; i < noteConstructor.getParameters().length; i++) {

                list.add(parseParameter2Argument(noteConstructor.getParameters()[i]));
            }

            return list.toArray(new ArgumentNode[0]);
        }

    }
}