package nullengine.command.completion;

public interface CompleteManager {

    void putCompleter(NamedCompleter completer);

    void setClassCompleter(Class clazz,Completer completer);

    Completer getCompleter(String name);

    Completer getCompleter(Class clazz);

    /*private static Map<Class, Function<CommandSender, List<String>>> functionHashMap = new HashMap<>();

    private static ReadWriteLock lock = new ReentrantReadWriteLock(false);

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
    }*/

}
