package io.codecrafters.shell;

import io.codecrafters.shell.iterator.expression.Command;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

final class BuiltInCommandFactory implements CommandFactory {

    private final Map<String, BuiltInConstructor> constructors;

    private BuiltInCommandFactory(Map<String, BuiltInConstructor> constructors) {
        this.constructors = constructors;
    }

    static BuiltInCommandFactory from(LinkedHashSet<CommandFactory> commandFactories) {
        return new BuiltInCommandFactory(
            Map.of(
                "exit",
                (workingDirectory, arguments, downstream) ->
                    new ExitBuiltIn(downstream),
                "echo",
                (workingDirectory, arguments, downstream) ->
                    new EchoBuiltIn(arguments, downstream),
                "type",
                (workingDirectory, arguments, downstream) ->
                    new TypeBuiltIn(arguments.getFirst(), commandFactories, downstream),
                "pwd",
                (workingDirectory, arguments, downstream) ->
                    new PwdBuiltIn(workingDirectory, downstream),
                "cd",
                (workingDirectory, arguments, downstream) ->
                    new CdBuiltIn(
                        workingDirectory.resolve(arguments.getFirst())
                            .normalize()
                            .toAbsolutePath(),
                        downstream
                    )
            )
        );
    }

    @Override
    public Optional<CommandType> commandType(String name) {
        if (!constructors.containsKey(name)) {
            return Optional.empty();
        }
        return Optional.of(new BuiltIn(name));
    }

    @Override
    public Optional<ExecutableExpression> executableExpression(Path workingDirectory, Command command, ExecutableExpression downstream) {
        return Optional.ofNullable(constructors.get(command.name()))
            .map(constructor ->
                constructor.executableExpression(workingDirectory, command.arguments(), downstream)
            );
    }

    private interface BuiltInConstructor {

        ExecutableExpression executableExpression(
            Path workingDirectory,
            List<String> arguments,
            ExecutableExpression downstream
        );
    }
}
