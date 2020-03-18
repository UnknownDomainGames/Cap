package engine.command.suggestion;

public interface SuggesterManager {

    void putSuggester(NamedSuggester completer);

    void setClassSuggester(Class clazz, Suggester suggester);

    Suggester getSuggester(String name);

    Suggester getSuggester(Class clazz);

}
