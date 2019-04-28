package unknowndomain.command.argument;

import unknowndomain.command.Command;

public abstract class ArgumentCommand extends Command {

    private ArgumentTree argumentTree = new ArgumentTree(null);

    public ArgumentCommand(String name,Class... args) {
        super(name);
    }


}
