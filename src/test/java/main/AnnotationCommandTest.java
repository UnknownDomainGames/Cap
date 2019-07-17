package main;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;
import unknowndomain.command.CommandResult;
import unknowndomain.command.CommandSender;
import unknowndomain.command.HashCommandManager;
import unknowndomain.command.anno.*;
import unknowndomain.command.anno.node.ArgumentNode;
import unknowndomain.command.anno.node.CommandNode;
import unknowndomain.command.anno.node.SenderNode;
import unknowndomain.command.argument.Argument;
import unknowndomain.command.argument.SimpleArgumentManager;
import unknowndomain.command.argument.SingleArgument;
import unknowndomain.command.argument.base.StringArgument;
import unknowndomain.command.completion.Completer;
import unknowndomain.command.exception.CommandSenderErrorException;
import unknowndomain.permission.HashPermissible;
import unknowndomain.permission.Permissible;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class AnnotationCommandTest {

    CommandSender sender = new TestSender();


    @Test
    public void senderTest() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException, InstantiationException {

        Constructor constructor = AnnotationCommand.class.getDeclaredConstructor(String.class, String.class, String.class);
        constructor.setAccessible(true);

        Field field = AnnotationCommand.class.getDeclaredField("annotationNode");

        field.setAccessible(true);

        AnnotationCommand annotationCommand = (AnnotationCommand) constructor.newInstance("test", "description", "helpMessage");

        CommandNode commandNode = (CommandNode) field.get(annotationCommand);

        CommandNode senderNode = new SenderNode(TestSender.class);
        CommandNode stringNode = new ArgumentNode(new StringArgument());
        senderNode.addChild(stringNode);

        stringNode.setInstance(this);
        stringNode.setMethod(getClass().getMethod("senderTest", CommandSender.class, String.class));

        commandNode.addChild(senderNode);

        CommandResult result = annotationCommand.execute(new TestSender2(), new String[]{"abc"});
        CommandResult result1 = annotationCommand.execute(sender, new String[]{"abc"});

        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(result1.isSuccess());

        Assert.assertEquals(result1.getMessage(), sender.getSenderName() + " --- " + "abc");
        Assert.assertEquals(result.getCause().getClass(), CommandSenderErrorException.class);
    }

    public CommandResult senderTest(CommandSender sender, String text) {
        return new CommandResult(true, sender.getSenderName() + " --- " + text);
    }

    @Test
    public void buildTest() {
        HashCommandManager commandManager = new HashCommandManager();

        AnnotationCommand
                .getBuilder(commandManager)
                .setArgumentManager(new SimpleArgumentManager())
                .addCommandHandler(this)
                .register();

        CommandResult result = commandManager.executeCommand(sender, "build", "block");

        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(result.getMessage(), "block");

        CommandResult result1 = commandManager.executeCommand(sender, "build2", "1");

        Assert.assertTrue(result1.isSuccess());
        Assert.assertEquals(result1.getMessage(), "12");

        CommandResult result2 = commandManager.executeCommand(sender, "build3", "test", "1");
        Assert.assertTrue(result2.isSuccess());
        Assert.assertEquals(result2.getMessage(), "12");

        CommandResult result3 = commandManager.executeCommand(sender, "build3", "tes1t", "1");
        Assert.assertFalse(result3.isSuccess());

    }

    @Command("build")
    public CommandResult buildTest(String text) {
        return new CommandResult(true, text);
    }


    @Command("build2")
    public CommandResult buildTest(@Sender CommandSender sender, Integer text) {
        return new CommandResult(true, text + "2");
    }

    @Command("build3")
    public CommandResult buildTest(@Sender CommandSender sender, @Required("test") String a, Integer text) {
        Assert.assertEquals(a, "test");
        return new CommandResult(true, text + "2");
    }


    @Test
    public void multiArgumentTest() {

        HashCommandManager commandManager = new HashCommandManager();

        AnnotationCommand
                .getBuilder(commandManager)
                .setArgumentManager(new SimpleArgumentManager())
                .addCommandHandler(new MultiClass())
                .register();


        CommandResult result = commandManager.executeCommand(sender, "multi", "1", "1", "1");
        Assert.assertEquals(result.getMessage(), new Location(1, 1, 1).toString());

        CommandResult result1 = commandManager.executeCommand(sender, "multi1", "1", "1", "1", "abc");
        Assert.assertEquals(result1.getMessage(), new Location(1, 1, 1).toString() + sender.getSenderName() + "abc");

    }

    public class MultiClass {
        @Command("multi")
        public CommandResult multiTest(Location location) {
            return new CommandResult(true, location.toString());
        }

        @Command("multi1")
        public CommandResult multiTest(Location location, @Sender CommandSender sender, String text) {
            return new CommandResult(true, location.toString() + sender.getSenderName() + text);
        }

    }


    @Test
    public void permissionTest() {

        HashCommandManager commandManager = new HashCommandManager();

        AnnotationCommand
                .getBuilder(commandManager)
                .setArgumentManager(new SimpleArgumentManager())
                .addCommandHandler(this)
                .register();

        CommandResult result = commandManager.executeCommand(sender, "permission");
        Assert.assertTrue(result.isSuccess());

        CommandResult result1 = commandManager.executeCommand(new TestSender2(), "permission");
        Assert.assertFalse(result1.isSuccess());

        CommandResult result2 = commandManager.executeCommand(sender, "permission1", "");
        Assert.assertTrue(result2.isSuccess());

        CommandResult result3 = commandManager.executeCommand(new TestSender2(), "permission1", "");
        Assert.assertFalse(result3.isSuccess());
    }

    @Command("permission")
    @Permission({"abc", "123"})
    public void permission() {
    }

    @Permission({"abc", "123"})
    @Command("permission1")
    public void permission(String text) {
    }

    @Test
    public void argumentHandlerTest() throws NoSuchFieldException, IllegalAccessException {
        HashCommandManager commandManager = new HashCommandManager();

        SimpleArgumentManager simpleArgumentManager = new SimpleArgumentManager();
        simpleArgumentManager.appendArgument(new Argument() {
            @Override
            public String getName() {
                return "TestString";
            }

            @Override
            public Class responsibleClass() {
                return String.class;
            }

            @Override
            public Optional parse(String arg) {
                return Optional.of(arg + "1");
            }

            @Override
            public Completer getCompleter() {
                return null;
            }
        });

        AnnotationCommand
                .getBuilder(commandManager)
                .setArgumentManager(simpleArgumentManager)
                .addCommandHandler(new ArgumentHandlerCommandHandler())
                .register();

        CommandResult result = commandManager.executeCommand(sender, "argument", "test");
        Assert.assertEquals(result.getMessage(), "test1");
    }

    public class ArgumentHandlerCommandHandler {

        @Command("argument")
        public CommandResult argument(@ArgumentHandler("TestString") String s) {
            return new CommandResult(true, s);
        }


    }

    public class Location {

        int x;
        int y;
        int z;

        @Generator
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

    private class TestSender implements CommandSender {
        private HashPermissible hashPermissible = new HashPermissible();

        public TestSender() {
            hashPermissible.setPermission("abc", true);
            hashPermissible.setPermission("123", true);
        }

        @Override
        public void sendMessage(String message) {

        }

        @Override
        public String getSenderName() {
            return "test sender";
        }

        @Override
        public Permissible getPermissible() {
            return hashPermissible;
        }
    }

    private class TestSender2 implements CommandSender {
        private HashPermissible hashPermissible = new HashPermissible();

        @Override
        public void sendMessage(String message) {

        }

        @Override
        public String getSenderName() {
            return "test2 sender";
        }

        @Override
        public Permissible getPermissible() {
            return hashPermissible;
        }
    }

    @Test
    public void completeTest(){
        HashCommandManager commandManager = new HashCommandManager();

        SimpleArgumentManager argumentManager = new SimpleArgumentManager();

        argumentManager.appendArgument(new SingleArgument(String.class,"test") {
            @Override
            public Optional parse(String arg) {
                return Optional.of(arg);
            }

            @Override
            public Completer getCompleter() {
                return (sender, command, args) -> {
                    String s = args[args.length - 1];
                    if (s.isEmpty())
                        return Sets.newHashSet("[test]");
                    else return Collections.emptySet();
                };
            }
        });

        argumentManager.setClassDefaultArgument(new StringArgument(){
            @Override
            public Completer getCompleter() {
                return (sender, command, args) -> {
                    String s = args[args.length - 1];
                    if (s.isEmpty())
                        return Sets.newHashSet("[test2]");
                    else return Collections.emptySet();
                };
            }
        });

        AnnotationCommand
                .getBuilder(commandManager)
                .setArgumentManager(argumentManager)
                .addCommandHandler(new CompleteTestClass())
                .register();


        Set<String> seta = commandManager.getCompleteList(sender,"a");

        Assert.assertEquals(seta.toArray().length,1);
        Assert.assertEquals(seta.toArray()[0],"acommand");

        Set<String> setb = commandManager.getCompleteList(sender,"b");
        Assert.assertEquals(setb.toArray().length,1);
        Assert.assertEquals(setb.toArray()[0],"bcommand");

        Set<String> completeList = commandManager.getCompleteList(sender,"acommand","");

        String[] completeArray = completeList.toArray(new String[0]);

        Assert.assertEquals(completeArray.length,2);
        Assert.assertEquals(completeArray[0],"test");
        Assert.assertEquals(completeArray[1],"[test2]");

        Set<String> completeList2 = commandManager.getCompleteList(sender,"bcommand","");

        completeArray = completeList2.toArray(new String[0]);

        Assert.assertEquals(completeArray.length,1);
        Assert.assertEquals(completeArray[0],"[test]");
    }

    public class CompleteTestClass{

        @Command("acommand")
        public void command1(String a){

        }

        @Command("bcommand")
        public void command2(@ArgumentHandler("test") String a){

        }

        @Command("acommand")
        public void command3(@Required("test") String a){

        }




    }


}
