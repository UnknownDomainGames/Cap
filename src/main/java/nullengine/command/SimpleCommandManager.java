package nullengine.command;

import nullengine.command.impl.DefaultCommandResolver;

public class SimpleCommandManager extends BaseCommandManager {

    @Override
    protected UncaughtExceptionHandler createUncaughtExceptionHandler() {
        return (e, sender, command, args) -> e.printStackTrace();
    }

    @Override
    protected CommandResolver createCommandResolver() {
        return new DefaultCommandResolver();
    }

}
