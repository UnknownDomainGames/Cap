package engine.command.anno;

import engine.command.CommandManager;
import engine.command.argument.ArgumentManager;
import engine.command.suggestion.SuggesterManager;

import java.util.List;

import static engine.command.anno.NodeAnnotationCommand.staticArgumentManage;
import static engine.command.anno.NodeAnnotationCommand.staticSuggesterManager;

public abstract class NodeCommandBuilder {

    private CommandManager commandManager;

    private ArgumentManager argumentManager = staticArgumentManage;

    private SuggesterManager suggesterManager = staticSuggesterManager;

    public NodeCommandBuilder setArgumentManager(ArgumentManager argumentManager) {
        this.argumentManager = argumentManager;
        return this;
    }

    public NodeCommandBuilder setSuggesterManager(SuggesterManager suggesterManager) {
        this.suggesterManager = suggesterManager;
        return this;
    }

    public NodeCommandBuilder addProvider(Object provider){
        
        return this;
    }

    protected abstract List<engine.command.Command> build();

    public void register() {
        build().stream()
                .filter(command -> !commandManager.hasCommand(command.getName()))
                .forEach(command -> commandManager.registerCommand(command));
    }
}
