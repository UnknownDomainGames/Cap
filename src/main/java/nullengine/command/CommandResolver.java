package nullengine.command;

@FunctionalInterface
public interface CommandResolver {

    Result resolve(String rawCommand);

    final class Result {
        public final String command;
        public final String[] args;

        public Result(String command, String[] args) {
            this.command = command;
            this.args = args;
        }
    }
}
