package engine.command.anno;

import engine.command.Command;
import engine.command.CommandManager;
import engine.command.argument.ArgumentManager;
import engine.command.suggestion.SuggesterManager;
import engine.command.util.CommandNodeUtil;
import engine.command.util.node.CommandNode;
import engine.command.util.node.Nodeable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class MethodAnnotationCommand extends NodeAnnotationCommand implements Nodeable {

    private MethodAnnotationCommand(String name, String description, String helpMessage) {
        super(name, description, helpMessage);
    }

    public static AnnotationCommandBuilder getBuilder(CommandManager commandManager) {
        return new AnnotationCommandBuilder(commandManager);
    }

    public static class AnnotationCommandBuilder extends NodeBuilder {

        private Set<Object> commandHandler = new HashSet<>();

        private AnnotationCommandBuilder(CommandManager commandManager) {
            super(commandManager);
        }

        public AnnotationCommandBuilder setArgumentManager(ArgumentManager argumentManager) {
            return (AnnotationCommandBuilder) super.setArgumentManager(argumentManager);
        }

        public AnnotationCommandBuilder setSuggesterManager(SuggesterManager suggesterManager) {
            return (AnnotationCommandBuilder) super.setSuggesterManager(suggesterManager);
        }

        public AnnotationCommandBuilder addProvider(Object object) {
            return (AnnotationCommandBuilder) super.addProvider(object);
        }

        public AnnotationCommandBuilder addCommandHandler(Object object) {
            commandHandler.add(object);
            return this;
        }

        protected List<Command> build() {
            return commandHandler.stream()
                    .map(object -> parse(object))
                    .collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
        }

        private List<Command> parse(Object o) {

            ArrayList<Command> list = new ArrayList();

            CommandNodeUtil annotationUtil = CommandNodeUtil.getMethodUtil(argumentManager, suggesterManager);

            providerList.forEach(object -> annotationUtil.addProvider(object));

            for (Method method : o.getClass().getMethods()) {

                engine.command.anno.Command commandAnnotation = method.getAnnotation(engine.command.anno.Command.class);

                if (commandAnnotation == null) {
                    continue;
                }

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

                Nodeable nodeable = (Nodeable) command;

                if (nodeable == null) {
                    nodeable = new MethodAnnotationCommand(commandAnnotation.value(), commandAnnotation.desc(), commandAnnotation.helpMessage());
                }


                List<CommandNode> nodes = null;

                for (Parameter parameter : method.getParameters()) {
                    List<CommandNode> children = annotationUtil.parseParameter(parameter);
                    if (nodes == null) {
                        nodes = children;
                        continue;
                    }
                    TreeSet<CommandNode> topNodes = new TreeSet<>();
                    for (CommandNode child : children) {
                        topNodes.add(CommandNodeUtil.getTopParent(child));
                    }
                    List<CommandNode> branchNodes = new ArrayList<>();
                    for (CommandNode parent : nodes) {
                        for (CommandNode topNode : topNodes) {
                            parent.addChild(topNode);
                            branchNodes.addAll(CommandNodeUtil.getAllLeafNode(topNode));
                        }
                    }
                    nodes = branchNodes;
                }

                nodes.forEach(commandNode -> commandNode.setExecutor((objects -> {
                    try {
                        method.invoke(o, objects.toArray());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                })));

                Permission permission = method.getAnnotation(Permission.class);
                if (permission != null) {
                    nodes.forEach(commandNode ->
                            commandNode.setNeedPermission(new HashSet<>(Arrays.asList(permission.value()))));
                }

                CommandNode mainNode = nodeable.getNode();

                for (CommandNode node : nodes) {
                    CommandNode clone = CommandNodeUtil.getTopParent(node).clone();
                    mainNode.addChild(clone);
                }

                list.add((Command) nodeable);
            }

            return list;
        }

    }
}