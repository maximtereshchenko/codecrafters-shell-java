package io.codecrafters.shell;

import io.codecrafters.shell.iterator.expression.Command;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

interface CommandFactory {

    Set<CommandType> commandTypes();

    Optional<ExecutableExpression> executableExpression(
        Path workingDirectory, Command command,
        ExecutableExpression downstream
    );
}
