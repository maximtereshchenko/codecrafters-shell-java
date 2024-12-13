package io.codecrafters.shell;

import java.util.Optional;

final class Exit implements CommandFactory {

    @Override
    public boolean hasName(String name) {
        return name.equals("exit");
    }

    @Override
    public Optional<Command> command(String name) {
        if (!hasName(name)) {
            return Optional.empty();
        }
        return Optional.of((output, arguments) -> Optional.of(0));
    }
}
