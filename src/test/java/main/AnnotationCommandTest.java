package main;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import unknowndomain.command.CommandResult;
import unknowndomain.command.CommandSender;
import unknowndomain.command.HashCommandManager;
import unknowndomain.command.anno.AnnotationCommand;
import unknowndomain.command.anno.Command;
import unknowndomain.command.anno.Sender;
import unknowndomain.command.argument.Argument;
import unknowndomain.command.argument.MultiArgument;
import unknowndomain.permission.Permissible;
import unknowndomain.permission.hash.HashPermissible;

import java.util.*;

public class AnnotationCommandTest {

    String text = "test";

    boolean testBoolean = false;

    @Test
    public void test() {

        HashCommandManager commandManager = new HashCommandManager();

        commandManager.getArgumentManager().appendArgument(new MultiArgument(data.class,"data") {

            @Override
            public Collection<SupportArguments> getSupportArgumentsOrders() {
                ArrayList<Argument> arguments = new ArrayList<>();
                arguments.add(commandManager.getArgumentManager().getArgument(Integer.class));
                arguments.add(commandManager.getArgumentManager().getArgument(String.class));
                SupportArguments supportArguments = new SupportArguments(arguments,objects -> new data((Integer) objects.get(0),(String)objects.get(1)));
                ArrayList<SupportArguments> list = new ArrayList<>();
                list.add(supportArguments);
                return list;
            }

            @Override
            public List<Argument> recommendInputArguments() {
                return null;
            }
        });

        AnnotationCommand.as(commandManager.getArgumentManager(), this).forEach(command -> commandManager.registerCommand(command));

        CommandSender sender = new CommandSender() {
            @Override
            public void sendMessage(String message) {
            }

            @Override
            public String getSenderName() {
                return "test";
            }

            @Override
            public Permissible getPermissible() {
                return new HashPermissible();
            }
        };

        CommandResult result1 = commandManager.executeCommand(sender, "changeText", text);

        Assert.assertTrue(result1.isSuccess());

        CommandResult result2 = commandManager.executeCommand(sender, "changeTextWithSender", text);

        Assert.assertTrue(result2.isSuccess());
        Assert.assertEquals(text, sender.getSenderName());

        CommandResult result3 = commandManager.executeCommand(sender, "returnTest");

        Assert.assertTrue(!result3.isSuccess());

        testBoolean = true;

        CommandResult result4 = commandManager.executeCommand(sender, "returnTest");

        Assert.assertTrue(result4.isSuccess());

        CommandResult result5 = commandManager.executeCommand(sender, "returnResult");

        Assert.assertEquals(sender.getSenderName() + new Boolean(testBoolean), result5.getMessage());
        Assert.assertTrue(result5.isSuccess());

        testBoolean = false;

        CommandResult result6 = commandManager.executeCommand(sender, "returnResult");

        Assert.assertEquals(sender.getSenderName() + new Boolean(testBoolean), result6.getMessage());
        Assert.assertTrue(!result6.isSuccess());


        CommandResult result7 = commandManager.executeCommand(sender, "data","1","test");

        Assert.assertEquals("test1", result7.getMessage());
    }

    @Command("changeText")
    public void textChangeWithOutSender(String text) {
        Assert.assertEquals(text, text);
    }

    @Command("changeTextWithSender")
    public void textChange(@Sender CommandSender sender, String text) {
        Assert.assertEquals(text, text);
        text = sender.getSenderName();
    }

    @Command("returnTest")
    public boolean returnCommand() {
        return testBoolean;
    }

    @Command("returnResult")
    public CommandResult resultCommand(@Sender CommandSender sender) {
        return new CommandResult(testBoolean, sender.getSenderName() + new Boolean(testBoolean));
    }

    @Command("data")
    public CommandResult resultCommand(data data) {
        return new CommandResult(true, data.s+data.i);
    }


    private class data {

        int i;
        String s;

        public data(int i, String s) {
            this.i = i;
            this.s = s;
        }
    }

}
