package main;

import unknowndomain.command.CommandResult;
import unknowndomain.command.CommandSender;
import unknowndomain.command.anno.Command;
import unknowndomain.command.anno.Sender;

public class AnnotationCommandClass {

    @Command(value = "annotationCommandFalse",desc = "desc",helpMessage = "test help")
    public boolean falseMethod(@Sender CommandSender sender, String text){
        return false;
    }

    @Command(value = "annotationCommandTrue",desc = "desc",helpMessage = "test help")
    public boolean trueMethod(@Sender CommandSender sender, String text){
        return true;
    }

    @Command(value = "annotationCommandResult",desc = "desc",helpMessage = "test help")
    public CommandResult resultMethod(@Sender CommandSender sender, String text){
        return new CommandResult(true,"command message");
    }

    @Command(value = "annotationCommandVoid",desc = "desc",helpMessage = "test help")
    public void voidMethod(@Sender CommandSender sender, String text){

    }

}
