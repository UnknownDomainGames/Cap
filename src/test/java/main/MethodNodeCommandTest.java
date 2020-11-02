package main;

import engine.command.BaseCommandManager;
import engine.command.CommandException;
import engine.command.CommandSender;
import engine.command.anno.*;
import engine.command.argument.Argument;
import engine.command.argument.ArgumentManager;
import engine.command.argument.SimpleArgumentManager;
import engine.command.impl.SimpleCommandManager;
import engine.command.suggestion.NamedSuggester;
import engine.command.suggestion.SimpleSuggesterManager;
import engine.command.suggestion.Suggester;
import engine.command.suggestion.SuggesterManager;
import engine.command.util.SuggesterHelper;
import engine.permission.HashPermissible;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;


public class MethodNodeCommandTest {

    private TestSender testSender = new TestSender("methodNodeTest", string -> message = string, commandException -> message = commandException.getType().name());

    public String message;

    @Test
    public void argumentTest() {

        SimpleCommandManager simpleCommandManager = new SimpleCommandManager();
        ArgumentManager argumentManager = new SimpleArgumentManager();
        argumentManager.appendArgument(new Argument() {
            @Override
            public String getName() {
                return "testArgument";
            }

            @Override
            public Class responsibleClass() {
                return String.class;
            }

            @Override
            public Optional parse(String arg) {
                return Optional.of("test" + arg);
            }

            @Override
            public Suggester getSuggester() {
                return ((sender, command, args) -> Collections.EMPTY_LIST);
            }
        });

        argumentManager.setClassDefaultArgument(new Argument() {
            @Override
            public String getName() {
                return "random";
            }

            @Override
            public Class responsibleClass() {
                return Random.class;
            }

            @Override
            public Optional parse(String arg) {
                return Optional.of(new Random(Integer.valueOf(arg)));
            }

            @Override
            public Suggester getSuggester() {
                return (sender, command, args) -> Collections.EMPTY_LIST;
            }
        });

        MethodAnnotationCommand.getBuilder(simpleCommandManager)
                .setArgumentManager(argumentManager)
                .addCommandHandler(new ArgumentTestClass())
                .register();

        int randomSeed = 12314;

        simpleCommandManager.execute(testSender, "argument " + randomSeed + " argument");

        Random random = new Random(randomSeed);
        Assertions.assertEquals(message, random.nextInt() + "testargument");
    }

    public class ArgumentTestClass {
        @Command("argument")
        public void argument(Random random, @ArgumentHandler("testArgument") String argumentMessage) {
            message = Integer.valueOf(random.nextInt()).toString() + argumentMessage;
        }
    }

    @Test
    void suggestTest() {

        SimpleCommandManager simpleCommandManager = new SimpleCommandManager();
        SuggesterManager suggesterManager = new SimpleSuggesterManager();
        suggesterManager.putSuggester(new NamedSuggester() {
            @Override
            public String getName() {
                return "suggestTest";
            }

            @Override
            public List<String> suggest(CommandSender sender, String command, String[] args) {
                return List.of("test");
            }
        });

        MethodAnnotationCommand.getBuilder(simpleCommandManager)
                .addCommandHandler(new SuggesterTestClass())
                .setSuggesterManager(suggesterManager)
                .register();

        List<String> completeResult = simpleCommandManager.complete(testSender, "suggest ");
        Assertions.assertEquals(1, completeResult.size());
        Assertions.assertEquals("test", completeResult.get(0));
    }

    public class SuggesterTestClass {
        @Command("suggest")
        public void suggest(@engine.command.anno.Suggester("suggestTest") String a) {
        }
    }

    @Test
    void provide() {
        Entity testEntity = new Entity() {
            HashPermissible permissible = new HashPermissible();

            @Override
            public World getWorld() {
                return new World("EntityWorld");
            }

            @Override
            public void sendMessage(String message) {
            }

            @Override
            public String getSenderName() {
                return "entity";
            }

            @Override
            public void sendCommandException(CommandException exception) {
                System.out.println(exception.toString());
            }

            @Override
            public boolean hasPermission(String permission) {
                return permissible.hasPermission(permission);
            }

            @Override
            public void setPermission(String permission, boolean bool) {
                permissible.setPermission(permission, bool);
            }

            @Override
            public void removePermission(String permission) {
                permissible.removePermission(permission);
            }

            @Override
            public void clearPermission() {
                permissible.clearPermission();
            }

            @Override
            public Map<String, Boolean> toPermissionMap() {
                return permissible.toPermissionMap();
            }
        };

        testEntity.setPermission("player.admin", true);

        BaseCommandManager commandManager = new SimpleCommandManager();
        ArgumentManager argumentManager = new SimpleArgumentManager();
        argumentManager.setClassDefaultArgument(new WorldArgument());

        NodeAnnotationCommand.METHOD.getBuilder(commandManager)
                .setArgumentManager(argumentManager)
                .addProvider(new LocationProvider())
                .addCommandHandler(new ProvideTest())
                .register();


        World commandWorld = new World("commandWorld");

        commandManager.execute(testEntity, "location 11 1 2 3 \"hello world\" commandWorld 4 5 6");
        Assertions.assertEquals(message, 11 + new Location(testEntity.getWorld(), 1, 2, 3).toString() + "hello world" + new Location(commandWorld, 4, 5, 6).toString());
        commandManager.execute(testEntity, "location 12 commandWorld 1 2 3 \"hello world\" 4 5 6");
        Assertions.assertEquals(message, 12 + new Location(commandWorld, 1, 2, 3).toString() + "hello world" + new Location(testEntity.getWorld(), 4, 5, 6).toString());
        commandManager.execute(testEntity, "location 13 commandWorld 1 2 3 \"hello world\" commandWorld 4 5 6");
        Assertions.assertEquals(message, 13 + new Location(commandWorld, 1, 2, 3).toString() + "hello world" + new Location(commandWorld, 4, 5, 6).toString());
        commandManager.execute(testEntity,"aaa bbb ccc");
    }

