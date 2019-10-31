package nullengine.command.anno;

import nullengine.command.CommandManager;
import nullengine.command.argument.ArgumentManager;
import nullengine.command.suggestion.SuggesterManager;

import java.util.List;

import static nullengine.command.anno.NodeAnnotationCommand.staticArgumentManage;
import static nullengine.command.anno.NodeAnnotationCommand.staticSuggesterManager;

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

    protected abstract List<nullengine.command.Command> build();

    public void register() {
        build().stream()
                .filter(command -> !commandManager.hasCommand(command.getName()))
                .forEach(command -> commandManager.registerCommand(command));
    }
}
