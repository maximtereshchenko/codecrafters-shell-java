package io.codecrafters.shell;

import java.io.PrintStream;
import java.util.List;
import java.util.Optional;

final class Exit implements Command {

    @Override
    public BuiltIn type() {
        return new BuiltIn("exit");
    }

    @Override
    public Optional<Integer> execute(PrintStream output, List<String> arguments) {
        return Optional.of(0);
    }
}
