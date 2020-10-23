package main.swing;

import engine.command.argument.Argument;
import engine.command.suggestion.Suggester;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EntityArgument extends Argument {
    @Override
    public String getName() {
        return "Entity";
    }

    @Override
    public Class responsibleClass() {
        return SwingEntity.class;
    }

    @Override
    public Optional parse(String arg) {
        return Optional.ofNullable(SwingTest.getInstance().getEntityManager().getEntity(arg));
    }

    @Override
    public Suggester getSuggester() {
        return (sender, command, args) -> {
            List<String> entityNames = SwingTest.getInstance().getEntityManager().getEntities().stream().map(SwingEntity::getName).collect(Collectors.toList());
            if(args!=null&&!args[args.length-1].isEmpty())
                return entityNames.stream().filter(name->name.startsWith(args[args.length-1])).collect(Collectors.toList());
            return entityNames;
        };
    }
}
