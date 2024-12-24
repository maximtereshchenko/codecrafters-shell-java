package io.codecrafters.shell;

import java.nio.file.Path;

record External(Path path) implements CommandType {}
