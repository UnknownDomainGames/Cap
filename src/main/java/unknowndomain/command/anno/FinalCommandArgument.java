package unknowndomain.command.anno;

import java.lang.reflect.Method;
import java.util.List;

public class FinalCommandArgument<T> extends ReflectCommandArgument {

    private T t;

    public FinalCommandArgument(T t) {
        this.t = t;
    }

    @Override
    public boolean match(List<String> list) {
        if(list==null)
            return false;
        if(list.isEmpty())
            return false;
        return t.equals(list.get(0));
    }

    @Override
    public int needPlace() {
        return 1;
    }
}
