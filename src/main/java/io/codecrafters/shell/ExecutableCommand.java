package io.codecrafters.shell;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

final class ExecutableCommand implements Command {

    private final Path path;

    ExecutableCommand(Path path) {
        this.path = path;
    }

    @Override
    public CommandType type() {
        return new Executable(path);
    }

    @Override
    public ExecutionResult execute(
        PrintStream output,
        Path homeDirectory,
        Path workingDirectory,
        List<String> arguments
    ) throws IOException {
        var process = process(output, arguments);
        wait(process);
        return new NoExecutionResult();
    }

    private void wait(Process process) {
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Process process(PrintStream output, List<String> arguments) throws IOException {
        var process = new ProcessBuilder(fullCommand(arguments))
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectErrorStream(true)
            .start();
        process.getInputStream().transferTo(output);
        return process;
    }

    private List<String> fullCommand(List<String> arguments) {
        return Stream.concat(
                Stream.of(path.toString()),
                arguments.stream()
            )
            .toList();
    }
}
