package io.codecrafters.shell;

import java.io.PrintStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

final class Type implements Command {

    private final Set<CommandFactory> other;

    Type(Set<CommandFactory> other) {
        this.other = other;
    }

    @Override
    public BuiltIn type() {
        return new BuiltIn("type");
    }

    @Override
    public Optional<Integer> execute(PrintStream output, List<String> arguments) {
        var name = arguments.getFirst();
        output.println(
            name +
            Stream.concat(
                    commands(name),
                    Stream.of(this)
                )
                .map(Command::type)
                .map(BuiltIn::name)
                .filter(commandName -> commandName.equals(name))
                .findAny()
                .map(commandFactory -> " is a shell builtin")
                .orElse(": not found")
        );
        return Optional.empty();
    }

    private Stream<Command> commands(String name) {
        return other.stream()
            .map(commandFactory -> commandFactory.command(name))
            .flatMap(Optional::stream);
    }
}
