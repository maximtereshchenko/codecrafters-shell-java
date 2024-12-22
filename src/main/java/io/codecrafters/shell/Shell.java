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
    private final Set<Location> locations;

    Shell(
        Iterator<Input> inputIterator,
        PrintStream output,
        Path homeDirectory,
        Path initialWorkingDirectory,
        Set<Path> executableDirectories
    ) {
        this.inputIterator = inputIterator;
        this.output = output;
        this.initialWorkingDirectory = initialWorkingDirectory;
        this.homeDirectory = homeDirectory;
        this.locations = locations(executableDirectories);
    }

    int evaluate() throws IOException {
        var workingDirectory = initialWorkingDirectory;
        while (true) {
            output.print("$ ");
            if (!inputIterator.hasNext()) {
                return 0;
            }
            var input = inputIterator.next();
            var commandFactory = commandFactory(input.name());
            if (commandFactory.isPresent()) {
                var executionResult = commandFactory.get()
                    .command(homeDirectory, workingDirectory, output)
                    .execute(input.arguments());
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

    private Set<Location> locations(Set<Path> directories) {
        var set = new LinkedHashSet<Location>();
        set.add(
            new BuiltIns(
                Set.of(
                    new EchoCommandFactory(),
                    new PwdCommandFactory(),
                    new CdCommandFactory(),
                    new TypeCommandFactory(set),
                    new ExitCommandFactory()
                )
            )
        );
        set.add(new Executables(directories));
        return set;
    }

    private Optional<CommandFactory> commandFactory(String name) throws IOException {
        for (var location : locations) {
            var commandFactory = location.commandFactory(name);
            if (commandFactory.isPresent()) {
                return commandFactory;
            }
        }
        return Optional.empty();
    }
}
