package nullengine.command.util;

import nullengine.command.anno.*;
import nullengine.command.argument.Argument;
import nullengine.command.argument.ArgumentManager;
import nullengine.command.completion.CompleteManager;
import nullengine.command.anno.Complete;
import nullengine.command.util.node.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;

public class CommandNodeUtil {


    public static AnnotationUtil getAnnotationUtil(ArgumentManager argumentManager, CompleteManager completeManager) {
        return new AnnotationUtil(argumentManager, completeManager);
    }


    /**
     * 统计包括node及其所有父node所需的args.
     *
     * @param node
     * @return
     */
    public static int getNeedArgs(CommandNode node) {
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
    public static void addChildren(CommandNode node,CommandNode children){
        node.addChild(getTopParent(children));
    }

    private static CommandNode getTopParent(CommandNode child) {
        while (true){
            if(child.getParent()==null)
                return child;
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

        private AnnotationUtil(ArgumentManager argumentManager, CompleteManager completeManager) {
            this.argumentManager = argumentManager;
            this.completeManager = completeManager;
        }

        /**
         * 有可能返回一个带父Node的Node
         *
         * @param parameter
         * @return
         */
        public CommandNode parseParameter(Parameter parameter) {

            Annotation[] annotations = parameter.getAnnotations();

            CommandNode node = foundMainNode(parameter.getType(), annotations);
            setCustomAnnotation(node, annotations);

            return node;
        }

        public CommandNode foundMainNode(Class clazz, Annotation[] annotations) {
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

        public CommandNode handleSender(Class... classes) {
            return new SenderNode(classes);
        }

        public CommandNode handleRequired(String required) {
            return new RequiredNode(required);
        }

        public CommandNode handleArgumentName(String argumentName) {
            Argument argument = argumentManager.getArgument(argumentName);
            CommandNode node = new ArgumentNode(argument);
            node.setCompleter(completeManager.getCompleter(argument.responsibleClass()));
            return node;
        }

        public CommandNode handleArgument(Class clazz) {
            Argument argument = argumentManager.getArgument(clazz);
            if (argument == null) {
                CommandNode node = handleGenerator(clazz);
                if (node == null)
                    throw new RuntimeException("这个类没有注册进Argument或没有标记Generator");
                return node;
            } else {
                CommandNode node = new ArgumentNode(argument);
                node.setCompleter(completeManager.getCompleter(clazz));
                return node;
            }
        }

        public CommandNode handleGenerator(Class clazz) {
            Constructor[] constructors = clazz.getConstructors();

            for (Constructor constructor : constructors) {
                Generator generator = (Generator) constructor.getAnnotation(Generator.class);
                if (generator != null) {
                    Parameter[] parameters = constructor.getParameters();
                    if (parameters == null || parameters.length == 0)
                        throw new RuntimeException("不知道要不要支持没有形参的构建方法");
                    ArgumentNode node = (ArgumentNode) parseParameter(parameters[0]);
                    for (int i = 1; i < parameters.length; i++) {
                        ArgumentNode child = (ArgumentNode) parseParameter(parameters[i]);
                        addChildren(node,child);
                        node = child;
                    }
                    MultiArgumentNode multiArgumentNode = new MultiArgumentNode(node.getArgument(), objects -> {
                        try {
                            return constructor.newInstance(objects);
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }, CommandNodeUtil.getNeedArgs(node));

                    if (node.getParent() == null)
                        return multiArgumentNode;
                    else {
                        CommandNode parent = node.getParent();
                        parent.removeChild(node);
                        parent.addChild(multiArgumentNode);

                        return multiArgumentNode;
                    }
                }
            }

            return null;
        }


        public void setCustomAnnotation(CommandNode node, Annotation[] annotations) {
            for (Annotation annotation : annotations) {

                if (annotation.annotationType() == Completer.class) {
                    Complete complete = (Complete) annotation;
                    node.setCompleter(completeManager.getCompleter(complete.value()));
                }

                if (annotation.annotationType() == Permission.class) {
                    Permission permission = (Permission) annotation;
                    node.setNeedPermission(new HashSet<>(Arrays.asList(permission.value())));
                }

                if (annotation.annotationType() == Tip.class) {
                    Tip tip = (Tip) annotation;
                    node.setTip(tip.value());
                }
            }
        }


    }

    public static class InnerUtil extends AnnotationUtil {
        private InnerUtil(ArgumentManager argumentManager, CompleteManager completeManager) {
            super(argumentManager, completeManager);
        }
    }

}
