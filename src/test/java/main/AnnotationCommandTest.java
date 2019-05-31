package main;

import org.junit.Assert;
import org.junit.Test;
import unknowndomain.command.CommandManager;
import unknowndomain.command.CommandResult;
import unknowndomain.command.CommandSender;
import unknowndomain.command.anno.AnnotationCommandManager;
import unknowndomain.command.argument.Argument;
import unknowndomain.command.argument.ArgumentManager;
import unknowndomain.command.exception.PermissionNotEnoughException;

public class AnnotationCommandTest {


    @Test
    public void test() {

        CommandSender newSender = new CommandSender() {
            @Override
            public boolean hasPermission(String permission) {
                return false;
            }

            @Override
            public void setPermission(String permission, boolean bool) {

            }

            @Override
            public void removePermission(String permission) {

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

        ArgumentManager.setClassArgument(new Argument(String.class, (sender, arg) -> arg));

        Assert.assertNotNull(ArgumentManager.getArgumentByClass(String.class));

        Assert.assertEquals("test", ArgumentManager.getArgumentByClass(String.class).getHandleFunction().apply(newSender, "test"));

        AnnotationCommandManager.registerAnnotationCommand(new AnnotationCommandClass());

        CommandResult result1 = CommandManager.getInstance().executeCommand(newSender, "annotationCommandFalse", "testArgs");

        Assert.assertFalse(result1.isSuccess());

        CommandResult result2 = CommandManager.getInstance().executeCommand(newSender, "annotationCommandTrue", "testArgs");

        Assert.assertTrue(result2.isSuccess());

        CommandResult result3 = CommandManager.getInstance().executeCommand(newSender, "annotationCommandResult", "testArgs");

        Assert.assertTrue(result3.isSuccess());
        Assert.assertEquals("command message", result3.getMessage());

        CommandResult result4 = CommandManager.getInstance().executeCommand(newSender, "annotationCommandVoid", "testArgs");

        Assert.assertTrue(result4.isSuccess());

        CommandResult result5 = CommandManager.getInstance().executeCommand(newSender, "annotationCommandPermission", "testArgs");

        Assert.assertFalse(result5.isSuccess());
        Assert.assertTrue(result5.getCause() instanceof PermissionNotEnoughException);
    }


}
