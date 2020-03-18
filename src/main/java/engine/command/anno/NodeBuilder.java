package engine.command.anno;

import engine.command.CommandManager;
import engine.command.argument.ArgumentManager;
import engine.command.suggestion.SuggesterManager;

import java.util.ArrayList;
import java.util.List;

import static engine.command.anno.NodeAnnotationCommand.staticArgumentManage;
import static engine.command.anno.NodeAnnotationCommand.staticSuggesterManager;

public abstract class NodeBuilder {

    protected ArgumentManager argumentManager = staticArgumentManage;

    protected CommandManager commandManager;

    protected SuggesterManager suggesterManager = staticSuggesterManager;

    protected List<Object> providerList = new ArrayList<>();

    protected NodeBuilder(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public NodeBuilder setArgumentManager(ArgumentManager argumentManager) {
        this.argumentManager = argumentManager;
        return this;
    }

    public NodeBuilder setSuggesterManager(SuggesterManager suggesterManager) {
        this.suggesterManager = suggesterManager;
        return this;
    }

    public NodeBuilder addProvider(Object object){
        this.providerList.add(object);
        return this;
    }

    protected abstract List<engine.command.Command> build();

    public void register() {
        build().stream()
                .filter(command -> !commandManager.hasCommand(command.getName()))
                .forEach(command -> commandManager.registerCommand(command));
    }

}
