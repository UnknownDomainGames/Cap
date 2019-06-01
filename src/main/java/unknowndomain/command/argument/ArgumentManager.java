package unknowndomain.command.argument;

public interface ArgumentManager {

    void setClassDefaultArgument(Argument argument);

    void appendArgument(Argument argument);

    void removeArgument(Argument argument);

    void removeArgument(String argumentName);

    Argument getArgument(Class clazz);

    Argument getArgument(String argumentName);
}
