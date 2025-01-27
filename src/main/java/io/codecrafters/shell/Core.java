package io.codecrafters.shell;

import io.codecrafters.shell.executableexpression.*;
import io.codecrafters.shell.token.Literal;
import io.codecrafters.shell.token.TokenFactory;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

final class Core {

    private final StringBuilder builder;
    private final Path workingDirectory;
    private final TokenFactory tokenFactory;
    private final ExecutableExpressionFactory executableExpressionFactory;
    private final Autocomplete autocomplete;

    private Core(
        StringBuilder builder,
        Path workingDirectory, TokenFactory tokenFactory,
        ExecutableExpressionFactory executableExpressionFactory,
        Autocomplete autocomplete
    ) {
        this.builder = builder;
        this.workingDirectory = workingDirectory;
        this.tokenFactory = tokenFactory;
        this.executableExpressionFactory = executableExpressionFactory;
        this.autocomplete = autocomplete;
    }

    Core(Path workingDirectory, TokenFactory tokenFactory, ExecutableExpressionFactory executableExpressionFactory, Autocomplete autocomplete) {
        this(new StringBuilder(), workingDirectory, tokenFactory, executableExpressionFactory, autocomplete);
    }

    BufferingResult buffered(char character) {
        var copy = new StringBuilder(builder).append(character);
        if (copy.toString().endsWith(System.lineSeparator())) {
            return new PreparedToFlush((output, error) -> flush(copy, output, error));
        }
        return new Buffered(withBuffer(copy));
    }

    AutocompletionResult autocompleted() {
        if (tokenFactory.tokens(builder).getLast() instanceof Literal(var value)) {
            var completions = autocomplete.completions(value);
            if (completions.size() == 1) {
                var completion = completions.getFirst();
                return new Autocompleted(completion, withBuffer(new StringBuilder(builder).append(completion)));
            }
            return new MultiplePossibleCompletions(completions);
        }
        return new Unchanged();
    }

    private FlushingResult flush(CharSequence charSequence, PrintStream output, PrintStream error) throws IOException {
        return executableExpressionFactory.executableExpression(
                workingDirectory,
                charSequence,
                new Sink(output, error)
            )
            .map(executableExpression ->
                switch (executableExpression.onEnd()) {
                    case ExitInitiated(var exitCode) -> new Exited(exitCode);
                    case Completed() -> new Flushed(withEmptyBuffer());
                    case WorkingDirectoryChanged(var path) -> new Flushed(withWorkingDirectory(path));
                }
            )
            .orElseGet(() -> new Flushed(withEmptyBuffer()));
    }

    private Core withBuffer(StringBuilder builder) {
        return new Core(builder, workingDirectory, tokenFactory, executableExpressionFactory, autocomplete);
    }

    private Core withEmptyBuffer() {
        return new Core(workingDirectory, tokenFactory, executableExpressionFactory, autocomplete);
    }

    private Core withWorkingDirectory(Path workingDirectory) {
        return new Core(workingDirectory, tokenFactory, executableExpressionFactory, autocomplete);
    }
}
