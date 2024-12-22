package io.codecrafters.shell;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

final class Cd implements Command {

    private final Path homeDirectory;
    private final Path workingDirectory;
    private final PrintStream output;

    Cd(Path homeDirectory, Path workingDirectory, PrintStream output) {
        this.homeDirectory = homeDirectory;
        this.workingDirectory = workingDirectory;
        this.output = output;
    }

    @Override
    public ExecutionResult execute(List<String> arguments) {
        var path = workingDirectory.resolve(
                arguments.getFirst().replace("~", homeDirectory.toString())
            )
            .normalize();
        if (!Files.exists(path)) {
            output.printf("cd: %s: No such file or directory%n", arguments.getFirst());
            return new NoExecutionResult();
        }
        return new WorkingDirectory(path);
    }
}
