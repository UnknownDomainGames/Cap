package nullengine.command.anno;

import nullengine.command.Command;
import nullengine.command.CommandManager;
import nullengine.command.CommandSender;
import nullengine.command.argument.ArgumentManager;
import nullengine.command.argument.SimpleArgumentManager;
import nullengine.command.completion.CompleteManager;
import nullengine.command.completion.Completer;
import nullengine.command.completion.SimpleCompleteManager;
import nullengine.command.util.CommandNodeUtil;
import nullengine.command.util.node.CommandNode;
import nullengine.command.util.node.EmptyArgumentNode;
import nullengine.command.util.node.Nodeable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class InnerAnnotationCommand extends NodeAnnotationCommand {

    private BiFunction<CommandSender, String[], List<String>> completeOverrideFunction;

    public InnerAnnotationCommand(String name, String description, String helpMessage) {
        super(name, description, helpMessage);
    }

    @Override
    public Completer.CompleteResult complete(CommandSender sender, String[] args) {
        if (completeOverrideFunction != null) {

        }
        return super.complete(sender,args);
    }


    public static class Builder {

        private CommandManager commandManager;

        private ArgumentManager argumentManager = new SimpleArgumentManager();

        private CompleteManager completeManager = new SimpleCompleteManager();

        private List<Command> commands = new ArrayList<>();

        public Builder(CommandManager commandManager) {
            this.commandManager = commandManager;
        }

        public Builder setArgumentManager(ArgumentManager argumentManager) {
            this.argumentManager = argumentManager;
            return this;
        }

        public void setCompleteManager(CompleteManager completeManager) {
            this.completeManager = completeManager;
        }

        public Builder caseCommand(String commandName,String desc,String helpMessage,Runnable commandHandler){
            CommandNodeUtil.InnerUtil innerUtil = CommandNodeUtil.getInnerUtil(commandHandler, argumentManager, completeManager);

            List<CommandNode> nodeList = new ArrayList<>();

            Command command = commandManager.getCommand(commandName).orElse(null);

            if (command == null)
                command = new InnerAnnotationCommand(commandName,desc,helpMessage);

            if (!(command instanceof Nodeable))
                throw new RuntimeException("命令: " + commandName + " 已注册，且不支持");

            Nodeable nodeable = (Nodeable) command;

            nodeList.add(nodeable.getNode());

            Class clazz = commandHandler.getClass();

            Field[] fields = clazz.getFields();

            for (Field field : fields) {
                if (field.getAnnotation(Ignore.class) != null)
                    continue;
                List<CommandNode> fieldNodes = innerUtil.parseField(field);
                for (CommandNode node : nodeList)
                    for (CommandNode child : fieldNodes)
                        CommandNodeUtil.addChildren(node, child);
                nodeList = fieldNodes;
            }

            Consumer<List<Object>> executeConsumer = objects -> {
                int ignored = 0;
                for(int i =0;i<objects.size();i++){
                    Field field = fields[i+ignored];
                    if(field.getAnnotation(Ignore.class)!=null){
                        ignored++;
                        continue;
                    }
                    try {
                        field.setAccessible(true);
                        field.set(commandHandler,objects.get(i));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                commandHandler.run();
            };

            for(CommandNode node : nodeList){
                node.setExecutor(executeConsumer);
            }

            if (command instanceof InnerAnnotationCommand)
                for (Method method : clazz.getMethods()) {
                    if (method.getAnnotation(Complete.class) != null && List.class.isAssignableFrom(method.getReturnType())) {
                        InnerAnnotationCommand innerAnnotationCommand = (InnerAnnotationCommand) command;
                        innerAnnotationCommand.completeOverrideFunction = ((sender, strings) -> {
                            try {
                                return (List<String>) method.invoke(commandHandler, sender, strings);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            return Collections.EMPTY_LIST;
                        });
                    }
                }

            if (nodeable instanceof NodeAnnotationCommand)
                ((NodeAnnotationCommand) nodeable).flush();
            commands.add(command);
            return this;
        }

        public Builder caseCommand(String commandName,String desc,Runnable commandHandler){
            return caseCommand(commandName,desc,"",commandHandler);
        }

        public Builder caseCommand(String commandName, Runnable commandHandler) {
           return caseCommand(commandName,"",commandHandler);
        }


        public void register() {
            commands.stream()
                    .filter(command -> !commandManager.hasCommand(command.getName()))
                    .forEach(command -> commandManager.registerCommand(command));
        }
    }

    public static Builder getBuilder(CommandManager commandManager) {
        return new Builder(commandManager);
    }

}
