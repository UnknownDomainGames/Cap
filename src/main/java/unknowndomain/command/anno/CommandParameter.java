package unknowndomain.command.anno;

import unknowndomain.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CommandParameter<T> {

    private Class<T> clazz;
    private BiFunction<CommandSender,String[], T> argumentFunction;
    private Function<String[],Boolean> matchFunction;
    private int needPlace;

    private List<String> placeDescription;
    private String description;

    public CommandParameter(Class<T> clazz, BiFunction<CommandSender, String[], T> argumentFunction, Function<String[], Boolean> matchFunction) {
        this(clazz,argumentFunction,matchFunction,1);
    }

    public CommandParameter(Class<T> clazz, BiFunction<CommandSender, String[], T> argumentFunction, Function<String[], Boolean> matchFunction, int needPlace) {
        this(clazz,argumentFunction,matchFunction,needPlace,Collections.EMPTY_LIST);
    }

    public CommandParameter(Class<T> clazz, BiFunction<CommandSender, String[], T> argumentFunction, Function<String[], Boolean> matchFunction, int needPlace, List<String> placeDescription) {
        this(clazz,argumentFunction,matchFunction,needPlace,placeDescription,"");
    }

    public CommandParameter(Class<T> clazz, BiFunction<CommandSender, String[], T> argumentFunction, Function<String[], Boolean> matchFunction, int needPlace, List<String> placeDescription, String description) {
        this.clazz = clazz;
        this.argumentFunction = argumentFunction;
        this.matchFunction = matchFunction;
        this.needPlace = needPlace;
        this.placeDescription = placeDescription;
        this.description = description;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public BiFunction<CommandSender, String[], T> getArgumentFunction() {
        return argumentFunction;
    }

    public boolean match(String[] args){
        return matchFunction.apply(args);
    }

    public int getNeedPlace() {
        return needPlace;
    }

    public List<String> getPlaceDescription() {
        return placeDescription;
    }

    public void setPlaceDescription(List<String> placeDescription) {
        this.placeDescription = placeDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public T getInstance(CommandSender sender,String[] args){
        return argumentFunction.apply(sender,args);
    }
}
