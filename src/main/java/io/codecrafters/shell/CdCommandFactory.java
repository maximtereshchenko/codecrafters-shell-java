package io.codecrafters.shell;

import java.io.PrintStream;
import java.nio.file.Path;

final class CdCommandFactory implements BuiltInCommandFactory {

    @Override
    public BuiltIn type() {
        return new BuiltIn("cd");
    }

    @Override
    public Command command(Path homeDirectory, Path workingDirectory, PrintStream output) {
        return new Cd(homeDirectory, workingDirectory, output);
    }
}
