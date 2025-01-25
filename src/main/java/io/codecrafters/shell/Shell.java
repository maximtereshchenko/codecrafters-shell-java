package io.codecrafters.shell;

import io.codecrafters.shell.iterator.CharacterIterator;
import io.codecrafters.shell.iterator.InputBufferingIterator;
import io.codecrafters.shell.iterator.expression.Expression;
import io.codecrafters.shell.iterator.expression.ExpressionIterator;
import io.codecrafters.shell.iterator.token.TokenIterator;

import java.io.PrintStream;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

final class Shell {

    private final Iterator<Expression> expressionIterator;
    private final Path initialWorkingDirectory;
    private final ExecutableExpressionFactory factory;
    private final PrintStream output;

    Shell(
        Iterator<Expression> expressionIterator,
        Path initialWorkingDirectory,
        ExecutableExpressionFactory factory,
        PrintStream output
    ) {
        this.expressionIterator = expressionIterator;
        this.initialWorkingDirectory = initialWorkingDirectory;
        this.factory = factory;
        this.output = output;
    }

    static Shell from(
        Reader reader,
        PrintStream output,
        PrintStream error,
        Path homeDirectory,
        Path workingDirectory,
        Set<Path> externalCommandLocations
    ) {
        var commandFactories = new LinkedHashSet<CommandFactory>();
        commandFactories.add(BuiltInCommandFactory.from(commandFactories));
        commandFactories.add(new ExternalCommandFactory(externalCommandLocations));
        var autocomplete = new Autocomplete(commandFactories);
        return new Shell(
            new ExpressionIterator(
                new TokenIterator(
                    homeDirectory,
                    new InputBufferingIterator(
                        new CharacterIterator(reader),
                        output,
                        homeDirectory,
                        autocomplete::complete
                    )
                )
            ),
            workingDirectory,
            new ExecutableExpressionFactory(commandFactories, new Sink(output, error)),
            output
        );
    }

    EvaluationResult evaluationResult() {
        var workingDirectory = initialWorkingDirectory;
        do {
            output.print("$ ");   //TODO centralized output
            if (!expressionIterator.hasNext()) {
                return EvaluationResult.SUCCESS;
            }
            try (var expression = factory.executableExpression(workingDirectory, expressionIterator.next())) {
                switch (expression.onEnd()) {
                    case Exit ignored -> {
                        return EvaluationResult.FAILURE;
                    }
                    case NoExecutionResult ignored -> {
                        //empty
                    }
                    case WorkingDirectory(var path) -> workingDirectory = path;
                }
            }
        } while (true);
    }
}
