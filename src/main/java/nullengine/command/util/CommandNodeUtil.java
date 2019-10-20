package nullengine.command.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import nullengine.command.anno.*;
import nullengine.command.argument.Argument;
import nullengine.command.argument.ArgumentManager;
import nullengine.command.completion.CompleteManager;
import nullengine.command.util.node.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;

public class CommandNodeUtil {

    private Object instance;

    public static AnnotationUtil getAnnotationUtil(Object instance, ArgumentManager argumentManager, CompleteManager completeManager) {
        return new AnnotationUtil(instance, argumentManager, completeManager);
    }

    public static InnerUtil getInnerUtil(Object instance, ArgumentManager argumentManager, CompleteManager completeManager) {
        return new InnerUtil(instance, argumentManager, completeManager);
    }

    public CommandNodeUtil(Object instance) {
        this.instance = instance;
    }

    public Object getInstance() {
        return instance;
    }

    /**
     * 统计包括node及其所有父node所需的args.
     *
     * @param node
     * @return
     */
    public static int getTotalNeedArgs(CommandNode node) {
        int i = 0;
        while (true) {
            if (node.getParent() == null)
                return i;
            i += node.getNeedArgs();
            node = node.getParent();
        }
    }


    /**
     * 将children的最顶端加入到node的子类中
     *
     * @param node
     * @param children
     */
    public static void addChildren(CommandNode node, CommandNode children) {
        node.addChild(getTopParent(children));
    }

