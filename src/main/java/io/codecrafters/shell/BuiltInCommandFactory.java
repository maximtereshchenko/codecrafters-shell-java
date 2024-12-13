package io.codecrafters.shell;

import java.util.Optional;
import java.util.Set;

final class BuiltInCommandFactory implements CommandFactory {

    private final Set<Command> builtIns;

    BuiltInCommandFactory(Set<Command> builtIns) {
        this.builtIns = builtIns;
    }

    @Override
    public Optional<Command> command(String name) {
        return builtIns.stream()
            .filter(command -> command.type().name().equals(name))
            .findAny();
    }
}
