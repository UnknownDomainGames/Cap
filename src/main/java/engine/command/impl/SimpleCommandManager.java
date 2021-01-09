package engine.command.impl;

import engine.command.BaseCommandManager;
import engine.command.CommandExceptionHandler;
import engine.command.CommandParser;

public class SimpleCommandManager extends BaseCommandManager {

    @Override
    protected CommandParser createCommandParser() {
        return new DefaultCommandParser();
    }

    @Override
    protected CommandExceptionHandler createExceptionHandler() {
        return new DefaultExceptionHandler();
    }

}
