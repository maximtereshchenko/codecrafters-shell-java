package io.codecrafters.shell;

import java.io.PrintStream;
import java.util.*;

final class Shell {

    private final Scanner input;
    private final PrintStream output;
    private final Set<CommandFactory> commandFactories = commandFactories();

    Shell(Scanner input, PrintStream output) {
        this.input = input;
        this.output = output;
    }

    int execute() {
        while (true) {
            output.print("$ ");
            var tokens = tokens();
            if (tokens.isEmpty()) {
                return 0;
            }
            var name = tokens.getFirst();
            var command = commandFactories.stream()
                .map(commandFactory -> commandFactory.command(name))
                .flatMap(Optional::stream)
                .findAny();
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

    private Set<CommandFactory> commandFactories() {
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
        return set;
    }

    private List<String> tokens() {
        if (!input.hasNextLine()) {
            return List.of();
        }
        return List.of(input.nextLine().split("\\s+"));
    }
}
