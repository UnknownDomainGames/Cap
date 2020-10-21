package nullengine.command.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import nullengine.command.anno.*;
import nullengine.command.argument.Argument;
import nullengine.command.argument.ArgumentManager;
import nullengine.command.suggestion.SuggesterManager;
import nullengine.command.util.node.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommandNodeUtil {

    protected ArgumentManager argumentManager;
    protected SuggesterManager suggesterManager;

    private Multimap<Class, List<CommandNode>> provideCommandNodeMap = HashMultimap.create();

    private HashMap<String, List<CommandNode>> nameProvideCommandNodeMap = new HashMap<>();

    public static CommandNodeUtil getMethodUtil(ArgumentManager argumentManager, SuggesterManager suggesterManager) {
        return new CommandNodeUtil(argumentManager, suggesterManager);
    }

    public static ClassUtil getClassUtil(ArgumentManager argumentManager, SuggesterManager suggesterManager) {
        return new ClassUtil(argumentManager, suggesterManager);
    }

    private CommandNodeUtil(ArgumentManager argumentManager, SuggesterManager suggesterManager) {
        this.argumentManager = argumentManager;
        this.suggesterManager = suggesterManager;
    }

    public void addProvider(Object object) {
        Class providerClass = object.getClass();
        Arrays.stream(providerClass.getConstructors())
                .filter(constructor -> constructor.getAnnotation(Provide.class) != null)
                .filter(constructor -> constructor.getParameterCount() != 0)
                .forEach(constructor -> handleProvider(providerClass, (Provide) constructor.getAnnotation(Provide.class),
                        constructor.getParameters(), objects -> {
                            try {
                                constructor.setAccessible(true);
                                return constructor.newInstance(objects);
                            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }));
        Arrays.stream(providerClass.getMethods())
                .filter(method -> method.getAnnotation(Provide.class) != null)
                .filter(method -> method.getParameterCount() != 0)
                .forEach(method -> handleProvider(method.getReturnType(), method.getAnnotation(Provide.class),
                        method.getParameters(), objects -> {
                            try {
                                method.setAccessible(true);
                                return method.invoke(object, objects);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }));
    }

    private void handleProvider(Class providerClass, Provide provide, Parameter[] parameters, Function<Object[], Object> instanceFunction) {
        ArrayList<CommandNode> list = new ArrayList(constructMultiArgument(parameters, instanceFunction).stream()
                .map(CommandNodeUtil::getTopParent)
                .collect(Collectors.toSet()));
        if (provide.name() != null && !provide.name().isEmpty()) {
            if (nameProvideCommandNodeMap.containsKey(provide.name()))
                throw new RuntimeException("name of provider already exist.existed name: " + provide.name());
            nameProvideCommandNodeMap.put(provide.name(), list);
        }
        provideCommandNodeMap.put(providerClass, list);
    }

    public List<CommandNode> parseParameter(Parameter parameter) {
        Annotation[] annotations = parameter.getAnnotations();
        List<CommandNode> node = foundMainNode(parameter.getType(), annotations);
        setCustomAnnotation(node, annotations);
        return node;
    }

    public List<CommandNode> foundMainNode(Class clazz, Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == Sender.class) {
                Sender sender = (Sender) annotation;
                Class[] senderClass = sender.value();
                if (senderClass == null || senderClass.length == 0) {
                    return handleSender(clazz);
                } else {
                    return handleSender(senderClass);
                }
            } else if (annotation.annotationType() == Required.class) {
                return handleRequired(((Required) annotation).value());
            } else if (annotation.annotationType() == ArgumentHandler.class) {
                return handleArgumentName(((ArgumentHandler) annotation).value());
            } else if (annotation.annotationType() == UseProvide.class) {
                UseProvide useProvide = (UseProvide) annotation;
                List<String> provides = Arrays.asList(useProvide.value());
                for (String provide : provides)
                    if (!nameProvideCommandNodeMap.containsKey(provide)) {
                        throw new RuntimeException("provide: " + provide + " can not found");
                    }
                return cloneList(provides.stream()
                        .map(name -> nameProvideCommandNodeMap.get(name))
                        .collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll));
            }
        }
        List<CommandNode> nodeList = handleArgument(nullengine.command.util.ClassUtil.packing(clazz));
        if (!nodeList.isEmpty()) {
            return nodeList;
        }

        nodeList = foundNodesByProvider(clazz);

        if (!nodeList.isEmpty()) {
            return nodeList;
        }
        if (clazz.isEnum()) {
            return handleEnum(clazz);
        }

        throw new RuntimeException(clazz + " no argument or provide");
    }

    private List<CommandNode> cloneList(Collection<CommandNode> list) {
        List<CommandNode> cloneList = new ArrayList<>();
        list.forEach(node -> cloneList.add(node.clone()));
        return cloneList;
    }

    public List<CommandNode> handleSender(Class... classes) {
        return Lists.newArrayList(new SenderNode(classes));
    }

    public List<CommandNode> handleRequired(String required) {
        return Lists.newArrayList(new RequiredNode(required));
    }

    public List<CommandNode> handleArgumentName(String argumentName) {
        Argument argument = argumentManager.getArgument(argumentName);
        CommandNode node = new ArgumentNode(argument);
        node.setSuggester(suggesterManager.getSuggester(argument.responsibleClass()));
        return Lists.newArrayList(node);
    }

    public List<CommandNode> foundNodesByProvider(Class clazz) {
        if (!provideCommandNodeMap.containsKey(clazz))
            return Collections.EMPTY_LIST;
        return cloneList(provideCommandNodeMap.get(clazz).stream().collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll));
    }

    public List<CommandNode> handleArgument(Class clazz) {
        Argument argument = argumentManager.getArgument(clazz);
        if (argument == null) {
            return Collections.EMPTY_LIST;
        } else {
            CommandNode node = new ArgumentNode(argument);
            node.setSuggester(suggesterManager.getSuggester(clazz));
            return Lists.newArrayList(node);
        }
    }

    public List<CommandNode> constructMultiArgument(Parameter[] parameters, Function<Object[], Object> constructFunction) {
        List<CommandNode> nodes = parseParameter(parameters[0]);
        for (int i = 1; i < parameters.length; i++) {
            List<CommandNode> children = parseParameter(parameters[i]);
            List<CommandNode> cloneChildren = new ArrayList<>();
            for (CommandNode parent : nodes) {
                for (CommandNode child : children) {
                    CommandNode clone = CommandNodeUtil.getTopParent(child).clone();
                    cloneChildren.addAll(getAllBottomNode(clone));
                    parent.addChild(clone);
                }
            }
            nodes = cloneChildren;
        }
        for (int i = 0; i < nodes.size(); i++) {
            CommandNode node = nodes.get(i);
            MultiArgumentNode multiArgumentNode = new MultiArgumentNode(node, constructFunction, CommandNodeUtil.getDepth(node));
            if (node.getParent() != null) {
                CommandNode parent = node.getParent();
                parent.removeChild(node);
                parent.addChild(multiArgumentNode);
            }
            nodes.set(i, multiArgumentNode);
        }
        return nodes;
    }

    public List<CommandNode> handleEnum(Class enumClazz) {
        return Lists.newArrayList(new EnumNode(enumClazz));
    }

    public void setCustomAnnotation(List<CommandNode> nodes, Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == Suggester.class) {
                Suggester complete = (Suggester) annotation;
                nodes.stream().forEach(node -> node.setSuggester(suggesterManager.getSuggester(complete.value())));
            }

            if (annotation.annotationType() == Tip.class) {
                Tip tip = (Tip) annotation;
                nodes.stream().forEach(node -> node.setTip(tip.value()));
            }
        }
    }

    /**
     * 统计包括node及其所有父node所需的args.
     *
     * @param node
     * @return
     */
    public static int getRequiredArgsSumFromParent2Child(CommandNode node) {
        int i = 0;
        while (true) {
            if (node == null) {
                return i;
            }
            i += node.getRequiredArgsNum();
            if (node.getParent() == null) {
                return i;
            }
            node = node.getParent();
        }
    }

    public static CommandNode getTopParent(CommandNode child) {
        while (true) {
            if (child.getParent() == null) {
                return child;
            }
            child = child.getParent();
        }
    }

    public static int getDepth(CommandNode node) {
        if (node == null)
            return 0;
        int i = 0;
        while (node.getParent() != null) {
            i++;
            node = node.getParent();
        }
        return i;
    }

    public static List<CommandNode> getLinkedFromParent2Child(CommandNode child) {
        ArrayList<CommandNode> list = new ArrayList<>();
        list.add(child);
        while (true) {
            if (child.getParent() == null) {
                Collections.reverse(list);
                return list;
            }
            list.add(child.getParent());
            child = child.getParent();
        }
    }

    public static Collection<? extends CommandNode> getAllBottomNode(CommandNode clone) {
        ArrayList<CommandNode> list = new ArrayList<>();
        List<CommandNode> arrayList = new LinkedList<>();
        arrayList.add(clone);
        while (!arrayList.isEmpty()) {
            CommandNode node = arrayList.remove(0);
            if (node.getChildren().isEmpty()) {
                list.add(node);
            } else {
                arrayList.addAll(node.getChildren());
            }
        }
        return list;
    }


    /**
     * 返回从当前node到最近结束的node的路径.
     * 结束指可以执行命令
     * 不包含传入的node
     *
     * @param node
     * @return
     */
    public static List<CommandNode> getShortestPath(CommandNode node) {
        CommandNode node1 = breadthFirstSearch(node);
        List<CommandNode> path = new LinkedList<>();
        path.add(node1);
        while (true) {
            if (node1.getParent() == node) {
                return path;
            }
            path.add(0, node1.getParent());
            node1 = node1.getParent();
        }
    }

    private static CommandNode breadthFirstSearch(CommandNode commandNode) {
        List<CommandNode> arrayList = new LinkedList<>();
        arrayList.add(commandNode);
        while (!arrayList.isEmpty()) {
            CommandNode node = arrayList.remove(0);
            if (node.canExecuteCommand()) {
                return node;
            }
            arrayList.addAll(node.getChildren());
        }
        return null;
    }

    public static class ClassUtil extends CommandNodeUtil {

        public ClassUtil(ArgumentManager argumentManager, SuggesterManager suggesterManager) {
            super(argumentManager, suggesterManager);
        }

        public List<CommandNode> parseField(Field field) {
            Annotation[] annotations = field.getAnnotations();
            List<CommandNode> nodeList = new ArrayList<>();
            nodeList.addAll(foundMainNode(field.getType(), annotations));
            setCustomAnnotation(nodeList, annotations);
            return nodeList;
        }
    }

    public static void showLink(CommandNode commandNode) {

        ArrayList<CommandNode> list1 = new ArrayList<>();

        list1.add(commandNode);
        while (commandNode.getParent() != null) {
            list1.add(commandNode.getParent());
            commandNode = commandNode.getParent();
        }

        List<String> list2 = list1.stream().map(CommandNodeUtil::getNodeDescription).collect(Collectors.toList());
        Collections.reverse(list2);
        System.out.println(list2.toString());
    }

    public static String getNodeDescription(CommandNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(node.getClass().getSimpleName());
        sb.append("(" + Integer.toHexString(node.hashCode()) + ")");
        if (node instanceof ArgumentNode) {
            ArgumentNode argumentNode = (ArgumentNode) node;
            sb.append(":");
            sb.append(argumentNode.getArgument().getName());
        } else if (node instanceof SenderNode) {
            SenderNode senderNode = (SenderNode) node;
            sb.append(":");
            sb.append(Arrays.stream(senderNode.getAllowedSenders()).map(Class::getSimpleName).collect(Collectors.toList()));
        } else if (node instanceof RequiredNode) {
            RequiredNode requiredNode = (RequiredNode) node;
            sb.append(":");
            sb.append(requiredNode.getRequire());
        }
        return sb.toString();
    }
}
