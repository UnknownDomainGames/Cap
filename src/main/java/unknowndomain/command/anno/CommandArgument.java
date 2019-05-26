package unknowndomain.command.anno;

import java.util.List;

public interface CommandArgument {

    public boolean match(List<String> list);

    boolean hasCommand();

    int needPlace();



}
