package main;

import nullengine.command.argument.base.BooleanArgument;
import nullengine.command.argument.base.StringArgument;
import nullengine.command.util.node.ArgumentNode;

public class Test {

    @org.junit.jupiter.api.Test
    public void test() throws CloneNotSupportedException {

        ArgumentNode parent = new ArgumentNode(new BooleanArgument());
        ArgumentNode argumentNode = new ArgumentNode(new StringArgument());

        parent.addChild(argumentNode);

        ArgumentNode cloneNode = (ArgumentNode) parent.clone();

        System.out.println(parent.getChildren().size());
        System.out.println(cloneNode.getChildren().size());

        System.out.println(parent.getChildren().stream().findAny().get()==cloneNode.getChildren().stream().findAny().get());



    }
}
