package main;

import com.google.common.collect.Lists;
import nullengine.command.CommandSender;
import nullengine.command.SimpleCommandManager;
import nullengine.command.anno.*;

import java.util.List;

public class InnerCommandTest {

    public void test() {

        SimpleCommandManager commandManager = new SimpleCommandManager();

        InnerAnnotationCommand.getBuilder(commandManager)
                .caseCommand("say", new Runnable() {

                    @Sender
                    public CommandSender sender;

                    public String message;

                    @Override
                    public void run() {
                        sender.sendMessage(message);
                    }

                    @Provide("message")
                    public String parse(String originalArgument){
                        return "<prefix>"+originalArgument;
                    }

                    @Complete("message")
                    public List<String> onCompleteMessage(String[] args){
                        return Lists.newArrayList("abc","123");
                    }
                })

                .register();


    }

}
