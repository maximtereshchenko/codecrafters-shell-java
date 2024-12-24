package io.codecrafters.shell;

sealed interface ExecutionResult permits Exit, NoExecutionResult, WorkingDirectory {

    ExecutionResult orElse(ExecutionResult executionResult);
}
