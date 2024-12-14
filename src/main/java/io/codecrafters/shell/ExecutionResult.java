package io.codecrafters.shell;

sealed interface ExecutionResult permits ExitCode, NoExecutionResult, WorkingDirectory {}
