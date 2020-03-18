package engine.command;

import engine.command.impl.DefaultCommandParser;

import java.util.logging.Logger;

public class SimpleCommandManager extends BaseCommandManager {

    private static Logger logger = Logger.getLogger("CAP");

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    protected CommandParser createCommandResolver() {
        return new DefaultCommandParser();
    }

}
