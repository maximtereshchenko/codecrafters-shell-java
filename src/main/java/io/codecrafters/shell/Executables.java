package io.codecrafters.shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

final class Executables implements Location {

    private final Set<Path> directories;

    Executables(Set<Path> directories) {
        this.directories = directories;
    }

    @Override
    public Optional<CommandFactory> commandFactory(String name) throws IOException {
        for (var directory : directories) {
            if (Files.exists(directory)) {
                try (var stream = Files.list(directory)) {
                    var executableCommand = stream.filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString().equals(name))
                        .map(Path::normalize)
                        .map(Path::toAbsolutePath)
                        .map(ExecutableCommandFactory::new)
                        .findAny();
                    if (executableCommand.isPresent()) {
                        return Optional.of(executableCommand.get());
                    }
                }
            }
        }
        return Optional.empty();
    }
}
