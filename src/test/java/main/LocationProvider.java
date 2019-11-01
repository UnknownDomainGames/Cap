package main;

import nullengine.command.anno.Provide;
import nullengine.command.anno.Sender;
import nullengine.command.anno.Tip;

public class LocationProvider {

    @Provide
    public Location a(@Sender Entity entity,@Tip("x") int x,@Tip("y") int y,@Tip("z") int z){
        return b(entity.getWorld(),x,y,z);
    }

    @Provide
    public Location b(@Tip("worldName") String world,@Tip("x") int x,@Tip("y") int y,@Tip("z") int z){
        return new Location(world,x,y,z);
    }

}
