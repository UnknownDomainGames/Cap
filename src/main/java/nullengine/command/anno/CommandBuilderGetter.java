package nullengine.command.anno;

import nullengine.command.CommandManager;

public abstract class CommandBuilderGetter<T> {
    public abstract T get(CommandManager commandManager);
}
