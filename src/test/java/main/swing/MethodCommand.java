package main.swing;

import nullengine.command.CommandSender;
import nullengine.command.anno.Command;
import nullengine.command.anno.Sender;
import nullengine.command.anno.Tip;

public class MethodCommand {

    @Command("dress")
    public void dress(@Tip("who") String who){
        System.out.println(who+"穿上了裙子");
    }

    @Command("tell")
    public void tell(@Sender CommandSender sender,@Tip("who") Entity entity,@Tip("message") String message){
        entity.sendMessage(" ["+sender.getSenderName()+"]: "+message);
    }

    @Command("addEntity")
    public void addEntity(@Tip("name") String entityName){
        SwingTest.getInstance().getEntityManager().addEntity(new Entity(entityName));
    }

}
