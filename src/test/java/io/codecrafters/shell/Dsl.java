package io.codecrafters.shell;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

final class Dsl {

    private final String input;
    private final Path workingDirectory;
    private final Path homeDirectory;
    private final Set<Path> executableCommandDirectories;

    private Dsl(String input, Path workingDirectory, Path homeDirectory, Set<Path> executableCommandDirectories) {
        this.input = input;
        this.workingDirectory = workingDirectory;
        this.homeDirectory = homeDirectory;
        this.executableCommandDirectories = executableCommandDirectories;
    }

    Dsl() {
        this("", Paths.get(""), Paths.get(""), Set.of());
    }

    Dsl givenInput(String input) {
        return new Dsl(input, workingDirectory, homeDirectory, executableCommandDirectories);
    }

    EvaluationResult whenEvaluated() throws IOException {
        var output = new ByteArrayOutputStream();
        try {
            return new Success(
                new Shell(
                    new Scanner(input),
                    new PrintStream(output),
                    homeDirectory, workingDirectory,
                    executableCommandDirectories
                )
                    .evaluate(),
                List.of(output.toString(StandardCharsets.UTF_8).split(System.lineSeparator()))
            );
        } catch (Exception e) {
            return new Failure(e);
        }
    }

    Dsl givenExecutionCommandDirectory(Path directory) {
        var copy = new HashSet<>(executableCommandDirectories);
        copy.add(directory);
        return new Dsl(input, workingDirectory, homeDirectory, copy);
    }

    Dsl givenWorkingDirectory(Path directory) {
        return new Dsl(input, directory, homeDirectory, executableCommandDirectories);
    }

    Dsl givenHomeDirectory(Path directory) {
        return new Dsl(input, workingDirectory, directory, executableCommandDirectories);
    }

    interface EvaluationResult {

        EvaluationResult thenOutputContains(String expected);

        EvaluationResult thenExitCodeIsZero();

        void thenOutputDoesNotContain(String notExpected);

        void thenNoExceptionThrown();
    }

    private static final class Success implements EvaluationResult {

        private final int exitCode;
        private final List<String> lines;

        Success(int exitCode, List<String> lines) {
            this.exitCode = exitCode;
            this.lines = lines;
        }

        @Override
        public EvaluationResult thenOutputContains(String expected) {
            assertThat(lines).anyMatch(line -> line.contains(expected));
            return this;
        }

        @Override
        public EvaluationResult thenExitCodeIsZero() {
            assertThat(exitCode).isZero();
            return this;
        }

        @Override
        public void thenOutputDoesNotContain(String notExpected) {
            assertThat(lines).noneMatch(line -> line.contains(notExpected));
        }

        @Override
        public void thenNoExceptionThrown() {
            //empty
        }
    }

    private static final class Failure implements EvaluationResult {

        private final Exception exception;

        private Failure(Exception exception) {
            this.exception = exception;
        }

        @Override
        public EvaluationResult thenOutputContains(String expected) {
            thenNoExceptionThrown();
            return this;
        }

        @Override
        public EvaluationResult thenExitCodeIsZero() {
            thenNoExceptionThrown();
            return this;
        }

        @Override
        public void thenOutputDoesNotContain(String notExpected) {
            thenNoExceptionThrown();
        }

        @Override
        public void thenNoExceptionThrown() {
            assertThat(exception).doesNotThrowAnyException();
        }
    }
}
