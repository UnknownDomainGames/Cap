package engine.command.util.node;

public class ParseResult {

    private final boolean success;
    private final Object value;

    private ParseResult(boolean success, Object value) {
        this.success = success;
        this.value = value;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFail(){
        return !success;
    }

    public Object getValue() {
        return value;
    }

    public static ParseResult success(Object value) {
        return new ParseResult(true, value);
    }

    public static ParseResult success() {
        return success(null);
    }

    public static ParseResult fail() {
        return new ParseResult(false, null);
    }
}
