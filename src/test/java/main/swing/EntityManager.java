package main.swing;

import java.util.ArrayList;
import java.util.List;

public class EntityManager {

    private List<SwingEntity> entities = new ArrayList<>();

    public void addEntity(SwingEntity swingEntity){
        entities.add(swingEntity);
    }

    public List<SwingEntity> getEntities(){
        return entities;
    }

    public SwingEntity getEntity(String name){
        return entities.stream().filter(swingEntity -> swingEntity.getName().equals(name)).findAny().orElse(null);
    }

    public void removeEntity(SwingEntity swingEntity){
        this.entities.remove(swingEntity);
    }

}
