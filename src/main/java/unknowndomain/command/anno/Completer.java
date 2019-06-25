package unknowndomain.command.anno;

import unknowndomain.command.traditional.CommandCompleter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;

@Target({PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Completer {
    Class<? extends CommandCompleter> value();
}
