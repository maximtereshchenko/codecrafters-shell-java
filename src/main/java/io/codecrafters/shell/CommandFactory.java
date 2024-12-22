package io.codecrafters.shell;

import java.io.PrintStream;
import java.nio.file.Path;

interface CommandFactory {

    CommandType type();

    Command command(Path homeDirectory, Path workingDirectory, PrintStream output);
}
