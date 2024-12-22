package io.codecrafters.shell;

import io.codecrafters.shell.iterator.CharacterIterator;
import io.codecrafters.shell.iterator.input.Input;
import io.codecrafters.shell.iterator.input.InputIterator;
import io.codecrafters.shell.iterator.token.TokenIterator;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.file.Files;
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

    private Shell(
        Iterator<Input> inputIterator,
        PrintStream output,
        Path homeDirectory,
        Path initialWorkingDirectory,
        Set<Path> executableLocations
    ) {
        this.inputIterator = inputIterator;
        this.output = output;
        this.initialWorkingDirectory = initialWorkingDirectory;
        this.homeDirectory = homeDirectory;
        this.locations = locations(executableLocations);
    }

    static Shell from(
        Reader reader,
        PrintStream output,
        Path homeDirectory,
        Path workingDirectory,
        Set<Path> executableLocations
    ) {
        return new Shell(
            new InputIterator(new TokenIterator(new CharacterIterator(reader))),
            output,
            homeDirectory,
            workingDirectory,
            executableLocations
        );
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
                var executionResult = executionResult(commandFactory.get(), workingDirectory, input);
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

    private ExecutionResult executionResult(CommandFactory commandFactory, Path workingDirectory, Input input) throws IOException {
        var outputRedirection = input.outputRedirection();
        if (outputRedirection.isEmpty()) {
            return commandFactory.command(homeDirectory, workingDirectory, output)
                .execute(input.arguments());
        }
        //TODO refactor
        try (var fileOutput = new PrintStream(Files.newOutputStream(workingDirectory.resolve(outputRedirection.get())))) {
            return commandFactory.command(homeDirectory, workingDirectory, fileOutput)
                .execute(input.arguments());
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
