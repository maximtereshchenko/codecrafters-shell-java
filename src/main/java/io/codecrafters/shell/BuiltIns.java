package io.codecrafters.shell;

import java.util.Optional;
import java.util.Set;

final class BuiltIns implements Location {

    private final Set<BuiltInCommandFactory> factories;

    BuiltIns(Set<BuiltInCommandFactory> factories) {
        this.factories = factories;
    }

    @Override
    public Optional<CommandFactory> commandFactory(String name) {
        for (var factory : factories) {
            if (factory.type().name().equals(name)) {
                return Optional.of(factory);
            }
        }
        return Optional.empty();
    }
}
