package io.codecrafters.shell;

import java.util.Collection;
import java.util.Set;

final class Autocomplete {

    private static final String RING_BELL = "\u0007";
    
    private final Set<CommandFactory> commandFactories;

    Autocomplete(Set<CommandFactory> commandFactories) {
        this.commandFactories = commandFactories;
    }

    String complete(String input) {
        return commandFactories.stream()
            .map(CommandFactory::commandTypes)
            .flatMap(Collection::stream)
            .map(CommandType::name)
            .filter(name -> name.startsWith(input))
            .map(name -> name.substring(input.length()))
            .map(completed -> completed + " ")
            .findAny()
            .orElse(RING_BELL);
    }
}
