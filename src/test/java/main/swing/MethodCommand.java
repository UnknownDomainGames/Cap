package main.swing;

import main.Location;
import nullengine.command.CommandSender;
import nullengine.command.anno.Command;
import nullengine.command.anno.Sender;
import nullengine.command.anno.Tip;

public class MethodCommand {

    @Command("dress")
    public void dress(@Tip("who") Entity who) {
        if (who.hasPermission("dress"))
            System.out.println(who.getName() + "穿上了裙子");
        else System.out.println(who.getName()+" 无权穿上裙子!");
    }

    @Command("tell")
    public void tell(@Sender CommandSender sender, @Tip("who") Entity entity, @Tip("message") String message) {
        entity.sendMessage(" [" + sender.getSenderName() + "]: " + message);
    }

    @Command("addEntity")
    public void addEntity(@Tip("name") String entityName) {
        SwingTest.getInstance().getEntityManager().addEntity(new Entity(entityName));
    }

    @Command("removeEntity")
    public void removeEntity(@Tip("entity") Entity entity) {
        SwingTest.getInstance().getEntityManager().removeEntity(entity);
    }

    @Command("permission")
    public void setPermission(@Tip("entity") Entity entity, @Tip("permission") String permission, @Tip("true/false") boolean bool) {
        entity.setPermission(permission, bool);
    }

    @Command("test")
    public void commandTest(@Sender CommandSender sender, Location location){

    }


}
