package engine.command.util.node;

import engine.command.CommandSender;
import engine.command.suggestion.Suggester;
import engine.command.util.StringArgs;
import engine.command.util.SuggesterHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EnumNode extends CommandNode {

    private Class enumClass;

    private List<String> enumNames;

    private HashMap<String, Object> enumMap = new HashMap<>();

    public EnumNode(Class enumClass) {
        this.enumClass = enumClass;
        try {
            Object[] enums = (Object[]) enumClass.getMethod("values").invoke(null);
            Method nameMethod = enumClass.getMethod("name");
            ArrayList list = new ArrayList();
            for(Object o : enums)
                list.add(nameMethod.invoke(o));
            enumNames = list;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getRequiredArgsNum() {
        return 1;
    }

    @Override
    protected Object parseArgs(CommandSender sender, StringArgs args) {
        String name = args.next();
        if (!enumNames.contains(name)) {
            return null;
        }
        if (enumMap.containsKey(name)) {
            return enumMap.get(name);
        }
        try {
            Object o = enumClass.getMethod("valueOf", String.class).invoke(null, name);
            enumMap.put(name, o);
            return o;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Suggester getSuggester() {
        return (sender, command, args) -> enumNames;
    }

    @Override
    public int priority() {
        return 2;
    }

    @Override
    public boolean same(CommandNode node) {
        if (super.same(node) && node instanceof EnumNode) {
            return ((EnumNode) node).enumClass.equals(enumClass);
        }
        return false;
    }
}
