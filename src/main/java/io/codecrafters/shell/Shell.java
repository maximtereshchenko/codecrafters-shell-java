package io.codecrafters.shell;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.*;

final class Shell {

    private final Scanner input;
    private final PrintStream output;
    private final Set<CommandFactory> commandFactories;

    Shell(Scanner input, PrintStream output, Set<Path> executableCommandDirectories) {
        this.input = input;
        this.output = output;
        this.commandFactories = commandFactories(executableCommandDirectories);
    }

    int evaluate() throws IOException {
        while (true) {
            output.print("$ ");
            var tokens = tokens();
            if (tokens.isEmpty()) {
                return 0;
            }
            var name = tokens.getFirst();
            var command = command(name);
            if (command.isPresent()) {
                var exitCode = command.get().execute(output, tokens.subList(1, tokens.size()));
                if (exitCode.isPresent()) {
                    return exitCode.get();
                }
            } else {
                output.println(name + ": command not found");
            }
        }
    }

    private Set<CommandFactory> commandFactories(Set<Path> executableCommandDirectories) {
        var set = new HashSet<CommandFactory>();
        set.add(
            new BuiltInCommandFactory(
                Set.of(
                    new Echo(),
                    new Exit(),
                    new Type(set)
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
