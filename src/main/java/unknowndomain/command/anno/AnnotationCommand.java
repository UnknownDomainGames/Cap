package unknowndomain.command.anno;

import unknowndomain.command.Command;
import unknowndomain.command.CommandSender;

import java.lang.reflect.Method;
import java.util.*;

public abstract class AnnotationCommand extends Command {

    private HashSet<CommandArgument> parameters = new HashSet<>();

    public AnnotationCommand(String name) {
        super(name);
    }

    public void registerCommand(Class clazz){
        for(Method method : clazz.getMethods()){

            unknowndomain.command.anno.Command anno =  method.getAnnotation(unknowndomain.command.anno.Command.class);
            if(anno!=null){
                Class[] methodParameters = method.getParameterTypes();
                ArrayList list = new ArrayList<>();
                Collections.addAll(list, Arrays.copyOfRange(methodParameters,1,methodParameters.length));
                parameters.add(new CommandArgument(list,method));
            }
        }
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {



        return false;
    }

    public List<List<Class>> getAllMethodPath(){

    }
}
