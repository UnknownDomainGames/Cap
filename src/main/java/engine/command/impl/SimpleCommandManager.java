package engine.command.impl;

import engine.command.BaseCommandManager;
import engine.command.CommandParser;

public class SimpleCommandManager extends BaseCommandManager {

    @Override
    protected CommandParser createCommandParser() {
        return new DefaultCommandParser();
    }

}
