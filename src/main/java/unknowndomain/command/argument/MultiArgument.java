package unknowndomain.command.argument;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class MultiArgument implements Argument {

    private String argumentName;

    private Class responsibleClass;

    public MultiArgument(Class responsibleClass, String argumentName) {
        this.argumentName = argumentName;
        this.responsibleClass = responsibleClass;
    }

    @Override
    public final String getName() {
        return argumentName;
    }

    @Override
    public final Class responsibleClass() {
        return responsibleClass;
    }

    @Override
    public ParseResult parseArgs(String[] args) {
        ParseResult result = new ParseResult(null, 0, true);
        Collection<SupportArguments> supportArguments = getSupportArgumentsOrders();

        for (SupportArguments entry : supportArguments) {
            List<? extends Argument> argumentList = entry.arguments;

            if (argumentList.size() > args.length)
                continue;

            List<ParseResult> parseResults = tryParse(argumentList, args);

            //TODO fail message support
            if (hasAnyFail(parseResults))
                continue;

            int usedCount = parseResults.stream().flatMapToInt(parseResult -> IntStream.of(parseResult.uesdArgsNum)).sum();
            result = new ParseResult(entry.instanceFunction
                    .apply(parseResults.stream().map(parseResult -> parseResult.result).collect(Collectors.toList()))
                    , usedCount, false);
            break;
        }

        return result;
    }

    private List<ParseResult> tryParse(List<? extends Argument> arguments, String[] args) {
        ArrayList<ParseResult> result = new ArrayList();
        int index = 0;
        for (int i = 0; i < arguments.size(); i++) {
            ParseResult parseResult = arguments.get(i).parseArgs(Arrays.copyOfRange(args, index, args.length));
            result.add(parseResult);
            index += parseResult.uesdArgsNum;
        }
        return result;
    }

    private static boolean hasAnyFail(List<ParseResult> parseResults) {
        for (ParseResult parseResult : parseResults) {
            if (parseResult.fail)
                return true;
        }
        return false;
    }

    @Override
    public String getInputHelp() {
        return recommendArguments().stream().map(Argument::getInputHelp).reduce((s, s2) -> s + " " + s2).get();
    }

    public abstract Collection<SupportArguments> getSupportArgumentsOrders();

    public abstract List<Argument> recommendArguments();

    public class SupportArguments {

        public final List<? extends Argument> arguments;

        public final Function<List<? extends Object>, Object> instanceFunction;

        public SupportArguments(List<? extends Argument> arguments, Function<List<? extends Object>, Object> instanceFunction) {
            this.arguments = arguments;
            this.instanceFunction = instanceFunction;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultiArgument that = (MultiArgument) o;
        return Objects.equals(argumentName, that.argumentName) &&
                Objects.equals(responsibleClass, that.responsibleClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(argumentName, responsibleClass);
    }
}
