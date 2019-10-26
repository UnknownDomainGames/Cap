package nullengine.command;

import java.util.List;
import java.util.Objects;

public abstract class Command {

    private final String name;
    private String description;
    private String helpMessage;

    public Command(String name) {
        this(name, "/" + name);
    }

    public Command(String name, String description) {
        this(name, description, "");
    }

    public Command(String name, String description, String helpMessage) {
        this.name = name;
        this.description = description;
        this.helpMessage = helpMessage;
    }

    public abstract void execute(CommandSender sender, String[] args) throws Exception;

    public abstract List<String> complete(CommandSender sender, String[] args);

    public abstract List<String> getTips(CommandSender sender,String[] args);

    public abstract ArgumentCheckResult checkArguments(CommandSender sender,String[] args);

    public abstract boolean handleUncaughtException(Exception e, CommandSender sender, String[] args);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHelpMessage() {
        return helpMessage;
    }

    public void setHelpMessage(String helpMessage) {
        this.helpMessage = helpMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Command command = (Command) o;
        return Objects.equals(name, command.name) &&
                description.equals(command.description) &&
                helpMessage.equals(command.helpMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, helpMessage);
    }

}
