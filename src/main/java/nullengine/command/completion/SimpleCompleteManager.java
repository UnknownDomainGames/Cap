package nullengine.command.completion;

import java.util.HashMap;

public class SimpleCompleteManager implements CompleteManager {

    private HashMap<Class, Completer> completerHashMapByClass = new HashMap<>();
    private HashMap<String, Completer> completerHashMapByName = new HashMap<>();

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
        return completerHashMapByName.get(name);
    }

    @Override
    public Completer getCompleter(Class clazz) {
        return completerHashMapByClass.get(clazz);
    }


}
