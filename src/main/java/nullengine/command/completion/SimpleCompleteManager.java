package nullengine.command.completion;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class SimpleCompleteManager implements CompleteManager {

    private HashMap<Class, Completer> completerHashMapByClass = new HashMap<>();
    private HashMap<String, Completer> completerHashMapByName = new HashMap<>();

    private static Completer noneCompleter = (sender, command, args) -> new Completer.CompleteResult();

    @Override
    public void putCompleter(NamedCompleter completer) {
        if (completerHashMapByName.containsKey(completer.getName()))
            throw new RuntimeException("completer name already exist");
        completerHashMapByName.put(completer.getName(), completer);
    }

    @Override
    public void setClassCompleter(Class clazz, Completer completer) {
        completerHashMapByClass.put(clazz, completer);
    }

    @Override
    public Completer getCompleter(String name) {
        return completerHashMapByName.getOrDefault(name, noneCompleter);
    }

    @Override
    public Completer getCompleter(Class clazz) {
        return completerHashMapByClass.getOrDefault(clazz, noneCompleter);
    }


}
