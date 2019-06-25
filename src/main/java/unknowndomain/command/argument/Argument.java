package unknowndomain.command.argument;

import java.util.List;

public interface Argument<T> {

    String getName();

    Class<T> responsibleClass();

    /**
     *
     * parse args to object.
     * method need's args must be first in array.
     * method will not pay attention to args of after it need args
     *
     * @param args
     * @return parseResult
     */
    ParseResult<T> parseArgs(String[] args);

    String getInputHelp();
}
