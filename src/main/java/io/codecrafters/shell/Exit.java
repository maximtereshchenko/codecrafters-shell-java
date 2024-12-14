package io.codecrafters.shell;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

final class Exit implements BuiltInCommand {

    @Override
    public BuiltIn type() {
        return new BuiltIn("exit");
    }

    @Override
    public Optional<Integer> execute(PrintStream output, Path workingDirectory, List<String> arguments) {
        return Optional.of(0);
    }
}
