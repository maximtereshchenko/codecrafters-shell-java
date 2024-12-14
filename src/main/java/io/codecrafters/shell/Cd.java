package io.codecrafters.shell;

import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

final class Cd implements BuiltInCommand {

    @Override
    public BuiltIn type() {
        return new BuiltIn("cd");
    }

    @Override
    public ExecutionResult execute(PrintStream output, Path workingDirectory, List<String> arguments) {
        return new WorkingDirectory(Paths.get(arguments.getFirst()));
    }
}
