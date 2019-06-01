package unknowndomain.command.argument;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class MultiArgument implements Argument {

    private String argumentName;

    private Class responsibleClass;

    public MultiArgument(Class responsibleClass, String argumentName) {
        this.argumentName = argumentName;
        this.responsibleClass = responsibleClass;
    }

    @Override
    public String getName() {
        return argumentName;
    }

    @Override
    public Class responsibleClass() {
        return responsibleClass;
    }

    @Override
    public ParseResult parseArgs(String[] args) {
        ParseResult result = new ParseResult(null, 1, true);
        Set<Map<List<Argument>, Function<List<Object>, Object>>> supportArguments = getSupportArgumentOrder();
        parseResultLoop:
        for (Map<List<Argument>, Function<List<Object>, Object>> arguments : supportArguments) {
            if (arguments.size() > args.length)
                continue;
            Set<Map.Entry<List<Argument>, Function<List<Object>, Object>>> entrySet = arguments.entrySet();

            entryLoop:
            for (Map.Entry<List<Argument>, Function<List<Object>, Object>> entry : entrySet) {
                List<ParseResult> parseResults = tryParse(entry.getKey(), args);
                for (ParseResult parseResult : parseResults) {
                    if (parseResult.fail)
                        break entryLoop;
                }

                ArrayList<Object> arrayList = new ArrayList<>();

                for(ParseResult parseResult : parseResults){
                    arrayList.add(parseResult.result);
                }

                result = new ParseResult(entry.getValue().apply(arrayList), entry.getKey().size(), false);
                break parseResultLoop;
            }
        }
        return result;
    }

    public List<ParseResult> tryParse(List<Argument> arguments, String[] args) {
        ArrayList<ParseResult> result = new ArrayList();
        int index = 0;
        for (int i = 0; i < arguments.size(); i++) {
            ParseResult parseResult = arguments.get(i).parseArgs(Arrays.copyOfRange(args,index,args.length));
            result.add(parseResult);
            index+=parseResult.uesdArgsNum;
        }
        return result;
    }

    @Override
    public String getInputHelp() {
        return defaultArgument().stream().map(Argument::getInputHelp).reduce((s, s2) -> s + " " + s2).get();
    }

    public abstract Set<Map<List<Argument>, Function<List<Object>, Object>>> getSupportArgumentOrder();

    public abstract List<Argument> defaultArgument();

}
