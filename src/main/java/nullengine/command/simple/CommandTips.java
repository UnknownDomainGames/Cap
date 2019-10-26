package nullengine.command.simple;

import nullengine.command.CommandSender;

import java.util.List;

public interface CommandTips {
    List<String> getTips(CommandSender sender, String[] args);
}
