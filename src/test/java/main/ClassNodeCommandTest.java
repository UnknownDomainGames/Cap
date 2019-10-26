package main;

import nullengine.command.Command;
import nullengine.command.CommandSender;
import nullengine.command.SimpleCommandManager;
import nullengine.command.anno.ClassAnnotationCommand;
import nullengine.command.anno.Sender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClassNodeCommandTest {

    SimpleCommandManager simpleCommandManager = new SimpleCommandManager();
    private TestSender testSender = new TestSender("methodNodeTest", string -> message = string);

    private String message;

    @Test
    void commandAttribute() {
        ClassAnnotationCommand.getBuilder(simpleCommandManager)
                .caseCommand("attribute","test","help",()->{})
                .register();
        Command command = simpleCommandManager.getCommand("attribute").get();
        Assertions.assertEquals("test",command.getDescription());
        Assertions.assertEquals("help",command.getHelpMessage());
    }

    @Test
    void use() {
        ClassAnnotationCommand.getBuilder(simpleCommandManager)
                .caseCommand("use", new Runnable() {
                    @Sender
                    public CommandSender sender;

                    @Override
                    public void run() {
                        sender.sendMessage(sender.getSenderName());
                    }
                })
                .register();
        simpleCommandManager.execute(testSender,"use");
        Assertions.assertEquals(message,testSender.getSenderName());
    }


}
