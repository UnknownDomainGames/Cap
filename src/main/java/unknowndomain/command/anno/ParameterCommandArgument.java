package unknowndomain.command.anno;

import java.util.List;

public class ParameterCommandArgument extends ReflectCommandArgument {

    private Class clazz;

    private CommandParameter commandParameter;

    public ParameterCommandArgument(Class clazz) {
        this.clazz = clazz;
        commandParameter = CommandParameterManager.getCommandArgument(clazz);
    }

    @Override
    public boolean match(List<String> list) {
        return commandParameter.match(list.toArray(new String[0]));
    }

    @Override
    public int needPlace() {
        return commandParameter.getNeedPlace();
    }
}
