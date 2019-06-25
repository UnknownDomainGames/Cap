package unknowndomain.command.completion;

import unknowndomain.command.CommandSender;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class SimpleCompleteManager implements CompleteManager {

    private HashMap<Class, Completer> completerHashMapByClass = new HashMap<>();
    private HashMap<String, Completer> completerHashMapByName = new HashMap<>();

    private static Completer noneCompleter= new Completer() {
        @Override
        public String getName() {
            return this.toString();
        }

        @Override
        public Set<String> complete(CommandSender sender, String command, String[] args) {
            return Collections.emptySet();
        }
    };

    @Override
    public void putCompleter(Completer completer) {
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
        return completerHashMapByClass.getOrDefault(clazz,noneCompleter);
    }


}
