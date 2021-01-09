package engine.command.impl;

import engine.command.CommandExceptionHandler;

public class DefaultExceptionHandler implements CommandExceptionHandler {
    @Override
    public void handleOnExecuting(Exception e) {
        System.out.println("Caught an exception when executing command.");
        e.printStackTrace();
    }

    @Override
    public void handleOnSuggesting(Exception e) {
        System.out.println("Caught an exception when suggesting command.");
        e.printStackTrace();
    }

    @Override
    public void handleOnGettingTips(Exception e) {
        System.out.println("Caught an exception when getting tips.");
        e.printStackTrace();
    }

    @Override
    public void handleOnCheckingArgument(Exception e) {
        System.out.println("Caught an exception when checking argument.");
        e.printStackTrace();
    }
}
