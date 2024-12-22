package io.codecrafters.shell.iterator.input;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public record Input(String name, List<String> arguments, Optional<Path> outputRedirection) {}
