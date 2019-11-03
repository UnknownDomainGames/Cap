package main;

public class World {

    private String worldName;

    public World(String worldName) {
        this.worldName = worldName;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    @Override
    public String toString() {
        return "World{" +
                "worldName='" + worldName + '\'' +
                '}';
    }
}
