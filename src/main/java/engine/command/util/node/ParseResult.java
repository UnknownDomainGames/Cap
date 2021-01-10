package engine.command.util.node;

public class ParseResult {

    private static final ParseResult EMPTY_SUCCESS = success(null);
    private static final ParseResult FAIL = new ParseResult(false,null);

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
        return EMPTY_SUCCESS;
    }

    public static ParseResult fail() {
        return FAIL;
    }
}
