package unknowndomain.command.argument;

public interface Argument<T> {

    String getName();

    Class<T> responsibleClass();

    /**
     *
     * parse args to object.
     * method can't found it need argument in args.
     * method need's args must be first in array.
     * method will not pay attention to args of after it need args
     *
     * @param args
     * @return
     */
    ParseResult<T> parseArgs(String[] args);

    String getInputHelp();
}
