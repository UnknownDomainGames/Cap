package engine.command.util.context;

import engine.command.CommandSender;
import engine.command.util.Type;

public interface Context {

    CommandSender getSender();

    int size();

    int first(Type type);

    int last(Type type);

    Type getTypeAt(int index);

    Object getValueAt(int index);

}
