package main;

import engine.command.Command;
import engine.command.CommandException;
import engine.command.CommandSender;
import engine.command.SimpleCommandManager;
import engine.command.anno.*;
import engine.command.argument.SimpleArgumentManager;
import engine.permission.HashPermissible;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Random;

public class ClassNodeCommandTest {

    SimpleCommandManager simpleCommandManager = new SimpleCommandManager();
    private TestSender testSender = new TestSender("methodNodeTest", string -> message = string, c -> message = c.getType().name());

    private String message;

    @Test
    void commandAttribute() {
        ClassAnnotationCommand.getBuilder(simpleCommandManager)
                .caseCommand("attribute", "test", "help", () -> {
                })
                .register();
        Command command = simpleCommandManager.getCommand("attribute").get();
        Assertions.assertEquals("test", command.getDescription());
        Assertions.assertEquals("help", command.getHelpMessage());
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
        simpleCommandManager.execute(testSender, "use");
        Assertions.assertEquals(message, testSender.getSenderName());
    }

    @Test
    void test() {
        Entity entitySender = new Entity() {
            private HashPermissible permissible = new HashPermissible();

            @Override
            public World getWorld() {
                return new World("testWorld");
            }

            @Override
            public void sendMessage(String message) {
                ClassNodeCommandTest.this.message = message;
            }

            @Override
            public String getSenderName() {
                return "entity";
            }

            @Override
            public void sendCommandException(CommandException exception) {
                message = exception.getType().name();
            }

            @Override
            public boolean hasPermission(@Nonnull String permission) {
                return permissible.hasPermission(permission);
            }

            @Override
            public void setPermission(@Nonnull String permission, boolean bool) {
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
        SimpleArgumentManager argumentManager = new SimpleArgumentManager();
        argumentManager.appendArgumentAndSetDefaultIfNotExist(new WorldArgument());
        ClassAnnotationCommand.getBuilder(simpleCommandManager)
                .addProvider(new Object() {
                                 @Provide
                                 public Random random(int seed) {
                                     return new Random(seed);
                                 }
                             }
                )
                .setArgumentManager(argumentManager)
                .addProvider(new LocationProvider())
                .caseCommand("test", new Runnable() {
                    @Sender
                    public CommandSender sender;

                    public Location location;

                    public Random random;

                    @Ignore
                    public int i = 123;

                    public String text;

                    @Override
                    @Permission("test")
                    public void run() {
                        sender.sendMessage(sender.getSenderName() + location + random.nextInt() + text);
                    }

                }).register();

        int seed = 12356;
        World world = new World("abc");
        Location location = new Location(world, 1, 2, 3);

        String text = "耗子女装";

        Command command = simpleCommandManager.getCommand("test").get();

        command.execute(entitySender, new String[]{"1", "2", "3", Integer.valueOf(seed).toString(), text});
        Assertions.assertEquals(CommandException.Type.PERMISSION_NOT_ENOUGH.name(), message);

        entitySender.setPermission("test", true);

        simpleCommandManager.execute(entitySender, String.format("test %f %f %f %d %s", location.getX(), location.getY(), location.getZ(), seed, text));

        Assertions.assertEquals(message, entitySender.getSenderName() + new Location(entitySender.getWorld(), location.getX(), location.getY(), location.getZ()) + new Random(seed).nextInt() + text);

        simpleCommandManager.execute(entitySender, String.format("test %s %f %f %f %d %s", world.getWorldName(), location.getX(), location.getY(), location.getZ(), seed, text));

        Assertions.assertEquals(message, entitySender.getSenderName() + location + new Random(seed).nextInt() + text);
    }
}
