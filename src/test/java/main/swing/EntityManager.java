package main.swing;

import java.util.ArrayList;
import java.util.List;

public class EntityManager {

    private List<Entity> entities = new ArrayList<>();

    public void addEntity(Entity entity){
        entities.add(entity);
    }

    public List<Entity> getEntities(){
        return entities;
    }

    public Entity getEntity(String name){
        return entities.stream().filter(entity -> entity.getName().equals(name)).findAny().orElse(null);
    }

    public void removeEntity(Entity entity){
        this.entities.remove(entity);
    }

}
