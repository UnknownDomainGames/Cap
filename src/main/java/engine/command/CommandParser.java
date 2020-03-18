package engine.command;

public interface CommandParser {

    Result parse(String command);

    final class Result {
        private final String name;
        private final String[] args;

        public Result(String name, String[] args) {
            this.name = name;
            this.args = args;
        }

        public String getName() {
            return name;
        }

        public String[] getArgs() {
            return args;
        }
    }
}
