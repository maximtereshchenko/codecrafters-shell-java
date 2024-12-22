package io.codecrafters.shell;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

final class CdCommandFactory implements BuiltInCommandFactory {

    @Override
    public BuiltIn type() {
        return new BuiltIn("cd");
    }

    @Override
    public Command command(PrintStream output, Path homeDirectory, Path workingDirectory) {
        return arguments -> {
            var path = workingDirectory.resolve(
                    arguments.getFirst().replace("~", homeDirectory.toString())
                )
                .normalize();
            if (!Files.exists(path)) {
                output.printf("cd: %s: No such file or directory%n", arguments.getFirst());
                return new NoExecutionResult();
            }
            return new WorkingDirectory(path);
        };
    }
}
