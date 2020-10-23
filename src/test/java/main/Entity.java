package main;

import engine.command.CommandSender;

public interface Entity extends CommandSender {
    World getWorld();
}
