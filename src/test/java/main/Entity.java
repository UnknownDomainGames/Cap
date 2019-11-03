package main;

import nullengine.command.CommandSender;

public interface Entity extends CommandSender {
    World getWorld();
}
