package nullengine.command.inner;

import nullengine.command.Command;
import nullengine.command.CommandManager;
import nullengine.command.CommandSender;
import nullengine.command.anno.Generator;
import nullengine.command.anno.Required;
import nullengine.command.anno.Sender;
import nullengine.command.argument.ArgumentManager;
import nullengine.command.argument.SimpleArgumentManager;
import nullengine.command.completion.Completer;
import nullengine.command.inner.anno.Provide;
import nullengine.command.util.ClassUtil;
import nullengine.command.util.node.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InnerCommand extends Command implements Nodeable{

    private CommandNode commandNode;

    public InnerCommand(String name, CommandNode commandNode) {
        super(name);
        this.commandNode = commandNode;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {

    }

    @Override
    public Completer.CompleteResult complete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public boolean handleUncaughtException(Exception e, CommandSender sender, String[] args) {
        return false;
    }

    @Override
    public CommandNode getNode() {
        return commandNode;
    }

    public static class Builder {

        private CommandManager commandManager;

        private ArgumentManager argumentManager = new SimpleArgumentManager();

        private List<InnerCommand> commands = new ArrayList<>();

        public Builder(CommandManager commandManager) {
            this.commandManager = commandManager;
        }

        public Builder setArgumentManager(ArgumentManager argumentManager) {
            this.argumentManager = argumentManager;
            return this;
        }

        public Builder caseCommand(String commandName, Runnable commandHandler) {

            Class handlerClass = commandHandler.getClass();

            Method[] methods = handlerClass.getMethods();

            Field[] fields = handlerClass.getFields();

            CommandNode node = new EmptyArgumentNode();

            commands.add(new InnerCommand(commandName, node));

            // key:field name
            HashMap<String, ProvideWrapper> provideMap = new HashMap<>();


            for (Method method : methods) {
                Provide provide = method.getAnnotation(Provide.class);
                if (provide != null) {
                    String fieldName = provide.value();
                    Class returnType = method.getReturnType();
                    if (!checkField(fields, fieldName, returnType))
                        throw new RuntimeException("provide method error,cannot find field: " + fieldName + " or field class not assignable from method return");

                    CommandNode methodNode = constructMethod(method);
                    provideMap.put(fieldName, new ProvideWrapper(methodNode, provide.replace()));
                }
            }

            for (Field field : fields) {

                var child = constructField(field);

                if (provideMap.containsKey(field.getName())) {
                    provideMap.get(field.getName()).setTarget(child);
                }

                node.addChild(child);
                node = child;
            }

            for (ProvideWrapper provideWrapper : provideMap.values()) {
                var target = provideWrapper.target;

                if (provideWrapper.replace)
                    target.getParent().removeChild(target);

                target.getParent().addChild(provideWrapper.commandNode);

                var deepNode = getNodeOn(target, provideWrapper.getTargetDeep());
                if (deepNode.getChildren().isEmpty())
                    continue;
                deepNode = deepNode.getChildren().stream().findAny().get();
                getLower(provideWrapper.commandNode).addChild(deepNode);
            }

            return this;
        }

        private boolean checkField(Field[] fields, String fieldName, Class clazz) {
            for (Field field : fields) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                if (field.getName().equals(fieldName)) {
                    if (field.getType().isAssignableFrom(clazz))
                        return true;
                    return false;
                }
            }
            return false;
        }

        private CommandNode constructField(Field field) {

            Sender sender = field.getAnnotation(Sender.class);

            if (sender != null) {
                Class[] classes = sender.value();
                if (classes == null || classes.length == 0)
                    return handleSender(new Class[]{field.getType()});
                else return handleSender(classes);
            }

            Required required = field.getAnnotation(Required.class);

            if (required != null) {
                var requiredString = required.value();
                return handleRequired(requiredString);
            }

            return handleArgument(ClassUtil.packing(field.getType()));
        }

        private CommandNode handleSender(Class[] classes) {
            for (Class clazz : classes) {
                if (!CommandSender.class.isAssignableFrom(clazz))
                    throw new RuntimeException("Sender 贴了一个不是CommandSender的类");
            }
            return new SenderNode(classes);
        }

        private CommandNode handleRequired(String requiredString) {
            return new RequiredNode(requiredString);
        }

        private CommandNode handleArgument(Class<?> type) {
            for (Constructor constructor : type.getConstructors()) {
                if (constructor.getAnnotation(Generator.class) != null) {
                    return constructGenerator(constructor.getParameters());
                }
            }

            return new ArgumentNode(argumentManager.getArgument(type));
        }

        private CommandNode constructGenerator(Parameter[] parameters){

            for(int i = 0;i<parameters.length;i++){
                Parameter parameter = parameters[i];
                var completer = parameter.getAnnotation(nullengine.command.anno.Completer.class);
                CommandNode commandNode;
                if(i==parameters.length-1)
                    commandNode = new MultiArgumentNode()
            }

            return null;
        }

        private CommandNode constructMethod(Method method) {
            return null;
        }

        private CommandNode getNodeOn(CommandNode node, int deep) {
            for (int i = 0; i < deep; i++) {
                if (node.getChildren().isEmpty())
                    throw new RuntimeException("get child on deep:" + deep + " failed, node: " + node.toString());
                node = node.getChildren().stream().findAny().get();
            }
            return node;
        }

        private CommandNode getLower(CommandNode commandNode) {
            while (true) {
                if (commandNode.getChildren().isEmpty())
                    return commandNode;
                commandNode = commandNode.getChildren().stream().findFirst().get();
            }
        }

        public void register() {

        }


        private class ProvideWrapper {
            public final CommandNode commandNode;
            public final boolean replace;

            private CommandNode target;
            private int targetDeep = 0;

            public ProvideWrapper(CommandNode commandNode, boolean dispensable) {
                this.commandNode = commandNode;
                this.replace = dispensable;
            }

            public CommandNode getTarget() {
                return target;
            }

            public void setTarget(CommandNode target) {
                this.target = target;
                while (true) {
                    if (target.getChildren().isEmpty())
                        break;
                    target = target.getChildren().stream().findFirst().get();
                    targetDeep++;
                }
            }

            public int getTargetDeep() {
                return targetDeep;
            }
        }

    }

    public static Builder getBuilder(CommandManager commandManager) {
        return new Builder(commandManager);
    }

}
