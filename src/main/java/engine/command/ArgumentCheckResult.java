package engine.command;

public class ArgumentCheckResult {

    private String helpMessage;

    private boolean valid;

    private ArgumentCheckResult(String helpMessage, boolean valid) {
        this.helpMessage = helpMessage;
        this.valid = valid;
    }

    public static ArgumentCheckResult Valid() {
        return new ArgumentCheckResult(null, true);
    }

    public static ArgumentCheckResult Error(String helpMessage) {
        return new ArgumentCheckResult(helpMessage, false);
    }

    public String getHelpMessage() {
        return helpMessage;
    }

    public boolean isValid() {
        return valid;
    }

}