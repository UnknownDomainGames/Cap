package unknowndomain.command.argument;

public class IntegerArgument extends SingleArgument {

    public IntegerArgument() {
        super(Integer.class,"Integer");
    }

    @Override
    public ParseResult<Integer> parseArgs(String[] args) {
        return new ParseResult(Integer.valueOf(args[0]),1,false);
    }

    @Override
    public String getInputHelp() {
        return "num:";
    }
}
