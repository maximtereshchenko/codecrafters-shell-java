package io.codecrafters.shell;

final class NoExecutionResult implements ExecutionResult {

    @Override
    public ExecutionResult orElse(ExecutionResult executionResult) {
        return executionResult;
    }
}
