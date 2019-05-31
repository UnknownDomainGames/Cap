package unknowndomain.command.traditional;

import unknowndomain.command.CommandManager;

public class TraditionalCommandRegister {

    public static TraditionalCommand registerCommand(String commandName){
        TraditionalCommand command = new TraditionalCommand(commandName);
        CommandManager.getInstance().registerCommand(command);
        return command;
    }

}
