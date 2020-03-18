package engine.command.suggestion;

import java.util.HashMap;

public class SimpleSuggesterManager implements SuggesterManager {

    private HashMap<Class, Suggester> suggesterHashMapByClass = new HashMap<>();
    private HashMap<String, Suggester> suggesterHashMapByName = new HashMap<>();

    @Override
    public void putSuggester(NamedSuggester completer) {
        if (suggesterHashMapByName.containsKey(completer.getName())){
            throw new RuntimeException("suggester name already exist");
        }
        suggesterHashMapByName.put(completer.getName(), completer);
    }

    @Override
    public void setClassSuggester(Class clazz, Suggester suggester) {
        suggesterHashMapByClass.put(clazz, suggester);
    }

    @Override
    public Suggester getSuggester(String name) {
        return suggesterHashMapByName.get(name);
    }

    @Override
    public Suggester getSuggester(Class clazz) {
        return suggesterHashMapByClass.get(clazz);
    }


}
