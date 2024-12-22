package io.codecrafters.shell;

import java.io.PrintStream;
import java.nio.file.Path;

final class ExitCommandFactory implements BuiltInCommandFactory {

    @Override
    public BuiltIn type() {
        return new BuiltIn("exit");
    }

    @Override
    public Command command(PrintStream output, Path homeDirectory, Path workingDirectory) {
        return arguments -> new ExitCode(0);
    }
}
