package io.codecrafters.shell;

import java.io.PrintStream;
import java.util.List;
import java.util.Optional;

interface Command {

    Optional<Integer> execute(PrintStream output, List<String> arguments);
}