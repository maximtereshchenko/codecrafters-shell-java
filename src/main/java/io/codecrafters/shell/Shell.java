package io.codecrafters.shell;

import java.io.PrintStream;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

final class Shell {

    private final Scanner input;
    private final PrintStream output;
    private final LinkedHashSet<CommandFactory> commandFactories = commandFactories();

    Shell(Scanner input, PrintStream output) {
        this.input = input;
        this.output = output;
    }

    int execute() {
        while (input.hasNextLine()) {
            output.print("$ ");
            var tokens = List.of(input.nextLine().split("\\s+"));
            var exitCode = commandFactories.stream()
                .map(commandFactory -> commandFactory.command(tokens.getFirst()))
                .flatMap(Optional::stream)
                .findAny()
                .flatMap(command -> command.execute(output, tokens.subList(1, tokens.size())));
            if (exitCode.isPresent()) {
                return exitCode.get();
            }
        }
        return 0;
    }

    private LinkedHashSet<CommandFactory> commandFactories() {
        var all = new LinkedHashSet<CommandFactory>();
        all.add(new Echo());
        all.add(new Exit());
        all.add(new Type(Set.copyOf(all)));
        all.add(new NotFoundCommandFactory());
        return all;
    }
}
