package io.codecrafters.shell;

final class Exit implements ExecutionResult {

    @Override
    public ExecutionResult orElse(ExecutionResult executionResult) {
        return this;
    }
}
