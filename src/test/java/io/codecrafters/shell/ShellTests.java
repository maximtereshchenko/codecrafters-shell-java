package io.codecrafters.shell;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Scanner;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

final class ShellTests {

    @Test
    void givenNoInput_thenPromptPrinted() throws IOException {
        assertThat(executionResult("").output()).startsWith("$ ");
    }

    @Test
    void givenInvalidCommand_thenInvalidCommandMessagePrinted() throws IOException {
        assertThat(executionResult("invalid_command").output()).contains("invalid_command: command not found");
    }

    @Test
    void givenMultipleCommand_thenEachCommandEvaluated() throws IOException {
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
    void givenExitBuiltin_thenShellExited() throws IOException {
        var executionResult = executionResult(
            """
            exit 0
            should_not_be_evaluated
            """
        );
        assertThat(executionResult.exitCode()).isZero();
        assertThat(executionResult.output()).doesNotContain("should_not_be_evaluated");
    }

    @Test
    void givenEchoBuiltin_thenArgumentsPrinted() throws IOException {
        assertThat(executionResult("echo first second").output()).contains("first second");
    }

    @Test
    void givenEchoBuiltin_thenEchoNotPrinted() throws IOException {
        assertThat(executionResult("echo first second").output()).doesNotContain("echo");
    }

    @Test
    void givenTypeBuiltin_thenExistingBuiltinTypePrinted() throws IOException {
        assertThat(executionResult("type exit").output()).contains("exit is a shell builtin");
    }

    @Test
    void givenTypeBuiltin_thenItsTypePrinted() throws IOException {
        assertThat(executionResult("type type").output()).contains("type is a shell builtin");
    }

    @Test
    void givenTypeBuiltin_thenNotFoundCommandPrinted() throws IOException {
        assertThat(executionResult("type invalid_command").output()).contains("invalid_command: not found");
    }

    @Test
    void givenTypeBuiltin_thenExecutableCommandPrinted(@TempDir Path directory) throws IOException {
        var executable = Files.createFile(directory.resolve("executable"));
        assertThat(executionResult("type executable", directory).output()).contains("executable is " + executable);
    }

    @Test
    void givenNotExistingExecutableDirectory_thenNoExceptionThrown() {
        assertThatCode(() -> executionResult("invalid_command", Paths.get("not-existing"))).doesNotThrowAnyException();
    }

    @Test
    void givenExecutableCommand_thenCommandExecuted(@TempDir Path directory) throws IOException {
        var executable = Files.createFile(
            directory.resolve("executable"),
            PosixFilePermissions.asFileAttribute(
                Set.of(
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE,
                    PosixFilePermission.OWNER_EXECUTE
                )
            )
        );
        Files.writeString(executable, "echo command was executed with $1");
        assertThat(executionResult("executable argument", directory).output()).contains("command was executed with argument");
    }

    private ExecutionResult executionResult(String input, Path... executableCommandDirectories) throws IOException {
        var output = new ByteArrayOutputStream();
        var exitCode = new Shell(new Scanner(input), new PrintStream(output), Set.of(executableCommandDirectories)).execute();
        var string = output.toString(StandardCharsets.UTF_8);
        return new ExecutionResult(exitCode, string);
    }

    private record ExecutionResult(int exitCode, String output) {}
}
