package io.codecrafters.shell;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

final class Shell {

    private final Iterator<Input> inputIterator;
    private final PrintStream output;
    private final Path initialWorkingDirectory;
    private final Path homeDirectory;
    private final Set<CommandFactory> commandFactories;

    Shell(
        Iterator<Input> inputIterator,
        PrintStream output,
        Path homeDirectory,
        Path initialWorkingDirectory,
        Set<Path> executableCommandDirectories
    ) {
        this.inputIterator = inputIterator;
        this.output = output;
        this.initialWorkingDirectory = initialWorkingDirectory;
        this.homeDirectory = homeDirectory;
        this.commandFactories = commandFactories(executableCommandDirectories);
    }

    int evaluate() throws IOException {
        var workingDirectory = initialWorkingDirectory;
        while (true) {
            output.print("$ ");
            if (!inputIterator.hasNext()) {
                return 0;
            }
            var input = inputIterator.next();
            var command = command(input.name());
            if (command.isPresent()) {
                var executionResult = command.get()
                    .execute(output, homeDirectory, workingDirectory, input.arguments());
                if (executionResult instanceof ExitCode(int code)) {
                    return code;
                }
                if (executionResult instanceof WorkingDirectory(Path directory)) {
                    workingDirectory = directory;
                }
            } else {
                output.println(input.name() + ": command not found");
            }
        }
    }

    private Set<CommandFactory> commandFactories(Set<Path> executableCommandDirectories) {
        var set = new LinkedHashSet<CommandFactory>();
        set.add(
            new BuiltInCommandFactory(
                Set.of(
                    new Echo(),
                    new Pwd(),
                    new Cd(),
                    new Type(set),
                    new Exit()
                )
            )
        );
        set.add(new ExecutableCommandFactory(executableCommandDirectories));
        return set;
    }

    private Optional<Command> command(String name) throws IOException {
        for (var commandFactory : commandFactories) {
            var command = commandFactory.command(name);
            if (command.isPresent()) {
                return command;
            }
        }
        return Optional.empty();
    }
}
