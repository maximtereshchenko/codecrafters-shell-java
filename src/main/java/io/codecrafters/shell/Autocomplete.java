package io.codecrafters.shell;

import io.codecrafters.shell.command.CommandFactory;
import io.codecrafters.shell.command.CommandType;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

final class Autocomplete {

    private final Set<CommandFactory> commandFactories;

    private Autocomplete(Set<CommandFactory> commandFactories) {
        this.commandFactories = commandFactories;
    }

    static Autocomplete from(Set<CommandFactory> commandFactories) {
        var autocomplete = new Autocomplete(commandFactories);
        for (var i = 0; i < 30; i++) {
            //need to warm up the JVM to pass tests in codecrafters.io
            autocomplete.completions("");
        }
        return autocomplete;
    }

    TreeSet<String> completions(String input) {
        return commandFactories.stream()
            .map(CommandFactory::commandTypes)
            .flatMap(Collection::stream)
            .map(CommandType::name)
            .filter(name -> name.startsWith(input))
            .collect(Collectors.toCollection(TreeSet::new));
    }
}