    private static CommandNode getTopParent(CommandNode child) {
        while (true) {
            if (child.getParent() == null)
                return child;
            child = child.getParent();
        }
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

    /**
     * 返回从当前node到最近结束的node的路径.
     * 结束指没有child
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
            if (node1.getParent() == node)
                return path;
            path.add(0, node1.getParent());
            node1 = node1.getParent();
        }
    }

    private static CommandNode breadthFirstSearch(CommandNode commandNode) {
        List<CommandNode> arrayList = new LinkedList<>();
        arrayList.add(commandNode);
        while (!arrayList.isEmpty()) {
            CommandNode node = arrayList.remove(0);
            if (node.canExecuteCommand())
                return node;
            arrayList.addAll(node.getChildren());
        }
        return null;
    }

    public static class AnnotationUtil extends CommandNodeUtil {

        private ArgumentManager argumentManager;
        private CompleteManager completeManager;

        public AnnotationUtil(Object instance, ArgumentManager argumentManager, CompleteManager completeManager) {
            super(instance);
            this.argumentManager = argumentManager;
            this.completeManager = completeManager;
        }

        /**
         * 有可能返回一个带父Node的Node
         *
         * @param parameter
         * @return
         */
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
                    if (senderClass == null || senderClass.length == 0)
                        return handleSender(clazz);
                    else return handleSender(senderClass);
                } else if (annotation.annotationType() == Required.class) {
                    return handleRequired(((Required) annotation).value());
                } else if (annotation.annotationType() == ArgumentHandler.class) {
                    return handleArgumentName(((ArgumentHandler) annotation).value());
                }
            }
            return handleArgument(ClassUtil.packing(clazz));
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
            node.setCompleter(completeManager.getCompleter(argument.responsibleClass()));
            return Lists.newArrayList(node);
        }

        public List<CommandNode> handleArgument(Class clazz) {
            Argument argument = argumentManager.getArgument(clazz);
            if (argument == null) {
                List<CommandNode> node = handleGenerator(clazz);
                if (node == null || node.isEmpty())
                    throw new RuntimeException("这个类没有注册进Argument或没有标记Generator");
                return node;
            } else {
                CommandNode node = new ArgumentNode(argument);
                node.setCompleter(completeManager.getCompleter(clazz));
                return Lists.newArrayList(node);
            }
        }

        public List<CommandNode> handleGenerator(Class clazz) {
            Constructor[] constructors = clazz.getConstructors();

            ArrayList<CommandNode> list = new ArrayList<>();

            for (Constructor constructor : constructors) {
                Generator generator = (Generator) constructor.getAnnotation(Generator.class);
                if (generator != null) {
                    Parameter[] parameters = constructor.getParameters();
                    if (parameters == null || parameters.length == 0)
                        throw new RuntimeException("不知道要不要支持没有形参的构造方法");
                    List<CommandNode> nodes = constructMultiArgument(parameters, objects -> {
                        try {
                            return constructor.newInstance(objects);
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        return null;
                    });
                    list.addAll(nodes);
                }
            }

            return list;
        }

        public List<CommandNode> constructMultiArgument(Parameter[] parameters, Function<Object[], Object> constructFunction) {
            List<CommandNode> nodes = parseParameter(parameters[0]);
            for (int i = 1; i < parameters.length; i++) {
                List<CommandNode> children = parseParameter(parameters[i]);

                for (CommandNode parent : nodes) {
                    for (CommandNode child : children) {
                        addChildren(parent, child);
                    }
                }
                nodes = children;
            }
            for (int i = 0; i < nodes.size(); i++) {
                CommandNode node = nodes.get(i);
                MultiArgumentNode multiArgumentNode = new MultiArgumentNode(node, constructFunction, CommandNodeUtil.getTotalNeedArgs(node));
                if (node.getParent() != null) {
                    CommandNode parent = node.getParent();
                    parent.removeChild(node);
                    parent.addChild(multiArgumentNode);
                }
                nodes.set(i, multiArgumentNode);
            }
            return nodes;
        }

        public void setCustomAnnotation(List<CommandNode> nodes, Annotation[] annotations) {
            for (Annotation annotation : annotations) {

                if (annotation.annotationType() == Completer.class) {
                    Completer complete = (Completer) annotation;
                    nodes.stream().forEach(node -> node.setCompleter(completeManager.getCompleter(complete.value())));
                }

                if (annotation.annotationType() == Tip.class) {
                    Tip tip = (Tip) annotation;
                    nodes.stream().forEach(node -> node.setTip(tip.value()));
                }
            }
        }


    }

    public static class InnerUtil extends AnnotationUtil {

        private Multimap<String, ProvideWrapper> providerMap = HashMultimap.create();

        public InnerUtil(Object instance, ArgumentManager argumentManager, CompleteManager completeManager) {
            super(instance, argumentManager, completeManager);
            handleInnerCommand(instance);
        }

        private void handleInnerCommand(Object instance) {
            Method[] methods = instance.getClass().getMethods();

            for (Method method : methods) {
                Provide provide = method.getAnnotation(Provide.class);
                if (provide != null)
                    providerMap.put(provide.value(), new ProvideWrapper(provide, method));
            }
        }

        public List<CommandNode> parseField(Field field) {

            Annotation[] annotations = field.getAnnotations();
            List<CommandNode> nodeList = new ArrayList<>();
            if (providerMap.containsKey(field.getName())) {
                Collection<ProvideWrapper> wrapper = providerMap.get(field.getName());
                if (checkProvider(field, wrapper))
                    throw new RuntimeException("provider 方法错误 其返回的对象不是目标Field的类或它的子类");
                ProvideWrapper replaceProvide = wrapper.stream().filter(provideWrapper -> provideWrapper.provide.replace()).findAny().orElse(null);
                if (replaceProvide != null) {
                    nodeList = constructMultiArgument(replaceProvide.method.getParameters(), objects -> {
                        try {
                            return replaceProvide.method.invoke(getInstance(), objects);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        return null;
                    });
                    setCustomAnnotation(nodeList, annotations);
                    return nodeList;
                }
                for (ProvideWrapper provideWrapper : wrapper) {
                    nodeList.addAll(constructMultiArgument(provideWrapper.method.getParameters(), objects -> {
                        try {
                            return replaceProvide.method.invoke(getInstance(), objects);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }));
                }
            }

            nodeList.addAll(foundMainNode(field.getType(), annotations));

            setCustomAnnotation(nodeList, annotations);

            return nodeList;
        }

        private boolean checkProvider(Field field, Collection<ProvideWrapper> wrapper) {
            Class fieldClass = field.getType();
            for (ProvideWrapper provideWrapper : wrapper) {
                if (!fieldClass.isAssignableFrom(provideWrapper.method.getReturnType())) {
                    return false;
                }
            }
            return true;
        }

        private class ProvideWrapper {

            public final Provide provide;
            public final Method method;

            public ProvideWrapper(Provide provide, Method method) {
                this.provide = provide;
                this.method = method;
            }
        }

    }

}
