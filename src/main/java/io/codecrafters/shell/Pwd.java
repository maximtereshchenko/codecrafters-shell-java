package io.codecrafters.shell;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;

final class Pwd implements BuiltInCommand {

    @Override
    public BuiltIn type() {
        return new BuiltIn("pwd");
    }

    @Override
    public ExecutionResult execute(
        PrintStream output,
        Path homeDirectory,
        Path workingDirectory,
        List<String> arguments
    ) {
        output.println(workingDirectory);
        return new NoExecutionResult();
    }
}
