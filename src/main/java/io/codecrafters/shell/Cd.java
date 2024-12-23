package io.codecrafters.shell;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

final class Cd implements BuiltInCommand {

    @Override
    public BuiltIn type() {
        return new BuiltIn("cd");
    }

    @Override
    public ExecutionResult execute(
        PrintStream output,
        Path homeDirectory,
        Path workingDirectory,
        List<String> arguments
    ) {
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
