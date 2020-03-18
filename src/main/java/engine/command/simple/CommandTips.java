package engine.command.simple;

import engine.command.CommandSender;

import java.util.List;

@FunctionalInterface
public interface CommandTips {
    List<String> getTips(CommandSender sender, String[] args);
}
