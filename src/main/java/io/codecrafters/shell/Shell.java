package io.codecrafters.shell;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.*;

final class Shell {

    private final Scanner input;
    private final PrintStream output;
    private final Path initialWorkingDirectory;
    private final Path homeDirectory;
    private final Set<CommandFactory> commandFactories;

    Shell(Scanner input, PrintStream output, Path homeDirectory, Path initialWorkingDirectory, Set<Path> executableCommandDirectories) {
        this.input = input;
        this.output = output;
        this.initialWorkingDirectory = initialWorkingDirectory;
        this.homeDirectory = homeDirectory;
        this.commandFactories = commandFactories(executableCommandDirectories);
    }

    int evaluate() throws IOException {
        var workingDirectory = initialWorkingDirectory;
        while (true) {
            output.print("$ ");
            var tokens = tokens();
            if (tokens.isEmpty()) {
                return 0;
            }
            var name = tokens.getFirst();
            var command = command(name);
            if (command.isPresent()) {
                var executionResult = command.get()
                    .execute(output, homeDirectory, workingDirectory, tokens.subList(1, tokens.size()));
                if (executionResult instanceof ExitCode(int code)) {
                    return code;
                }
                if (executionResult instanceof WorkingDirectory(Path directory)) {
                    workingDirectory = directory;
                }
            } else {
                output.println(name + ": command not found");
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

    private List<String> tokens() {
        if (!input.hasNextLine()) {
            return List.of();
        }
        return List.of(input.nextLine().split("\\s+"));
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
