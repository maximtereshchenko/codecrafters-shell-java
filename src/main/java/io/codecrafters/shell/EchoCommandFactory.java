package io.codecrafters.shell;

import java.io.PrintStream;
import java.nio.file.Path;

final class EchoCommandFactory implements BuiltInCommandFactory {

    @Override
    public BuiltIn type() {
        return new BuiltIn("echo");
    }

    @Override
    public Command command(Path homeDirectory, Path workingDirectory, PrintStream output) {
        return arguments -> {
            output.println(String.join(" ", arguments));
            return new NoExecutionResult();
        };
    }
}
