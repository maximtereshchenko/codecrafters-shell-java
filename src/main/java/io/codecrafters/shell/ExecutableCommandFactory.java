package io.codecrafters.shell;

import java.io.PrintStream;
import java.nio.file.Path;

final class ExecutableCommandFactory implements CommandFactory {

    private final Path path;

    ExecutableCommandFactory(Path path) {
        this.path = path;
    }

    @Override
    public CommandType type() {
        return new Executable(path);
    }

    @Override
    public Command command(Path homeDirectory, Path workingDirectory, PrintStream output) {
        return new ExecutableCommand(path, workingDirectory, output);
    }
}
