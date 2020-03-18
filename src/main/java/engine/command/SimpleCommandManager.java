package engine.command;

import engine.command.impl.DefaultCommandParser;

public class SimpleCommandManager extends BaseCommandManager {

    @Override
    protected CommandParser createCommandParser() {
        return new DefaultCommandParser();
    }

}
