package io.codecrafters.shell;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

final class ExecutableCommand implements Command {

    private final Path path;

    ExecutableCommand(Path path) {
        this.path = path;
    }

    @Override
    public CommandType type() {
        return new Executable(path);
    }

    @Override
    public Optional<Integer> execute(PrintStream output, List<String> arguments) {
        return Optional.empty();
    }
}
