package unknowndomain.command.argument;

import java.util.HashMap;

public class ArgumentManager {

    private static HashMap<Class,Argument> argumentByClass = new HashMap<>();
    private static HashMap<String,Argument> argumentByName = new HashMap<>();

    public static void setClassArgument(Argument argument){
        if(argumentByName.containsKey(argument.getArgumentName()))
            throw new RuntimeException(argument.getArgumentName()+" already exist");

        argumentByClass.put(argument.getHandleClass(),argument);
        argumentByName.put(argument.getArgumentName(),argument);
    }

    public static void appendArgument(Argument argument){
        if(argumentByName.containsKey(argument.getArgumentName()))
            throw new RuntimeException(argument.getArgumentName()+" already exist");
        argumentByName.put(argument.getArgumentName(),argument);
    }

    public static Argument getArgumentByClass(Class clazz){
        return argumentByClass.get(clazz);
    }

    public static Argument getArgumentByName(String argumentName){
        return argumentByName.get(argumentName);
    }

    public static void unregisterArgument(String argumentName){
        argumentByName.remove(argumentName);
    }

}
