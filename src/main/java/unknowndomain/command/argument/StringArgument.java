package unknowndomain.command.argument;

public class StringArgument extends SingleArgument {

    public StringArgument() {
        super(String.class, "String");
    }

    @Override
    public ParseResult<String> parseArgs(String[] args) {
        return new ParseResult(args[0],1,false);
    }

    @Override
    public String getInputHelp() {
        return "text:";
    }
}
