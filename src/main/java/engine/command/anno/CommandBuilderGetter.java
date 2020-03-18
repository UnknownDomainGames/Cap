package engine.command.anno;

import engine.command.CommandManager;

public abstract class CommandBuilderGetter<T> {
    public abstract T getBuilder(CommandManager commandManager);
}
