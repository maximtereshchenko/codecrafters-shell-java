package io.codecrafters.shell;

import io.codecrafters.shell.command.CommandFactory;
import io.codecrafters.shell.command.CommandType;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

final class Autocomplete {

    private final Set<CommandFactory> commandFactories;

    Autocomplete(Set<CommandFactory> commandFactories) {
        this.commandFactories = commandFactories;
    }

    LinkedHashSet<String> completions(String input) {
        return commandFactories.stream()
            .map(CommandFactory::commandTypes)
            .flatMap(Collection::stream)
            .map(CommandType::name)
            .filter(name -> name.startsWith(input))
            .sorted()
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
