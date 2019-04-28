package unknowndomain.command.argument;

import java.util.HashMap;

public class CommandArgumentManager {

    private static HashMap<Class,CommandArgument> argumentHashMap = new HashMap<>();

    public static CommandArgument getCommandArgument(Class clazz){
        return argumentHashMap.get(clazz);
    }

    public static <T> CommandArgumentBuilder<T> getBuilder(){
        return new CommandArgumentBuilder();
    }

    public static <T> void registerArgument(CommandArgument<T> tCommandArgument) {
        argumentHashMap.put(tCommandArgument.getClazz(),tCommandArgument);
    }

}
