package unknowndomain.command.argument;

import java.util.HashSet;
import java.util.Set;

public class ArgumentTree {

    private Class parentClass;

    private Class argumentClass;

    private Set<Class> childClasses = new HashSet<>();

    public ArgumentTree(Class argumentClass) {
        this.argumentClass = argumentClass;
    }

    public void setParentClass(Class parentClass){
        this.parentClass = parentClass;
    }

    public void addChildClass(Class childClass){
        this.childClasses.add(childClass);
    }

}
