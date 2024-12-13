package io.codecrafters.shell;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

final class ShellTests {

    @Test
    void givenNoInput_thenPromptPrinted() {
        assertThat(executionResult("").output()).startsWith("$ ");
    }

    @Test
    void givenInvalidCommand_thenInvalidCommandMessagePrinted() {
        assertThat(executionResult("invalid_command").output()).contains("invalid_command: command not found");
    }

    @Test
    void givenMultipleCommand_thenEachCommandEvaluated() {
        assertThat(
            executionResult("""
                            invalid_command_1
                            invalid_command_2
                            """)
                .output()
        )
            .contains("invalid_command_1: command not found", "invalid_command_2: command not found");
    }

    @Test
    void givenExitBuiltin_thenShellExited() {
        var executionResult = executionResult("""
                                              exit 0
                                              should_not_be_evaluated
                                              """);
        assertThat(executionResult.exitCode()).isZero();
        assertThat(executionResult.output()).doesNotContain("should_not_be_evaluated");
    }

    @Test
    void givenEchoBuiltin_thenArgumentsPrinted() {
        assertThat(executionResult("echo first second").output()).contains("first second");
    }

    @Test
    void givenEchoBuiltin_thenEchoNotPrinted() {
        assertThat(executionResult("echo first second").output()).doesNotContain("echo");
    }

    @Test
    void givenTypeBuiltin_thenExistingBuiltinTypePrinted() {
        assertThat(executionResult("type exit").output()).contains("exit is a shell builtin");
    }

    @Test
    void givenTypeBuiltin_thenItsTypePrinted() {
        assertThat(executionResult("type type").output()).contains("type is a shell builtin");
    }

    @Test
    void givenTypeBuiltin_thenNotFoundCommandPrinted() {
        assertThat(executionResult("type invalid_command").output()).contains("invalid_command: not found");
    }

    private ExecutionResult executionResult(String input) {
        var output = new ByteArrayOutputStream();
        var exitCode = new Shell(new Scanner(input), new PrintStream(output)).execute();
        var string = output.toString(StandardCharsets.UTF_8);
        return new ExecutionResult(exitCode, string);
    }

    private record ExecutionResult(int exitCode, String output) {}
}
