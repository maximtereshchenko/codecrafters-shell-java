package io.codecrafters.shell;

import java.io.PrintStream;
import java.nio.file.Path;

final class PwdCommandFactory implements BuiltInCommandFactory {

    @Override
    public BuiltIn type() {
        return new BuiltIn("pwd");
    }

    @Override
    public Command command(PrintStream output, Path homeDirectory, Path workingDirectory) {
        return arguments -> {
            output.println(workingDirectory);
            return new NoExecutionResult();
        };
    }
}
