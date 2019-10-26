package nullengine.command.anno;

import nullengine.command.Command;
import nullengine.command.CommandManager;
import nullengine.command.argument.ArgumentManager;
import nullengine.command.suggestion.SuggesterManager;
import nullengine.command.util.CommandNodeUtil;
import nullengine.command.util.node.CommandNode;
import nullengine.command.util.node.Nodeable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ClassAnnotationCommand extends NodeAnnotationCommand {

    public ClassAnnotationCommand(String name, String description, String helpMessage) {
        super(name, description, helpMessage);
    }

    public static class Builder {

        private CommandManager commandManager;

        private ArgumentManager argumentManager = staticArgumentManage;

        private SuggesterManager suggesterManager = staticSuggesterManager;

        private List<CommandHandlerWrapper> commandHandlerList = new ArrayList<>();

        public Builder(CommandManager commandManager) {
            this.commandManager = commandManager;
        }

        public Builder setArgumentManager(ArgumentManager argumentManager) {
            this.argumentManager = argumentManager;
            return this;
        }

        public void setSuggesterManager(SuggesterManager suggesterManager) {
            this.suggesterManager = suggesterManager;
        }

        public Builder caseCommand(String commandName, String desc, String helpMessage, Runnable commandHandler) {
            commandHandlerList.add(new CommandHandlerWrapper(commandName, desc, helpMessage, commandHandler));
            return this;
        }

        public Builder caseCommand(String commandName, String desc, Runnable commandHandler) {
            return caseCommand(commandName, desc, "", commandHandler);
        }

        public Builder caseCommand(String commandName, Runnable commandHandler) {
            return caseCommand(commandName, "", commandHandler);
        }

        private List<Command> build() {
            List<Command> commands = new ArrayList<>();
            for (CommandHandlerWrapper wrapper : commandHandlerList) {
                Runnable commandHandler = wrapper.instance;
                String commandName = wrapper.commandName;
                String desc = wrapper.desc;
                String helpMessage = wrapper.help;

                CommandNodeUtil.ClassUtil innerUtil = CommandNodeUtil.getInnerUtil(commandHandler, argumentManager, suggesterManager);

                List<CommandNode> nodeList = new ArrayList<>();

                Command command = commandManager.getCommand(commandName).orElse(null);

                if (command == null)
                    command = new ClassAnnotationCommand(commandName, desc, helpMessage);

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
                    ArrayList<CommandNode> branches = new ArrayList<>();
                    for (CommandNode node : nodeList)
                        for (CommandNode child : fieldNodes)
                            try {
                                CommandNode topCloneChild = CommandNodeUtil.getTopParent(child).clone();
                                node.addChild(topCloneChild);
                                branches.addAll(CommandNodeUtil.getAllBottomBranches(topCloneChild));
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                    nodeList = branches;
                }

                Consumer<List<Object>> executeConsumer = objects -> {
                    int ignored = 0;
                    for (int i = 0; i < objects.size(); i++) {
                        Field field = fields[i + ignored];
                        if (field.getAnnotation(Ignore.class) != null) {
                            ignored++;
                            continue;
                        }
                        try {
                            field.setAccessible(true);
                            field.set(commandHandler, objects.get(i));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    commandHandler.run();
                };

                for (CommandNode node : nodeList) {
                    node.setExecutor(executeConsumer);
                }

                if (nodeable instanceof NodeAnnotationCommand)
                    ((NodeAnnotationCommand) nodeable).flush();
                commands.add(command);
            }
            return commands;
        }

        public void register() {
            build().stream()
                    .filter(command -> !commandManager.hasCommand(command.getName()))
                    .forEach(command -> commandManager.registerCommand(command));
        }

        private class CommandHandlerWrapper {
            public final String commandName;
            public final String desc;
            public final String help;
            public final Runnable instance;

            public CommandHandlerWrapper(String commandName, String desc, String help, Runnable instance) {
                this.commandName = commandName;
                this.desc = desc;
                this.help = help;
                this.instance = instance;
            }
        }
    }

    public static Builder getBuilder(CommandManager commandManager) {
        return new Builder(commandManager);
    }

}
