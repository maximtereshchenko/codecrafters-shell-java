package io.codecrafters.shell;

import java.util.Optional;
import java.util.Set;

final class BuiltInCommandFactory implements CommandFactory {

    private final Set<BuiltInCommand> builtIns;

    BuiltInCommandFactory(Set<BuiltInCommand> builtIns) {
        this.builtIns = builtIns;
    }

    @Override
    public Optional<Command> command(String name) {
        for (var builtIn : builtIns) {
            if (builtIn.type().name().equals(name)) {
                return Optional.of(builtIn);
            }
        }
        return Optional.empty();
    }
}
