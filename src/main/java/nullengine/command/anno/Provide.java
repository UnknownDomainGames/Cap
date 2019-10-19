package nullengine.command.anno;

public @interface Provide {

    /**
     * @return field name
     */
    String value();

    boolean replace() default false;
}
