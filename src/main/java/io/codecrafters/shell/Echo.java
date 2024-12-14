package io.codecrafters.shell;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

final class Echo implements BuiltInCommand {

    @Override
    public BuiltIn type() {
        return new BuiltIn("echo");
    }

    @Override
    public Optional<Integer> execute(PrintStream output, Path workingDirectory, List<String> arguments) {
        output.println(String.join(" ", arguments));
        return Optional.empty();
    }
}
