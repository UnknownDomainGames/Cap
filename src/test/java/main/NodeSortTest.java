package main;

import engine.command.CommandSender;
import engine.command.argument.base.IntegerArgument;
import engine.command.argument.base.StringArgument;
import engine.command.util.node.*;
import org.junit.jupiter.api.Test;

public class NodeSortTest {

    @Test
    void sortTest() {

        EmptyArgumentNode emptyArgumentNode = new EmptyArgumentNode();

        ArgumentNode integerArgumentNode = new ArgumentNode(new IntegerArgument());
        ArgumentNode argumentNode = new ArgumentNode(new StringArgument());
        EnumNode enumNode = new EnumNode(TestEnum.class);
        MultiArgumentNode multiArgumentNode = new MultiArgumentNode(new ArgumentNode(new IntegerArgument()),null,1);
        MultiArgumentNode multiArgumentNode2 = new MultiArgumentNode(new RequiredNode("abc"),null,1);
        RequiredNode requiredNode = new RequiredNode("123");
        SenderNode senderNode = new SenderNode(CommandSender.class);

        emptyArgumentNode.addChild(argumentNode);
        emptyArgumentNode.addChild(enumNode);
        emptyArgumentNode.addChild(multiArgumentNode);
        emptyArgumentNode.addChild(requiredNode);
        emptyArgumentNode.addChild(senderNode);
        emptyArgumentNode.addChild(integerArgumentNode);
        emptyArgumentNode.addChild(multiArgumentNode2);

        for(CommandNode node : emptyArgumentNode.getChildren()){
            System.out.print(node.getClass().getName());
            if(node instanceof ArgumentNode){
                ArgumentNode argumentNode1 = (ArgumentNode) node;
                System.out.print("   "+argumentNode1.getArgument().getName());
            }
            System.out.println();
        }

    }


}
