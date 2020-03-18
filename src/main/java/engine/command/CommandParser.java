package engine.command;

public interface CommandParser {

    Result parse(String command);

    Result parse(String commandName, String[] args);

    final class Result {
        private final String raw;
        private final String name;
        private final String[] args;

        public Result(String raw, String name, String[] args) {
            this.raw = raw;
            this.name = name;
            this.args = args;
        }

        public String getRaw() {
            return raw;
        }

        public String getName() {
            return name;
        }

        public String[] getArgs() {
            return args;
        }
    }
}
