package unknowndomain.command.traditional;

import unknowndomain.command.Command;
import unknowndomain.command.CommandSender;

public class TraditionalCommand extends Command {

    private CommandExecuter executer;

    public TraditionalCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender executor, String[] args) {
        if(executer==null)
            return false;
        return executer.execute(executor,this.name,args);
    }

    public void setExecuter(CommandExecuter executer) {
        this.executer = executer;
    }
}
