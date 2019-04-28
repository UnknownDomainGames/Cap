package unknowndomain.command.argument;

import unknowndomain.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CommandArgumentBuilder<T> {

    private Class<T> clazz;
    private BiFunction<CommandSender,String[], T> instance;
    private int needPlace = 1;

    private String description = "";

    private List<String> placeDescriptions = new ArrayList<>();

    public CommandArgumentBuilder setClazz(Class<T> clazz) {
        this.clazz = clazz;
        return this;
    }

    public CommandArgumentBuilder setInstance(BiFunction<CommandSender,String[], T> instance) {
        this.instance = instance;
        return this;
    }

    public CommandArgumentBuilder setNeedPlace(int needPlace) {
        this.needPlace = needPlace;
        return this;
    }

    public CommandArgumentBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public CommandArgumentBuilder setPlaceDescription(List<String> placeDescriptions) {
        this.placeDescriptions = placeDescriptions;
        return this;
    }

    public CommandArgumentBuilder addPlaceDescription(String placeDescription) {
        this.placeDescriptions.add(placeDescription);
        return this;
    }

    public void register(){
        CommandArgumentManager.registerArgument(new CommandArgument<T>(clazz,instance,needPlace,description,placeDescriptions));
    }
}
