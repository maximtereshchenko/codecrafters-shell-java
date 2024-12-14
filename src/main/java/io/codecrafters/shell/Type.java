package io.codecrafters.shell;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

final class Type implements BuiltInCommand {

    private final LinkedHashSet<CommandFactory> other;

    Type(LinkedHashSet<CommandFactory> other) {
        this.other = other;
    }

    @Override
    public BuiltIn type() {
        return new BuiltIn("type");
    }

    @Override
    public ExecutionResult execute(PrintStream output, Path workingDirectory, List<String> arguments) throws IOException {
        output.println(description(arguments.getFirst()));
        return new NoExecutionResult();
    }

    private String description(String name) throws IOException {
        for (var commandFactory : other) {
            var description = commandFactory.command(name)
                .map(Command::type)
                .flatMap(type -> description(type, name));
            if (description.isPresent()) {
                return description.get();
            }
        }
        return name + ": not found";
    }

    private Optional<String> description(CommandType type, String name) {
        return switch (type) {
            case BuiltIn builtIn -> description(builtIn, name);
            case Executable executable -> description(executable, name);
        };
    }

    private Optional<String> description(BuiltIn builtIn, String name) {
        if (!builtIn.name().equals(name)) {
            return Optional.empty();
        }
        return Optional.of(builtIn.name() + " is a shell builtin");
    }

    private Optional<String> description(Executable executable, String name) {
        var executableName = executable.path().getFileName().toString();
        if (!executableName.equals(name)) {
            return Optional.empty();
        }
        return Optional.of("%s is %s".formatted(executableName, executable.path()));
    }
}
