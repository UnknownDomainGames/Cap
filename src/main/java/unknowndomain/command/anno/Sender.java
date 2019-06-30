package unknowndomain.command.anno;

import unknowndomain.command.CommandSender;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Indicates that a parameter with annotations which is extends CommandSender.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sender {

    Class<? extends CommandSender>[] value() default {};

}