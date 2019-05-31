package main;

import org.junit.Assert;
import org.junit.Test;
import unknowndomain.command.CommandManager;
import unknowndomain.command.CommandResult;
import unknowndomain.command.CommandSender;
import unknowndomain.command.anno.AnnotationCommandManager;
import unknowndomain.command.argument.Argument;
import unknowndomain.command.argument.ArgumentManager;
import unknowndomain.command.exception.DontHavePermissionException;

public class AnnotationCommandTest {


    @Test
    public void test(){

        CommandSender newSender = new CommandSender() {
            @Override
            public boolean hasPermission(String permission) {
                return false;
            }

            @Override
            public void definePermission(String permission, boolean bool) {

            }

            @Override
            public void undefinePermission(String permission) {

            }

            @Override
            public void sendMessage(String message) {
                return;
            }

            @Override
            public String getSenderName() {
                return "test sender";
            }
        };

        ArgumentManager.setClassArgument(new Argument(String.class,(sender,arg)->arg));

        Assert.assertNotNull(ArgumentManager.getArgumentByClass(String.class));

        Assert.assertEquals("test",ArgumentManager.getArgumentByClass(String.class).getHandleFunction().apply(newSender,"test"));

        AnnotationCommandManager.registerAnnotationCommand(new AnnotationCommandClass());

        CommandResult result1 = CommandManager.getInstance().doCommand(newSender, "annotationCommandFalse", "testArgs");

        Assert.assertFalse(result1.success);

        CommandResult result2 = CommandManager.getInstance().doCommand(newSender, "annotationCommandTrue", "testArgs");

        Assert.assertTrue(result2.success);

        CommandResult result3 = CommandManager.getInstance().doCommand(newSender, "annotationCommandResult", "testArgs");

        Assert.assertTrue(result3.success);
        Assert.assertEquals("command message",result3.getMessage());

        CommandResult result4 = CommandManager.getInstance().doCommand(newSender, "annotationCommandVoid", "testArgs");

        Assert.assertTrue(result4.success);

        CommandResult result5 = CommandManager.getInstance().doCommand(newSender, "annotationCommandPermission", "testArgs");

        Assert.assertFalse(result5.success);
        Assert.assertTrue(result5.getThrowable() instanceof DontHavePermissionException);
    }


}
