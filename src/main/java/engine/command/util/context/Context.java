package engine.command.util.context;

import engine.command.CommandSender;
import engine.command.util.Type;

public interface Context {

    CommandSender getSender();

    int length();

    int first(Type type);

    int last(Type type);

    Type typeAt(int index);

    Object valueAt(int index);

}
