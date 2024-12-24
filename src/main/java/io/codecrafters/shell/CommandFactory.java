package io.codecrafters.shell;

import io.codecrafters.shell.iterator.expression.Command;

import java.nio.file.Path;
import java.util.Optional;

interface CommandFactory {

    Optional<CommandType> commandType(String name);

    Optional<ExecutableExpression> executableExpression(
        Path workingDirectory, Command command,
        ExecutableExpression downstream
    );
}
