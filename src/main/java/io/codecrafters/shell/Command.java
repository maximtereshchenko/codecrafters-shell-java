package io.codecrafters.shell;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;

interface Command {

    CommandType type();

    ExecutionResult execute(
        PrintStream output,
        Path homeDirectory,
        Path workingDirectory,
        List<String> arguments
    ) throws IOException;
}
