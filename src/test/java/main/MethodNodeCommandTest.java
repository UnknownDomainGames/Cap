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
import engine.command.util.CommandNodeUtil;
import engine.command.util.SuggesterHelper;
import engine.command.util.node.CommandNode;
import engine.permission.HashPermissible;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


public class MethodNodeCommandTest {

    private TestSender testSender = new TestSender("methodNodeTest", string -> message = string, commandException -> message = commandException.getType().name());

    public String message;

    SimpleCommandManager simpleCommandManager = new SimpleCommandManager();

    public MethodNodeCommandTest() {
      /*  MethodAnnotationCommand.getBuilder(simpleCommandManager)
                .addCommandHandler(this)
                .register();*/
    }

    @Test
    public void commandAttributeTest() {
        engine.command.Command command = simpleCommandManager.getCommand("command").get();

        Assertions.assertEquals(command.getDescription(), "desc");
        Assertions.assertEquals(command.getHelpMessage(), "helpMessage");

        simpleCommandManager.execute(testSender, "command");
        Assertions.assertEquals(message, "command");
    }

    @Command(value = "command", desc = "desc", helpMessage = "helpMessage")
    public void command() {
        message = "command";
    }

    @Test
    void permissionTest() {
        testSender.removePermission("permission.use");
        testSender.removePermission("permission");
        testSender.removePermission("op");
        engine.command.Command command = simpleCommandManager.getCommand("permission").get();
        command.execute(testSender, new String[0]);
        Assertions.assertEquals(CommandException.Type.PERMISSION_NOT_ENOUGH.name(), message);
        testSender.setPermission("permission", true);
        Assertions.assertDoesNotThrow(() -> command.execute(testSender, new String[0]));
        testSender.setPermission("permission", false);
        testSender.setPermission("permission.use", true);
        Assertions.assertDoesNotThrow(() -> command.execute(testSender, new String[0]));
        testSender.removePermission("permission.use");
        testSender.removePermission("permission");
        Assertions.assertEquals("p", message);
    }

    @Command(value = "permission")
    @Permission({"permission.use"})
    public void permission() {
        message = "p";
    }

    @Test
    public void senderTest() {
        engine.command.Command command = simpleCommandManager.getCommand("sender").get();
        Sender1 sender1 = new Sender1();
        command.execute(sender1, new String[0]);
        Assertions.assertEquals(sender1.getSenderName(), message);
        Sender2 sender2 = new Sender2();
        command.execute(sender2, new String[0]);
        Assertions.assertEquals(sender2.getSenderName(), message);
        command.execute(testSender, new String[0]);
        Assertions.assertEquals(CommandException.Type.COMMAND_WRONG_USAGE.name(), message);
    }

    @Command("sender")
    public void sender(@Sender({Sender1.class, Sender2.class}) CommandSender sender) {
        message = sender.getSenderName();
    }

    private class Sender1 extends TestSender {

        public Sender1() {
            super("testSender1", s -> {
            }, c -> {
            });
        }
    }

    private class Sender2 implements CommandSender {
        @Override
        public void sendMessage(String message) {
        }

        @Override
        public String getSenderName() {
            return "sender2";
        }

        @Override
        public void sendCommandException(CommandException exception) {

        }

        @Override
        public boolean hasPermission(@Nonnull String permission) {
            return true;
        }

        @Override
        public void setPermission(@Nonnull String permission, boolean bool) {
        }

        @Override
        public void removePermission(String permission) {
        }

        @Override
        public void clearPermission() {

        }

        @Override
        public Map<String, Boolean> toPermissionMap() {
            return null;
        }
    }

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
    public void requiredTest() throws Exception {
        engine.command.Command command = simpleCommandManager.getCommand("required").get();

        command.execute(testSender, new String[]{"c"});
        Assertions.assertEquals(CommandException.Type.COMMAND_WRONG_USAGE.name(), message);

        command.execute(testSender, new String[]{"a"});
        Assertions.assertEquals("a", message);
        command.execute(testSender, new String[]{"b"});
        Assertions.assertEquals("b", message);
    }

    @Command("required")
    public void required1(@Required("a") String a) {
        message = a;
    }

    @Command("required")
    public void required2(@Required("b") String a) {
        message = a;
    }

    @Test
    void tip() {
        List<String> tips = simpleCommandManager.getTips(testSender, "tip ");
        Assertions.assertArrayEquals(tips.toArray(), new String[]{"x", "y", "z"});
        tips = simpleCommandManager.getTips(testSender, "tip 2");
        Assertions.assertArrayEquals(tips.toArray(), new String[]{"x", "y", "z"});
        tips = simpleCommandManager.getTips(testSender, "tip 2 5");
        Assertions.assertArrayEquals(tips.toArray(), new String[]{"y", "z"});
        tips = simpleCommandManager.getTips(testSender, "tip 2 5 6");
        Assertions.assertArrayEquals(tips.toArray(), new String[]{"z"});
    }

    @Command("tip")
    public void tip(@Tip("x") int x, @Tip("y") int y, @Tip("z") int z) {
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
        for (int i = 0; i < 10000; i++) {
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
        }
    }

    public class ProvideTest {
        @Command("location")
        public void location(int i, Location location, String b, Location location2) {
            message = i + location.toString() + b + location2.toString();
        }

        @Command("location")
        public void location(int i, Location location) {
            message = i + location.toString();
        }
    }

    @Test
    void enumTest() {
        simpleCommandManager.execute(testSender, "enum A");
        Assertions.assertEquals("A", message);
    }

    @Command("enum")
    public void enumCommand(TestEnum testEnum) {
        message = testEnum.name();
    }

    @Test
    void commandTest() {
        simpleCommandManager.execute(testSender, "command1");
        Assertions.assertEquals(message, testSender.getSenderName() + "command1");
        simpleCommandManager.execute(testSender, "command1 abc");
        Assertions.assertEquals(message, testSender.getSenderName() + "abc");
    }

    @Command("command1")
    public void command1(@Sender CommandSender sender) {
        message = sender.getSenderName() + "command1";
    }

    @Command("command1")
    public void command1(@Sender CommandSender sender, String message) {
        this.message = sender.getSenderName() + message;
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
        NodeAnnotationCommand command = (NodeAnnotationCommand) commandManager.getCommand("command").get();
        try {
            Method parseArgsMethod = NodeAnnotationCommand.class.getDeclaredMethod("parseArgs", CommandSender.class, String[].class);
            parseArgsMethod.setAccessible(true);
            try {
                Object invoke = parseArgsMethod.invoke(command, testSender, new String[]{"a", "asd"});
                if (invoke == null) {
                    System.out.println("null");
                } else {
                    System.out.println(CommandNodeUtil.getNodeDescription((CommandNode) invoke));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

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
