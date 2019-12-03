package nullengine.command;

import nullengine.command.impl.DefaultCommandResolver;

public class SimpleCommandManager extends BaseCommandManager {

    @Override
    protected CommandResolver createCommandResolver() {
        return new DefaultCommandResolver();
    }

}
