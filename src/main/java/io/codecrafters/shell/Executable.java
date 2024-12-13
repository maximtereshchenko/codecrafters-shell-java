package io.codecrafters.shell;

import java.nio.file.Path;

record Executable(Path path) implements CommandType {}
