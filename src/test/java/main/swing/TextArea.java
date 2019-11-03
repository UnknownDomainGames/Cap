package main.swing;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.CopyOnWriteArrayList;

public class TextArea extends JTextArea {

    private CopyOnWriteArrayList<String> cache = new CopyOnWriteArrayList<>();
    private boolean stop = false;

    public TextArea() {

        Thread thread = new Thread(() -> {

            Runtime.getRuntime().addShutdownHook(new Thread(() -> stop = true));

            while (!stop) {
                for (String s : cache)
                    append(s);
                cache.clear();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        System.setOut(new PrintStream(System.out) {
            public void println(String x) {
                TextArea.this.cache.add(x + "\n");
            }

            public void println(Object o) {
                println(o.toString());
            }
        });
    }
}
