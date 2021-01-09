package engine.command;

public interface CommandExceptionHandler {
    void handleOnExecuting(Exception e);

    void handleOnSuggesting(Exception e);

    void handleOnGettingTips(Exception e);

    void handleOnCheckingArgument(Exception e);
}
