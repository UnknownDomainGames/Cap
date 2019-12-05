package main.swing;

import main.LocationProvider;
import main.WorldArgument;
import nullengine.command.*;
import nullengine.command.anno.MethodAnnotationCommand;
import nullengine.command.argument.ArgumentManager;
import nullengine.command.argument.SimpleArgumentManager;
import nullengine.command.suggestion.SimpleSuggesterManager;
import nullengine.command.suggestion.SuggesterManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SwingTest {

    private static SwingTest INSTANCE;
    private SimpleCommandManager commandManager = new SimpleCommandManager();
    private SuggesterManager suggesterManager = new SimpleSuggesterManager();
    private ArgumentManager argumentManager = new SimpleArgumentManager();
    private ConsoleSender consoleSender = new ConsoleSender();
    private EntityManager entityManager = new EntityManager();

    private CommandResolver resolve;

    public SwingTest() {
        INSTANCE = this;
    }

    public static SwingTest getInstance() {
        return INSTANCE;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public static void main(String[] args) {
        SwingTest swingTest = new SwingTest();
        swingTest.commandReady();
        swingTest.show();
    }

    public void commandReady() {

        EntityArgument entityArgument = new EntityArgument();

        argumentManager.setClassDefaultArgument(entityArgument);
        argumentManager.setClassDefaultArgument(new WorldArgument());

        MethodAnnotationCommand.getBuilder(commandManager)
                .setArgumentManager(argumentManager)
                .addProvider(new LocationProvider())
                .setSuggesterManager(suggesterManager)
                .addCommandHandler(new MethodCommand())
                .register();

        try {
            Field field = BaseCommandManager.class.getDeclaredField("resolver");
            field.setAccessible(true);
            resolve = (CommandResolver) field.get(commandManager);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public SuggesterManager getSuggesterManager() {
        return suggesterManager;
    }

    public ArgumentManager getArgumentManager() {
        return argumentManager;
    }

    public void show() {
        JFrame jFrame = new JFrame("Swing example");

        jFrame.setSize(800, 800);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jFrame.setLayout(new BorderLayout());

        JTextArea textArea = new TextArea();
        JTextField textField = new JTextField();
        JLabel label = new JLabel("");

        jFrame.add(textArea, BorderLayout.CENTER);
        jFrame.add(textField, BorderLayout.SOUTH);

        textArea.setLayout(new BorderLayout());
        textArea.add(label, BorderLayout.SOUTH);

        textArea.setEditable(false);

        textField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                String text = textField.getText();
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    System.out.println(text);
                    if (text.startsWith("/"))
                        commandManager.execute(consoleSender, text.substring(1));
                    textField.setText("");
                    setTips();
                } else if (e.getKeyChar() == KeyEvent.VK_SPACE) {
                    CommandResolver.Result result = resolve.resolve(text.substring(1));
                    ArgumentCheckResult argumentCheckResult = commandManager.checkLastArgument(consoleSender,result.command,Arrays.copyOfRange(result.args,0,result.args.length-1));
                    if(!argumentCheckResult.isValid()){
                        System.out.println(argumentCheckResult.getHelpMessage());
                    }
                    if (text.startsWith("/")) {
                        setTips();
                    }
                } else {
                    if (text.startsWith("/")) {
                        setTips();
                    }
                }
            }

            private void setTips() {
                if (textField.getText().isEmpty()) {
                    label.setText("");
                    return;
                }
                String text = textField.getText().substring(1);
                CommandResolver.Result result = resolve.resolve(text);
                if (result.args != null && result.args.length != 0) {
                    List<String> tips = commandManager.getTips(consoleSender, result.command, result.args);
                    String tipsString = tips.stream().map(str -> "<" + str + "> ").collect(Collectors.joining());
                    String space = getSpace("/" + result.command + " " + Arrays.stream(Arrays.copyOfRange(result.args, 0, result.args.length - 1)).map(str -> str + " ").collect(Collectors.joining()) + " ");
                    label.setText(space + tipsString);
                } else label.setText("");
            }

            private String getSpace(String s) {
                FontMetrics fontMetrics = textField.getFontMetrics(textField.getFont());
                int textWidth = fontMetrics.stringWidth(s);
                int halfWidth = fontMetrics.charWidth(' ');
                int fullWidth = fontMetrics.charWidth('　');
                int fullNum = 0;
                while (true) {

                    if ((textWidth - fullWidth * fullNum) % halfWidth <= 2) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < (textWidth - fullWidth * fullNum) / halfWidth; i++)
                            sb.append(' ');
                        for (int i = 0; i < fullNum; i++)
                            sb.append('　');
                        return sb.toString();
                    }
                    fullNum++;
                }
            }

        });

        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {

            List<String> completeList = new ArrayList<>();
            int completeIndex = 0;

            @Override
            public void eventDispatched(AWTEvent event) {
                if (event instanceof KeyEvent && event.getID() == KeyEvent.KEY_PRESSED) {
                    KeyEvent keyEvent = (KeyEvent) event;
                    if (keyEvent.getKeyChar() == KeyEvent.VK_TAB) {
                        keyEvent.consume();

                        String text = textField.getText();
                        if (text.startsWith("/")) {
                            CommandResolver.Result result = resolve.resolve(text.substring(1));
                            if (result.args == null || result.args.length == 0) {
                                if (!completeList.contains(result.command)) {
                                    completeList = commandManager.complete(consoleSender, result.command, result.args);
                                    System.out.println("suggest: " + completeList.toString());
                                    completeIndex = -1;
                                }
                                if (completeList.isEmpty())
                                    return;
                                completeIndex++;
                                completeIndex %= completeList.size();
                                setCommand(completeList.get(completeIndex), result.args);
                            } else {
                                if (!completeList.contains(result.args[result.args.length - 1])) {
                                    completeList = commandManager.complete(consoleSender, result.command, result.args);
                                    System.out.println("suggest: " + completeList.toString());
                                    completeIndex = -1;
                                }
                                if (completeList.isEmpty())
                                    return;
                                completeIndex++;
                                completeIndex %= completeList.size();
                                result.args[result.args.length - 1] = completeList.get(completeIndex);
                                setCommand(result.command, result.args);
                            }
                        }
                    }
                }
            }

            private void setCommand(String command, String[] args) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("/").append(command);
                Arrays.stream(args).forEach(arg -> stringBuilder.append(" " + arg));
                textField.setText(stringBuilder.toString());
            }
        }, AWTEvent.KEY_EVENT_MASK);

        jFrame.setVisible(true);
    }


}
