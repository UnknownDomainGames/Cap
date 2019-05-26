package unknowndomain.command.anno;

import java.util.HashMap;

public class CommandParameterManager {

    private static HashMap<Class, CommandParameter> argumentHashMap = new HashMap<>();

    public static CommandParameter getCommandArgument(Class clazz){
        return argumentHashMap.get(clazz);
    }

    public static <T> void registerArgument(CommandParameter<T> tCommandArgument) {
        argumentHashMap.put(tCommandArgument.getClazz(),tCommandArgument);
    }

}
