package io.codecrafters.shell;

import io.codecrafters.shell.iterator.expression.Command;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

final class ExternalCommandFactory implements CommandFactory {

    private final Set<Path> externalCommandLocations;

    ExternalCommandFactory(Set<Path> externalCommandLocations) {
        this.externalCommandLocations = externalCommandLocations;
    }

    @Override
    public Optional<CommandType> commandType(String name) {
        return path(name)
            .map(External::new);
    }

    @Override
    public Optional<ExecutableExpression> executableExpression(Path workingDirectory, Command command, ExecutableExpression downstream) {
        return path(command.name())
            .map(path -> fullCommand(path, command.arguments()))
            .map(fullCommand -> externalCommand(fullCommand, downstream));
    }

    private Optional<Path> path(String name) {
        return externalCommandLocations.stream()
            .filter(Files::exists)
            .map(this::files)
            .flatMap(Collection::stream)
            .filter(path -> path.getFileName().toString().equals(name))
            .findAny();
    }

    private Set<Path> files(Path path) {
        if (Files.isRegularFile(path)) {
            return Set.of(path);
        }
        try (var entries = Files.list(path)) {
            return entries.map(this::files)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private ExternalCommand externalCommand(List<String> command, ExecutableExpression downstream) {
        try {
            return new ExternalCommand(
                new ProcessBuilder()
                    .command(command)
                    .start(),
                downstream
            );
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private List<String> fullCommand(Path path, List<String> arguments) {
        var command = new ArrayList<String>();
        command.add(path.toString());
        command.addAll(arguments);
        return command;
    }
}
