package engine.command;

import engine.command.impl.DefaultCommandResolver;

import java.util.logging.Logger;

public class SimpleCommandManager extends BaseCommandManager {

    private static Logger logger = Logger.getLogger("CAP");

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    protected CommandResolver createCommandResolver() {
        return new DefaultCommandResolver();
    }

}
