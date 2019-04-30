package unknowndomain.command.traditional;

import unknowndomain.command.Command;
import unknowndomain.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TraditionalCommand extends Command {

    private CommandExecutor executer;
    private CompletionExecutor completionExecutor;

    public TraditionalCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender executor, String[] args) {
        if(executer==null)
            return false;
        return executer.execute(executor,this.name,args);
    }

    public void setExecuter(CommandExecutor executer) {
        this.executer = executer;
    }

    public void setCompletionExecutor(CompletionExecutor completionExecutor) {
        this.completionExecutor = completionExecutor;
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return Optional.ofNullable(completionExecutor).orElse((sender1,string,args1)->new ArrayList<>()).complete(sender,this.name,args);
    }
}
