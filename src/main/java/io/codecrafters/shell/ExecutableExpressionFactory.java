package io.codecrafters.shell;

import io.codecrafters.shell.iterator.expression.Command;
import io.codecrafters.shell.iterator.expression.ErrorRedirection;
import io.codecrafters.shell.iterator.expression.Expression;
import io.codecrafters.shell.iterator.expression.OutputRedirection;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

final class ExecutableExpressionFactory {

    private final Set<CommandFactory> commandFactories;
    private final Sink sink;

    ExecutableExpressionFactory(Set<CommandFactory> commandFactories, Sink sink) {
        this.commandFactories = commandFactories;
        this.sink = sink;
    }

    ExecutableExpression executableExpression(Path workingDirectory, Expression expression) {
        try {
            return executableExpression(workingDirectory, expression, sink);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private ExecutableExpression executableExpression(
        Path workingDirectory,
        Expression expression,
        ExecutableExpression downstream
    ) throws IOException {
        return switch (expression) {
            case Command command -> command(workingDirectory, command, downstream);
            case OutputRedirection(var redirected, var path) -> executableExpression(
                workingDirectory,
                redirected,
                path,
                printStream -> new OutputRedirectionExpression(printStream, downstream)
            );
            case ErrorRedirection(var redirected, var path) -> executableExpression(
                workingDirectory,
                redirected,
                path,
                printStream -> new ErrorRedirectionExpression(printStream, downstream)
            );
        };
    }

    private ExecutableExpression executableExpression(
        Path workingDirectory,
        Expression redirected,
        Path path,
        Function<PrintStream, ExecutableExpression> function
    ) throws IOException {
        return executableExpression(
            workingDirectory,
            redirected,
            function.apply(new PrintStream(Files.newOutputStream(workingDirectory.resolve(path))))
        );
    }

    private ExecutableExpression command(Path workingDirectory, Command command, ExecutableExpression downstream) {
        return commandFactories.stream()
            .map(factory -> factory.executableExpression(workingDirectory, command, downstream))
            .flatMap(Optional::stream)
            .findAny()
            .orElseGet(() -> new CommandNotFound(command.name(), downstream));
    }
}
