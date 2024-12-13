package io.codecrafters.shell;

import java.io.PrintStream;
import java.util.List;
import java.util.Optional;

final class Echo implements CommandFactory {

    @Override
    public boolean hasName(String name) {
        return name.equals("echo");
    }

    @Override
    public Optional<Command> command(String name) {
        if (!hasName(name)) {
            return Optional.empty();
        }
        return Optional.of(this::printArguments);
    }

    private Optional<Integer> printArguments(PrintStream output, List<String> arguments) {
        output.println(String.join(" ", arguments));
        return Optional.empty();
    }
}
