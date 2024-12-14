package io.codecrafters.shell;

import java.io.PrintStream;
import java.nio.file.Files;
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
        var path = Paths.get(arguments.getFirst());
        if (!Files.exists(path)) {
            output.printf("cd: %s: No such file or directory%n", path);
            return new NoExecutionResult();
        }
        return new WorkingDirectory(path);
    }
}
