package unknowndomain.command.completion;

import unknowndomain.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

public class CompleteManager {

    private static HashMap<Class, Function<CommandSender, List<String>>> functionHashMap = new HashMap<>();

    private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock(false);

    public static List<String> complete(CommandSender sender, Class clazz) {
        lock.readLock().lock();
        try {
            return functionHashMap.getOrDefault(clazz, (sender1) -> new ArrayList<>()).apply(sender);
        } finally {
            lock.readLock().unlock();
        }
    }

    public static void putCompleteFunction(Class clazz, Function<CommandSender, List<String>> function) {
        lock.writeLock().lock();
        functionHashMap.put(clazz, function);
        lock.writeLock().unlock();
    }

}
