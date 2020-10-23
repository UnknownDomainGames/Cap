package engine.command.argument;

import engine.command.argument.base.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SimpleArgumentManager implements ArgumentManager {

    private static List<Argument> baseArguments = new ArrayList<>();

    private HashMap<Class, Argument> argumentByClass = new HashMap<>();
    private HashMap<String, Argument> argumentByName = new HashMap<>();

    public SimpleArgumentManager() {
        baseArguments.stream().forEach(this::appendArgumentAndSetDefaultIfNotExist);
    }

    @Override
    public void setClassDefaultArgument(Argument argument) {
        argumentByClass.put(argument.responsibleClass(), argument);
    }

    @Override
    public void appendArgument(Argument argument) {
        if (argumentByName.containsKey(argument.getName())){
            throw new RuntimeException("argument already exist");
        }
        argumentByName.put(argument.getName(), argument);
    }

    public void appendArgumentAndSetDefaultIfNotExist(Argument argument) {
        appendArgument(argument);
        if (!argumentByClass.containsKey(argument.responsibleClass())){
            setClassDefaultArgument(argument);
        }
    }

    @Override
    public void removeArgument(Argument argument) {
        removeArgument(argument.getName());
    }

    @Override
    public void removeArgument(String argumentName) {
        argumentByName.remove(argumentName);
    }

    @Override
    public Argument getArgument(Class clazz) {
        return argumentByClass.get(clazz);
    }

    @Override
    public Argument getArgument(String argumentName) {
        return argumentByName.get(argumentName);
    }

    static {
        baseArguments.add(new IntegerArgument());
        baseArguments.add(new StringArgument());
        baseArguments.add(new BooleanArgument());
        baseArguments.add(new FloatArgument());
        baseArguments.add(new DoubleArgument());
        baseArguments.add(new LongArgument());
        baseArguments.add(new ShortArgument());
    }
}
