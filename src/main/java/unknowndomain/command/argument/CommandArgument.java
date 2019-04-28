package unknowndomain.command.argument;

import com.sun.istack.internal.NotNull;
import unknowndomain.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CommandArgument<T> {

    private Class<T> clazz;
    private BiFunction<CommandSender,String[], T> instance;
    private int needPlace;

    private List<String> placeDescription;
    private String description;

    public CommandArgument(Class<T> clazz, BiFunction<CommandSender,String[], T> instance) {
        this(clazz, instance, 1);
    }

    public CommandArgument(Class<T> clazz, BiFunction<CommandSender,String[], T> instance, int needplace) {
        this(clazz, instance, needplace, "");
    }

    public CommandArgument(Class<T> clazz, BiFunction<CommandSender,String[], T> instance, int needPlace, String description) {
        this(clazz, instance, needPlace, description, new ArrayList<>());
    }

    public CommandArgument(Class<T> clazz, BiFunction<CommandSender,String[], T> instance, int needPlace, String description, List<String> placeDescription) {
        this.clazz = clazz;
        this.instance = instance;
        this.needPlace = needPlace;
        this.placeDescription = placeDescription;
        this.description = description;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public BiFunction<CommandSender,String[], T> getInstance() {
        return instance;
    }

    public int getNeedPlace() {
        return needPlace;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getPlaceDescription() {
        return placeDescription;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPlaceDescription(@NotNull List<String> placeDescription) {
        this.placeDescription = placeDescription;
    }

    public T getArgument(CommandSender sender,List<String> args){
        return getArgument(sender,args.toArray(new String[0]));
    }

    public T getArgument(CommandSender sender,String... strings){
        return instance.apply(sender,strings);
    }
}
