package unknowndomain.command.argument;

import java.util.Objects;

public abstract class SingleArgument extends Argument {

    private String argumentName;

    private Class responsibleClass;

    public SingleArgument(Class responsibleClass, String argumentName) {
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
        return "SingleArgument{" +
                "argumentName='" + argumentName + '\'' +
                ", responsibleClass=" + responsibleClass +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingleArgument that = (SingleArgument) o;
        return Objects.equals(argumentName, that.argumentName) &&
                Objects.equals(responsibleClass, that.responsibleClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(argumentName, responsibleClass);
    }
}
