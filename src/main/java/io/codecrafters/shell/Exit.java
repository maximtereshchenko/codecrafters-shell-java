package io.codecrafters.shell;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;

final class Exit implements BuiltInCommand {

    @Override
    public BuiltIn type() {
        return new BuiltIn("exit");
    }

    @Override
    public ExecutionResult execute(PrintStream output, Path workingDirectory, List<String> arguments) {
        return new ExitCode(0);
    }
}
