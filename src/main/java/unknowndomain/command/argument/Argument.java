package unknowndomain.command.argument;

import unknowndomain.command.CommandSender;

import java.util.Objects;
import java.util.function.BiFunction;

public class Argument {

    private String argumentName;

    private String desc;

    private Class handleClass;

    private BiFunction<CommandSender, String, Object> handleFunction;

    public Argument(Class handleClass, BiFunction<CommandSender, String, Object> handleFunction) {
        this(handleClass, handleFunction, handleClass.getName());
    }

    public Argument(Class handleClass, BiFunction<CommandSender, String, Object> handleFunction, String argumentName) {
        this(handleClass, handleFunction, argumentName, argumentName);
    }

    public Argument(Class handleClass, BiFunction<CommandSender, String, Object> handleFunction, String argumentName, String desc) {
        this.argumentName = argumentName;
        this.desc = desc;
        this.handleClass = handleClass;
        this.handleFunction = handleFunction;
    }

    public String getArgumentName() {
        return argumentName;
    }

    public String getDesc() {
        return desc;
    }

    public Class getHandleClass() {
        return handleClass;
    }

    public BiFunction<CommandSender, String, Object> getHandleFunction() {
        return handleFunction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Argument argument = (Argument) o;
        return Objects.equals(argumentName, argument.argumentName) &&
                Objects.equals(desc, argument.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(argumentName, desc);
    }
}
