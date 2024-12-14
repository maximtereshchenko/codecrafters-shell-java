package io.codecrafters.shell;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

final class Pwd implements BuiltInCommand {

    @Override
    public BuiltIn type() {
        return new BuiltIn("pwd");
    }

    @Override
    public Optional<Integer> execute(PrintStream output, Path workingDirectory, List<String> arguments) {
        output.println(workingDirectory);
        return Optional.empty();
    }
}
