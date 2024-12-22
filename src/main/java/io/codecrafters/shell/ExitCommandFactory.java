package io.codecrafters.shell;

import java.io.PrintStream;
import java.nio.file.Path;

final class ExitCommandFactory implements BuiltInCommandFactory {

    @Override
    public BuiltIn type() {
        return new BuiltIn("exit");
    }

    @Override
    public Command command(Path homeDirectory, Path workingDirectory, PrintStream output) {
        return arguments -> new ExitCode(0);
    }
}
