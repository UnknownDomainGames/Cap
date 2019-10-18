package main;

import com.google.common.collect.Lists;
import nullengine.command.CommandSender;
import nullengine.command.SimpleCommandManager;
import nullengine.command.anno.ArgumentHandler;
import nullengine.command.anno.Sender;
import nullengine.command.inner.anno.Complete;
import nullengine.command.inner.InnerCommand;

import java.util.List;

public class InnerCommandTest {

    public void test() {

        SimpleCommandManager commandManager = new SimpleCommandManager();

        InnerCommand.getBuilder(commandManager)
                .caseCommand("say", new Runnable() {

                    @Sender
                    public CommandSender sender;

                    public String message;

                    @Override
                    public void run() {
                        sender.sendMessage(message);
                    }

                    @ArgumentHandler("message")
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
