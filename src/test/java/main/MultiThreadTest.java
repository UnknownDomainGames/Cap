package main;

import engine.command.BaseCommandManager;
import engine.command.anno.Command;
import engine.command.anno.NodeAnnotationCommand;
import engine.command.impl.SimpleCommandManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadTest {

    String thread1;
    String thread2;
    String thread3;

    boolean stop = false;

    AtomicInteger executeTimes = new AtomicInteger();

    @Test
    public void test() {

        BaseCommandManager commandManager = new SimpleCommandManager();

        NodeAnnotationCommand.METHOD.getBuilder(commandManager)
                .addCommandHandler(this)
                .register();

        Thread t1 = new Thread(() -> {
            TestSender testSender = new TestSender("thread 1", null, null);
            Random random = new Random(System.currentTimeMillis());
            while (!stop) {
                String s = String.valueOf(random.nextInt(100000));
                commandManager.execute(testSender, "test thread1 " + s);
                Assertions.assertEquals(thread1, s);
                executeTimes.incrementAndGet();
            }
        });
        Thread t2 = new Thread(() -> {
            TestSender testSender = new TestSender("thread 2", null, null);
            Random random = new Random(System.currentTimeMillis());
            while (!stop) {
                String s = String.valueOf(random.nextInt(100000));
                commandManager.execute(testSender, "test thread2 " + s);
                Assertions.assertEquals(thread2, s);
                executeTimes.incrementAndGet();
            }
        });
        Thread t3 = new Thread(() -> {
            TestSender testSender = new TestSender("thread 3", null, null);
            Random random = new Random(System.currentTimeMillis());
            while (!stop) {
                String s = String.valueOf(random.nextInt(100000));
                commandManager.execute(testSender, "test thread3 " + s);
                Assertions.assertEquals(thread3, s);
                executeTimes.incrementAndGet();
            }
        });
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

    @Command("test")
    public void setString(String fieldName, String value) {
        if (fieldName.equals("thread1")) {
            thread1 = value;
        } else if (fieldName.equals("thread2")) {
            thread2 = value;
        } else if (fieldName.equals("thread3")) {
            thread3 = value;
        }
    }


}
