package io.codecrafters.shell;

import java.io.PrintStream;
import java.util.Optional;

final class NotFoundCommandFactory implements CommandFactory {

    @Override
    public boolean hasName(String name) {
        return false;
    }

    @Override
    public Optional<Command> command(String name) {
        return Optional.of((output, arguments) -> printCommandNotFound(output, name));
    }

    private Optional<Integer> printCommandNotFound(PrintStream output, String name) {
        output.println(name + ": command not found");
        return Optional.empty();
    }
}
