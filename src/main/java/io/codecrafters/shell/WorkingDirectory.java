package io.codecrafters.shell;

import java.nio.file.Path;

record WorkingDirectory(Path path) implements ExecutionResult {

    @Override
    public ExecutionResult orElse(ExecutionResult executionResult) {
        return this;
    }
}
