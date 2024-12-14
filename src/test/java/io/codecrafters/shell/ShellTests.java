package io.codecrafters.shell;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

@ExtendWith(DslExtension.class)
final class ShellTests {

    @Test
    void givenNoInput_thenPromptPrinted(Dsl dsl) throws IOException {
        dsl.whenEvaluated()
            .thenOutputContains("$ ");
    }

    @Test
    void givenInvalidCommand_thenInvalidCommandMessagePrinted(Dsl dsl) throws IOException {
        dsl.givenInput("invalid_command")
            .whenEvaluated()
            .thenOutputContains("invalid_command: command not found");
    }

    @Test
    void givenMultipleCommand_thenEachCommandEvaluated(Dsl dsl) throws IOException {
        dsl.givenInput(
                """
                invalid_command_1
                invalid_command_2
                """
            )
            .whenEvaluated()
            .thenOutputContains("invalid_command_1: command not found")
            .thenOutputContains("invalid_command_2: command not found");
    }

    @Test
    void givenExitBuiltIn_thenShellExited(Dsl dsl) throws IOException {
        dsl.givenInput(
                """
                exit 0
                should_not_be_evaluated
                """
            )
            .whenEvaluated()
            .thenExitCodeIsZero()
            .thenOutputDoesNotContain("should_not_be_evaluated");
    }

    @Test
    void givenEchoBuiltIn_thenArgumentsPrinted(Dsl dsl) throws IOException {
        dsl.givenInput("echo first second")
            .whenEvaluated()
            .thenOutputContains("first second");
    }

    @Test
    void givenEchoBuiltIn_thenEchoNotPrinted(Dsl dsl) throws IOException {
        dsl.givenInput("echo first second")
            .whenEvaluated()
            .thenOutputDoesNotContain("echo");
    }

    @Test
    void givenTypeBuiltIn_thenExistingBuiltInTypePrinted(Dsl dsl) throws IOException {
        dsl.givenInput("type exit")
            .whenEvaluated()
            .thenOutputContains("exit is a shell builtin");
    }

    @Test
    void givenTypeBuiltIn_thenItsTypePrinted(Dsl dsl) throws IOException {
        dsl.givenInput("type type")
            .whenEvaluated()
            .thenOutputContains("type is a shell builtin");
    }

    @Test
    void givenTypeBuiltIn_thenNotFoundCommandPrinted(Dsl dsl) throws IOException {
        dsl.givenInput("type invalid_command")
            .whenEvaluated()
            .thenOutputContains("invalid_command: not found");
    }

    @Test
    void givenTypeBuiltIn_thenExecutableCommandPrinted(Dsl dsl, @TempDir Path directory) throws IOException {
        var executable = Files.createFile(directory.resolve("executable"));
        dsl.givenInput("type executable")
            .givenExecutionCommandDirectory(directory)
            .whenEvaluated()
            .thenOutputContains("executable is " + executable);
    }

    @Test
    void givenNotExistingExecutableDirectory_thenNoExceptionThrown(Dsl dsl) throws IOException {
        dsl.givenInput("invalid_command")
            .givenExecutionCommandDirectory(Paths.get("not-existing"))
            .whenEvaluated()
            .thenNoExceptionThrown();
    }

    @Test
    void givenExecutableCommand_thenCommandExecuted(Dsl dsl, @TempDir Path directory) throws IOException {
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
        dsl.givenInput("executable argument")
            .givenExecutionCommandDirectory(directory)
            .whenEvaluated()
            .thenOutputContains("command was executed with argument");
    }

    @Test
    void givenPwdBuiltIn_thenWorkingDirectoryPrinted(Dsl dsl, @TempDir Path directory) throws IOException {
        dsl.givenInput("pwd")
            .givenWorkingDirectory(directory)
            .whenEvaluated()
            .thenOutputContains(directory.toString());
    }

    @Test
    void givenExecutableCommandWithBuiltInName_thenBuiltInPrioritized(Dsl dsl, @TempDir Path directory) throws IOException {
        var pwd = Files.createFile(directory.resolve("pwd"));
        dsl.givenInput("type pwd")
            .givenExecutionCommandDirectory(directory)
            .whenEvaluated()
            .thenOutputContains("pwd is a shell builtin")
            .thenOutputDoesNotContain("pwd is " + pwd);
    }

    @Test
    void givenCdBuiltInWithAbsolutePath_thenWorkingDirectoryChanged(Dsl dsl, @TempDir Path directory) throws IOException {
        var nested = Files.createDirectory(directory.resolve("nested"));
        dsl.givenInput(
                """
                cd %s
                pwd
                """
                    .formatted(nested.toString())
            )
            .givenExecutionCommandDirectory(directory)
            .whenEvaluated()
            .thenOutputContains(nested.toString());
    }
}
