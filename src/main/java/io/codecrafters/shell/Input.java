package io.codecrafters.shell;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

record Input(String name, List<String> arguments, Optional<Path> outputRedirection) {}
