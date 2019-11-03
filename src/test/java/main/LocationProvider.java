package main;

import nullengine.command.CommandSender;
import nullengine.command.anno.Provide;
import nullengine.command.anno.Sender;
import nullengine.command.anno.Tip;

public class LocationProvider {

    @Provide
    public Location a(@Sender CommandSender entity, @Tip("x") int x, @Tip("y") int y, @Tip("z") int z){
        return b(new World("123"),x,y,z);
    }

    @Provide
    public Location b(@Tip("world") World world,@Tip("x") int x,@Tip("y") int y,@Tip("z") int z){
        return new Location(world,x,y,z);
    }

}