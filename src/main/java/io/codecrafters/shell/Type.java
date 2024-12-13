package io.codecrafters.shell;

import java.io.PrintStream;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

final class Type implements CommandFactory {

    private final Set<CommandFactory> otherCommandFactories;

    Type(Set<CommandFactory> otherCommandFactories) {
        this.otherCommandFactories = otherCommandFactories;
    }

    @Override
    public boolean hasName(String name) {
        return name.equals("type");
    }

    @Override
    public Optional<Command> command(String name) {
        if (!hasName(name)) {
            return Optional.empty();
        }
        return Optional.of((output, arguments) -> printType(output, arguments.getFirst()));
    }

    private Optional<Integer> printType(PrintStream output, String name) {
        output.println(
            name +
                Stream.concat(
                        otherCommandFactories.stream(),
                        Stream.of(this)
                    )
                    .filter(commandFactory -> commandFactory.hasName(name))
                    .findAny()
                    .map(commandFactory -> " is a shell builtin")
                    .orElse(": not found")
        );
        return Optional.empty();
    }
}
