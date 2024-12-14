package io.codecrafters.shell;

import java.nio.file.Path;

record WorkingDirectory(Path directory) implements ExecutionResult {}
