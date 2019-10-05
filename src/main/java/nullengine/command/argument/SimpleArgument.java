package nullengine.command.argument;

import java.util.Objects;

public abstract class SimpleArgument extends Argument {

    private String argumentName;

    private Class responsibleClass;

    public SimpleArgument(Class responsibleClass, String argumentName) {
        this.argumentName = argumentName;
        this.responsibleClass = responsibleClass;
    }

    @Override
    public String getName() {
        return argumentName;
    }

    @Override
    public Class responsibleClass() {
        return responsibleClass;
    }

    @Override
    public String toString() {
        return "SimpleArgument{" +
                "argumentName='" + argumentName + '\'' +
                ", responsibleClass=" + responsibleClass +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleArgument that = (SimpleArgument) o;
        return Objects.equals(argumentName, that.argumentName) &&
                Objects.equals(responsibleClass, that.responsibleClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(argumentName, responsibleClass);
    }
}
