package main.swing;

import engine.command.CommandSender;
import engine.command.anno.Command;
import engine.command.anno.Permission;
import engine.command.anno.Sender;
import engine.command.anno.Tip;
import main.Location;

public class MethodCommand {

    @Command("dress")
    @Permission("dress")
    public void dress(@Tip("who") SwingEntity who) {
        if (who.hasPermission("dress"))
            System.out.println(who.getName() + "穿上了裙子");
        else System.out.println(who.getName()+" 无权穿上裙子!");
    }

    @Command("tell")
    public void tell(@Sender CommandSender sender, @Tip("who") SwingEntity swingEntity, @Tip("message") String message) {
        swingEntity.sendMessage(" [" + sender.getSenderName() + "]: " + message);
    }

    @Command("addEntity")
    public void addEntity(@Tip("name") String entityName) {
        SwingTest.getInstance().getEntityManager().addEntity(new SwingEntity(entityName));
    }

    @Command("removeEntity")
    public void removeEntity(@Tip("entity") SwingEntity swingEntity) {
        SwingTest.getInstance().getEntityManager().removeEntity(swingEntity);
    }

    @Command("permission")
    public void setPermission(@Tip("entity") SwingEntity swingEntity, @Tip("permission") String permission, @Tip("true/false") boolean bool) {
        swingEntity.setPermission(permission, bool);
    }

    @Command("test")
    public void commandTest(@Sender CommandSender sender, Location location){

    }


}
