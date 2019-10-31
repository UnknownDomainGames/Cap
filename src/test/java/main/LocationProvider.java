package main;

import nullengine.command.anno.Provide;
import nullengine.command.anno.Sender;

public class LocationProvider {

    @Provide
    public Location a(@Sender Entity entity,int x,int y,int z){
        return new Location(entity,x,y,z);
    }

    @Provide
    public Location b(String world,int x,int y,int z){
        return new Location(world,x,y,z);
    }

}
