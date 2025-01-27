package io.codecrafters.shell;

import java.io.IOException;
import java.io.PrintStream;

record PreparedToFlush(FlushFunction flush) implements BufferingResult {

    @FunctionalInterface
    interface FlushFunction {

        FlushingResult apply(PrintStream output, PrintStream error) throws IOException;
    }
}
