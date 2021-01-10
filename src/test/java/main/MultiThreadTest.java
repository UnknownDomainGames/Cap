package main;

import engine.command.BaseCommandManager;
import engine.command.CommandSender;
import engine.command.anno.Command;
import engine.command.anno.NodeAnnotationCommand;
import engine.command.anno.Sender;
import engine.command.impl.SimpleCommandManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class MultiThreadTest {

    String thread1;
    String thread2;
    String thread3;

    boolean stop = false;

    AtomicInteger executeTimes = new AtomicInteger();

    BaseCommandManager commandManager;

    @Test
    public void test() {

        commandManager = new SimpleCommandManager();

        NodeAnnotationCommand.METHOD.getBuilder(commandManager)
                .addCommandHandler(this)
                .register();

        Thread t1 = new Thread(() -> {
            TestSender testSender = new TestSender("thread 1", null, null);
            Random random = new Random(System.currentTimeMillis());
            while (!stop) {
                executeCommand("thread1", random, testSender, () -> thread1);
            }
        });
        Thread t2 = new Thread(() -> {
            TestSender testSender = new TestSender("thread 2", null, null);
            Random random = new Random(System.currentTimeMillis());
            while (!stop) {
                executeCommand("thread2", random, testSender, () -> thread2);
            }
        });
        Thread t3 = new Thread(() -> {
            TestSender testSender = new TestSender("thread 3", null, null);
            Random random = new Random(System.currentTimeMillis());
            while (!stop) {
                executeCommand("thread3", random, testSender, () -> thread3);
            }
        });
        t1.setName("thread-1");
        t2.setName("thread-2");
        t3.setName("thread-3");
        t1.start();
        t2.start();
        t3.start();

        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stop = true;
        System.out.println("execute times: " + executeTimes.get());
    }

    private void executeCommand(String threadName, Random random, TestSender sender, Supplier<String> supplier) {
        String s;
        if (random.nextInt(2) == 0)
            s = String.valueOf(random.nextInt(100000));
        else
            s = TestEnum.values()[random.nextInt(TestEnum.values().length)].name();
        commandManager.execute(sender, "test " + threadName + " " + s);
        Assertions.assertEquals(supplier.get(), sender.getSenderName() + s);
        executeTimes.incrementAndGet();
    }

    @Command("test")
    public void setString(@Sender CommandSender sender, String fieldName, String value) {
        switch (fieldName) {
            case "thread1":
                thread1 = sender.getSenderName() + value;
                break;
            case "thread2":
                thread2 = sender.getSenderName() + value;
                break;
            case "thread3":
                thread3 = sender.getSenderName() + value;
                break;
        }
    }

    @Command("test")
    public void setString(@Sender CommandSender sender, String fieldName, TestEnum value) {
        switch (fieldName) {
            case "thread1":
                thread1 = sender.getSenderName() + value.name();
                break;
            case "thread2":
                thread2 = sender.getSenderName() + value.name();
                break;
            case "thread3":
                thread3 = sender.getSenderName() + value.name();
                break;
        }
    }

    public enum TestEnum {
        STOP, START, RUNNING, END
    }


}
