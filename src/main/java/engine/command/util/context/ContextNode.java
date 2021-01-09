package engine.command.util.context;

import engine.command.util.node.CommandNode;

public interface ContextNode {

    Object getValue();

    void setValue(Object value);

    CommandNode getOwner();

    void setNext(ContextNode node);

    void setPre(ContextNode node);

    ContextNode getNext();

    ContextNode getPre();

    ContextNode clone();

}
