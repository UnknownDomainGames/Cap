package engine.command.util.context;

import engine.command.util.node.CommandNode;

import java.util.List;

public interface LinkedContext extends Context {

    void add(CommandNode handler, Object object);

    void add(CommandNode handler, int index, Object object);

    void remove(int index);

    void removeLast();

    List<Object> valueToArray();

    LinkedContext clone();
}
