package io.codecrafters.shell;

import java.io.PrintStream;
import java.nio.file.Path;

final class EchoCommandFactory implements BuiltInCommandFactory {

    @Override
    public BuiltIn type() {
        return new BuiltIn("echo");
    }

    @Override
    public Command command(PrintStream output, Path homeDirectory, Path workingDirectory) {
        return arguments -> {
            output.println(String.join(" ", arguments));
            return new NoExecutionResult();
        };
    }
}
