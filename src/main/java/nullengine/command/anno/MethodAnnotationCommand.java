package nullengine.command.anno;

import com.google.common.collect.Lists;
import nullengine.command.Command;
import nullengine.command.CommandManager;
import nullengine.command.argument.ArgumentManager;
import nullengine.command.suggestion.SuggesterManager;
import nullengine.command.util.CommandNodeUtil;
import nullengine.command.util.node.CommandNode;
import nullengine.command.util.node.Nodeable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class MethodAnnotationCommand extends NodeAnnotationCommand implements Nodeable {

    private MethodAnnotationCommand(String name, String description, String helpMessage) {
        super(name, description, helpMessage);
    }

    public static AnnotationCommandBuilder getBuilder(CommandManager commandManager) {
        return new AnnotationCommandBuilder(commandManager);
    }

    public static class AnnotationCommandBuilder extends NodeBuilder{

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

        public AnnotationCommandBuilder addProvider(Object object){
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

            providerList.forEach(object->annotationUtil.addProvider(object));

            for (Method method : o.getClass().getMethods()) {

                nullengine.command.anno.Command commandAnnotation = method.getAnnotation(nullengine.command.anno.Command.class);

                if (commandAnnotation == null){
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

                if (nodeable == null){
                    nodeable = new MethodAnnotationCommand(commandAnnotation.value(), commandAnnotation.desc(), commandAnnotation.helpMessage());
                }

                List<CommandNode> node = Lists.newArrayList(nodeable.getNode());

                for (Parameter parameter : method.getParameters()) {
                    List<CommandNode> children = annotationUtil.parseParameter(parameter);
                    ArrayList<CommandNode> branches = new ArrayList<>();
                    for (CommandNode parent : node){
                        for (CommandNode child : children){
                            CommandNode topCloneChild = CommandNodeUtil.getTopParent(child).clone();
                            parent.addChild(topCloneChild);
                            branches.addAll(CommandNodeUtil.getAllBottomNode(topCloneChild));
                        }
                    }

                    node = branches;
                }

                //Tip 注释部分用于排插问题
//                System.out.println("register");
//                for(CommandNode commandNode : node){
//                    CommandNodeUtil.showLink(commandNode);
//                }

                node.forEach(commandNode -> commandNode.setExecutor((objects -> {
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
                    node.forEach(commandNode ->
                            commandNode.setNeedPermission(new HashSet<>(Arrays.asList(permission.value()))));
                }

                if (nodeable instanceof NodeAnnotationCommand){
                    ((NodeAnnotationCommand) nodeable).flush();
                }
                list.add((Command) nodeable);
            }

            return list;
        }

    }
}