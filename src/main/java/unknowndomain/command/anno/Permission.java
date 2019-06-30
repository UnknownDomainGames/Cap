package unknowndomain.command.anno;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;

@Target({PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
    String[] value();
}
