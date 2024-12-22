package io.codecrafters.shell;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

final class ExecutableCommand implements Command {

    private final Path path;
    private final Path workingDirectory;
    private final PrintStream output;

    ExecutableCommand(Path path, Path workingDirectory, PrintStream output) {
        this.path = path;
        this.workingDirectory = workingDirectory;
        this.output = output;
    }

    @Override
    public ExecutionResult execute(List<String> arguments) throws IOException {
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
            .directory(workingDirectory.toFile())
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectErrorStream(true)
            .start();
        process.getInputStream().transferTo(output);
        return process;
    }

    private List<String> fullCommand(List<String> arguments) {
        var command = new ArrayList<String>();
        command.add(path.toString());
        command.addAll(arguments);
        return command;
    }
}
