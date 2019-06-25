package main;

import org.junit.Assert;
import org.junit.Test;
import unknowndomain.command.CommandResult;
import unknowndomain.command.CommandSender;
import unknowndomain.command.HashCommandManager;
import unknowndomain.command.anno.AnnotationCommand;
import unknowndomain.command.anno.Command;
import unknowndomain.command.anno.Sender;
import unknowndomain.command.argument.Argument;
import unknowndomain.command.argument.MultiArgument;
import unknowndomain.command.argument.SimpleArgumentManager;
import unknowndomain.command.completion.CompleteManager;
import unknowndomain.command.completion.SimpleCompleteManager;
import unknowndomain.permission.HashPermissible;
import unknowndomain.permission.Permissible;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AnnotationCommandTest {

    @Test
    public void test() {

        HashCommandManager commandManager = new HashCommandManager();

        SimpleArgumentManager simpleArgumentManager = new SimpleArgumentManager();

        CompleteManager completeManager = new SimpleCompleteManager();

        CommandSender sender = new CommandSender() {
            HashPermissible permissible = new HashPermissible();
            @Override
            public void sendMessage(String message) { }
            @Override
            public String getSenderName() {
                return "test";
            }

            @Override
            public Permissible getPermissible() {
                return permissible;
            }
        };

        simpleArgumentManager.appendArgument(new MultiArgument(Location.class, "location") {
            @Override
            public Collection<SupportArguments> getSupportArgumentsOrders() {
                ArrayList list = new ArrayList();
                list.add(simpleArgumentManager.getArgument(Integer.class));
                list.add(simpleArgumentManager.getArgument(Integer.class));
                list.add(simpleArgumentManager.getArgument(Integer.class));

                SupportArguments supportArguments = new SupportArguments(list, l -> new Location((Integer) l.get(0), (Integer) l.get(1), (Integer) l.get(2)));

                ArrayList supportList = new ArrayList();
                supportList.add(supportArguments);
                return supportList;
            }

            @Override
            public List<Argument> recommendArguments() {
                ArrayList list = new ArrayList();
                list.add(simpleArgumentManager.getArgument(Integer.class));
                list.add(simpleArgumentManager.getArgument(Integer.class));
                list.add(simpleArgumentManager.getArgument(Integer.class));
                return list;
            }
        });

        List<unknowndomain.command.Command> commands = AnnotationCommand.as(commandManager, simpleArgumentManager,completeManager, this);
        for (unknowndomain.command.Command command : commands)
            if (!commandManager.hasCommand(command.name))
                commandManager.registerCommand(command);

        CommandResult result = commandManager.executeCommand(sender, "say", "hamburger");
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(sender.getSenderName()+": "+"hamburger",result.getMessage());

        CommandResult result1 = commandManager.executeCommand(sender, "say", "hamburger", "is delicious");
        Assert.assertTrue(result1.isSuccess());
        Assert.assertEquals(result1.getMessage(),sender.getSenderName()+": "+"hamburger is delicious");

        CommandResult result2 = commandManager.executeCommand(sender, "say", "1","2","3");
        Assert.assertTrue(result2.isSuccess());
        Assert.assertEquals(result2.getMessage(),new Location(1,2,3).toString());


    }


    @Command("say")
    public CommandResult say(String text) {
        return new CommandResult(true,text);
    }

    @Command("say")
    public CommandResult say(String text, @Sender CommandSender sender, String text2) {
        return new CommandResult(true,sender.getSenderName() + ": " + text +" "+ text2);
    }

    @Command("say")
    public CommandResult say(Location location){
        return new CommandResult(true,location.toString());
    }

    @Command("say")
    public CommandResult say(@Sender CommandSender sender,String text){
        return new CommandResult(true,sender.getSenderName()+": "+text);
    }

    @Command("say")
    public CommandResult say(@Sender CommandSender sender,String text,String text2){
        return new CommandResult(true,sender.getSenderName()+": "+text+" "+text2);
    }


    class Location {

        int x;
        int y;
        int z;

        public Location(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public String toString() {
            return "Location{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '}';
        }
    }


}
