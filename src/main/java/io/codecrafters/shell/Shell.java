package io.codecrafters.shell;

import io.codecrafters.shell.command.BuiltInCommandFactory;
import io.codecrafters.shell.command.CommandFactory;
import io.codecrafters.shell.command.ExternalCommandFactory;
import io.codecrafters.shell.executableexpression.ExecutableExpressionFactory;
import io.codecrafters.shell.expression.ExpressionFactory;
import io.codecrafters.shell.token.TokenFactory;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

final class Shell {

    private final Path homeDirectory;
    private final Path workingDirectory;
    private final Reader reader;
    private final PrintStream output;
    private final PrintStream error;
    private final LinkedHashSet<CommandFactory> commandFactories;

    private Shell(
        Path homeDirectory,
        Path workingDirectory,
        Reader reader,
        PrintStream output,
        PrintStream error,
        LinkedHashSet<CommandFactory> commandFactories
    ) {
        this.homeDirectory = homeDirectory;
        this.workingDirectory = workingDirectory;
        this.reader = reader;
        this.output = output;
        this.error = error;
        this.commandFactories = commandFactories;
    }

    static Shell from(
        Path homeDirectory,
        Path workingDirectory,
        Reader reader,
        PrintStream output,
        PrintStream error,
        Set<Path> externalCommandLocations
    ) {
        var commandFactories = new LinkedHashSet<CommandFactory>();
        commandFactories.add(BuiltInCommandFactory.from(commandFactories));
        commandFactories.add(new ExternalCommandFactory(externalCommandLocations));
        return new Shell(homeDirectory, workingDirectory, reader, output, error, commandFactories);
    }

    int exitCode() throws IOException {
        var core = core();
        prompt();
        for (var read = reader.read(); read != -1; read = reader.read()) {
            var character = (char) read;
            if (character == '\t') {
                core = autocompleted(core);
            } else {
                output.print(character);
                switch (afterBuffering(core.buffered(character))) {
                    case Continue(var nextCore) -> core = nextCore;
                    case Exit(var exitCode) -> {
                        return exitCode;
                    }
                }
            }
        }
        return 0;
    }

    private Core core() {
        var tokenFactory = new TokenFactory(homeDirectory);
        return new Core(
            workingDirectory,
            tokenFactory,
            new ExecutableExpressionFactory(
                commandFactories,
                new ExpressionFactory(
                    tokenFactory
                )
            ),
            new Autocomplete(commandFactories)
        );
    }

    private Core autocompleted(Core core) {
        switch (core.autocompleted()) {
            case Autocompleted autocompleted -> {
                output.printf("%s ", autocompleted.completed());
                return autocompleted.core();
            }
            case MultiplePossibleCompletions(var completions) -> output.println(String.join("  ", completions)); //TODO
            case Unchanged() -> ringBell();
        }
        return core;
    }

    private AfterBuffering afterBuffering(BufferingResult bufferingResult) throws IOException {
        return switch (bufferingResult) {
            case Buffered buffered -> new Continue(buffered.core());
            case PreparedToFlush(var flush) -> switch (flush.apply(output, error)) {
                case Exited exited -> new Exit(exited.exitCode());
                case Flushed flushed -> {
                    prompt();
                    yield new Continue(flushed.core());
                }
            };
        };
    }

    private void prompt() {
        output.print("$ ");
    }

    private void ringBell() {
        output.print('\u0007');
    }

    private sealed interface AfterBuffering {}

    private record Continue(Core core) implements AfterBuffering {}

    private record Exit(int exitCode) implements AfterBuffering {}
}
