package unknowndomain.command.argument;

public class ParseResult<T> {

    public final T result;

    public final int uesdArgsNum;

    public final boolean fail;

    public ParseResult(T result, int uesdArgsNum, boolean fail) {
        this.result = result;
        this.uesdArgsNum = uesdArgsNum;
        this.fail = fail;
    }

    @Override
    public String toString() {
        return "ParseResult{" +
                "result=" + result +
                ", uesdArgsNum=" + uesdArgsNum +
                ", fail=" + fail +
                '}';
    }
}