    public class ProvideTest {
        @Command("location")
        @Permission("player.admin | player.location & player.teleport")
        public void location(int i, Location location, String b, Location location2) {
            message = i + location.toString() + b + location2.toString();
        }

        @Command("location")
        public void location(int i, Location location) {
            message = i + location.toString();
        }

    }

    @Test
    void commandTest1() {
        BaseCommandManager commandManager = new SimpleCommandManager();

        ArgumentManager argumentManager = new SimpleArgumentManager();
        argumentManager.setClassDefaultArgument(new WorldArgument());

        NodeAnnotationCommand.METHOD.getBuilder(commandManager)
                .setArgumentManager(argumentManager)
                .addCommandHandler(new CommandTest())
                .register();
    }

    public class CommandTest {

        @Command("command")
        public void command2(@Sender CommandSender sender, @Required("a") String s) {
            message = sender.getSenderName() + "a";
        }

        @Command("command")
        public void command1(@Sender CommandSender sender, @Required("a") String a, String s) {
            message = sender.getSenderName() + "a" + s;
        }

        @Command("command")
        public void command1(@Sender TestSender sender) {
            message = sender.getSenderName();
        }

    }

    private HashMap<String, Double> bank = new HashMap<>();

    @Test
    void moneyTest() {

        HashMap<String, TestSender> entityHashMap = new HashMap<>();

        entityHashMap.put("asd", new TestSender("asd", null, null));
        entityHashMap.put("123", new TestSender("123", null, null));
        entityHashMap.put("zxc", new TestSender("zxc", null, null));

        BaseCommandManager commandManager = new SimpleCommandManager();
        ArgumentManager argumentManager = new SimpleArgumentManager();
        argumentManager.setClassDefaultArgument(new Argument() {
            @Override
            public String getName() {
                return "TestSender";
            }

            @Override
            public Class responsibleClass() {
                return TestSender.class;
            }

            @Override
            public Optional parse(String arg) {
                return Optional.ofNullable(entityHashMap.get(arg));
            }

            @Override
            public String toString() {
                return "class:" + getClass().getName();
            }

            @Override
            public Suggester getSuggester() {
                return null;
            }
        });

        NodeAnnotationCommand.METHOD.getBuilder(commandManager)
                .setArgumentManager(argumentManager)
                .addCommandHandler(new moneyTest())
                .register();

        commandManager.execute(testSender, "money");
        Assertions.assertEquals(message, "0.0");

        commandManager.execute(testSender, "money asd");
        Assertions.assertEquals(message, "0.0");

        commandManager.execute(testSender, "money set 100");
        commandManager.execute(testSender, "money");
        Assertions.assertEquals(message, "100.0");
    }

    public class moneyTest {

        @Command("money")
        public void money(@Sender CommandSender sender) {
            sender.sendMessage(bank.getOrDefault(sender.getSenderName(), 0d) + "");
        }

        @Command("money")
        public void money(@Sender CommandSender sender, TestSender who) {
            sender.sendMessage(bank.getOrDefault(who.getSenderName(), 0d) + "");
        }

        @Command("money")
        public void setMoney(@Sender CommandSender sender, @Required("set") String s, double many) {
            bank.put(sender.getSenderName(), many);
        }

        @Command("money")
        public void setMoney(@Sender CommandSender sender, @Required("set") String s, TestSender playerEntity, double many) {
            bank.put(playerEntity.getSenderName(), many);
        }

    }


    @Test
    void test2() {

        HashMap<String, TestSender> entityHashMap = new HashMap<>();

        entityHashMap.put("asd", new TestSender("asd", null, null));
        entityHashMap.put("123", new TestSender("123", null, null));
        entityHashMap.put("zxc", new TestSender("zxc", null, null));

        BaseCommandManager commandManager = new SimpleCommandManager();
        ArgumentManager argumentManager = new SimpleArgumentManager();
        argumentManager.setClassDefaultArgument(new Argument() {
            @Override
            public String getName() {
                return "TestSender";
            }

            @Override
            public Class responsibleClass() {
                return TestSender.class;
            }

            @Override
            public Optional parse(String arg) {
                return Optional.ofNullable(entityHashMap.get(arg));
            }

            @Override
            public String toString() {
                return "class:" + getClass().getName();
            }

            @Override
            public Suggester getSuggester() {
                return (sender, command, args) -> SuggesterHelper.filterStartWith(new ArrayList<>(entityHashMap.keySet()), args[args.length - 1]);
            }
        });

        NodeAnnotationCommand.METHOD.getBuilder(commandManager)
                .setArgumentManager(argumentManager)
                .addCommandHandler(new test2())
                .register();

        commandManager.execute(testSender, "test asd");
        Assertions.assertEquals(message, "asd");
        commandManager.execute(testSender, "test asd 123");
        Assertions.assertEquals(message, "123");
        commandManager.execute(testSender, "test 100");
        Assertions.assertEquals(message, "100.0");

        Assertions.assertEquals(commandManager.complete(testSender, "test asd ").toString(), "[123, asd, zxc]");
    }

    public class test2 {
        @Command("test")
        public void testCommand1(@Sender TestSender sender, TestSender testSender) {
            message = testSender.getSenderName();
        }

        @Command("test")
        public void testCommand2(TestSender sender, TestSender testSender) {
            message = testSender.getSenderName();
        }

        @Command("test")
        public void testCommand2(@Sender TestSender sender, @Sender TestSender sender2, double s) {
            message = Double.toString(s);
        }


    }


}
