package io.codecrafters.shell;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;

interface Command {

    CommandType type();

    Optional<Integer> execute(PrintStream output, List<String> arguments) throws IOException;
}
