package main;

import nullengine.command.Command;
import nullengine.command.CommandSender;
import nullengine.command.SimpleCommandManager;
import nullengine.command.anno.*;
import nullengine.command.exception.PermissionNotEnoughException;
import nullengine.permission.HashPermissible;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

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

//    @Test
//    void test() {
//        Entity entitySender = new Entity() {
//            private HashPermissible permissible = new HashPermissible();
//            @Override
//            public String getWorld() {
//                return "testWorld";
//            }
//
//            @Override
//            public void sendMessage(String message) {
//                ClassNodeCommandTest.this.message = message;
//            }
//
//            @Override
//            public String getSenderName() {
//                return "entity";
//            }
//
//            @Override
//            public boolean hasPermission(String permission) {
//                return permissible.hasPermission(permission);
//            }
//
//            @Override
//            public void setPermission(String permission, boolean bool) {
//                permissible.setPermission(permission,bool);
//            }
//
//            @Override
//            public void removePermission(String permission) {
//                permissible.removePermission(permission);
//            }
//        };
//
//        ClassAnnotationCommand.getBuilder(simpleCommandManager)
//                .caseCommand("test", new Runnable() {
//                    @Sender
//                    public CommandSender sender;
//
//                    public Location location;
//
//                    public Random random;
//
//                    @Ignore
//                    public int i = 123;
//
//                    public String text;
//
//                    @Override
//                    @Permission("test")
//                    public void run() {
//                        sender.sendMessage(sender.getSenderName()+location+random.nextInt()+text);
//                    }
//
//                    @Provide(value = "random")
//                    public Random random(int seed){
//                        return new Random(seed);
//                    }
//                })
//                .register();
//
//        int seed = 12356;
//        String world = "abc";
//        Location location = new Location(world,1,2,3);
//
//        String text = "耗子女装";
//
//        Command command = simpleCommandManager.getCommand("test").get();
//
//        Assertions.assertThrows(PermissionNotEnoughException.class,()->command.execute(entitySender,new String[]{"1","2","3",Integer.valueOf(seed).toString(),text}));
//
//        entitySender.setPermission("test",true);
//
//        simpleCommandManager.execute(entitySender,String.format("test %d %d %d %d %s",location.getX(),location.getY(),location.getZ(),seed,text));
//
//        Assertions.assertEquals(message,entitySender.getSenderName()+new Location(entitySender.getWorld(),location.getX(),location.getY(),location.getZ())+new Random(seed).nextInt()+text);
//
//        simpleCommandManager.execute(entitySender,String.format("test %s %d %d %d %d %s",world,location.getX(),location.getY(),location.getZ(),seed,text));
//
//        Assertions.assertEquals(message,entitySender.getSenderName()+location+new Random(seed).nextInt()+text);
//    }
}
