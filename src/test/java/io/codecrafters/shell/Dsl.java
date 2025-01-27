package io.codecrafters.shell;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

final class Dsl {

    private final String input;
    private final Path workingDirectory;
    private final Path homeDirectory;
    private final Set<Path> externalCommandLocations;

    private Dsl(String input, Path workingDirectory, Path homeDirectory, Set<Path> externalCommandLocations) {
        this.input = input;
        this.workingDirectory = workingDirectory;
        this.homeDirectory = homeDirectory;
        this.externalCommandLocations = externalCommandLocations;
    }

    Dsl(Path path) {
        this("", path, path, Set.of());
    }

    Dsl givenInput(String input) {
        return new Dsl(withLineSeparator(input), workingDirectory, homeDirectory, externalCommandLocations);
    }

    Result whenEvaluated() {
        var output = new ByteArrayOutputStream();
        var error = new ByteArrayOutputStream();
        try {
            return new Success(
                Shell.from(
                        homeDirectory,
                        workingDirectory,
                        new StringReader(input),
                        new PrintStream(output),
                        new PrintStream(error),
                        externalCommandLocations
                    )
                    .exitCode(),
                lines(output),
                lines(error)
            );
        } catch (Exception e) {
            return new Failure(e);
        }
    }

    Dsl givenExternalCommandLocation(Path directory) {
        var copy = new HashSet<>(externalCommandLocations);
        copy.add(directory);
        return new Dsl(input, workingDirectory, homeDirectory, copy);
    }

    Dsl givenWorkingDirectory(Path directory) {
        return new Dsl(input, directory, homeDirectory, externalCommandLocations);
    }

    Dsl givenHomeDirectory(Path directory) {
        return new Dsl(input, workingDirectory, directory, externalCommandLocations);
    }

    private String withLineSeparator(String input) {
        if (input.endsWith(System.lineSeparator())) {
            return input;
        }
        return input + System.lineSeparator();
    }

    private List<String> lines(ByteArrayOutputStream stream) {
        return List.of(stream.toString(StandardCharsets.UTF_8).split(System.lineSeparator()));
    }

    interface Result {

        Result thenOutputContainsLines(String... expected);

        void thenErrorContainsLines(String... expected);

        Result thenFinishedWith(int exitCode);

        void thenOutputDoesNotContainLine(String notExpected);

        void thenErrorDoesNotContain(String notExpected);

        void thenNoExceptionThrown();
    }

    private static final class Success implements Result {

        private final int exitCode;
        private final List<String> output;
        private final List<String> error;

        Success(int exitCode, List<String> output, List<String> error) {
            this.exitCode = exitCode;
            this.output = output;
            this.error = error;
        }

        @Override
        public Result thenOutputContainsLines(String... expected) {
            containsLines(output, expected);
            return this;
        }

        @Override
        public void thenErrorContainsLines(String... expected) {
            containsLines(error, expected);
        }

        @Override
        public Result thenFinishedWith(int exitCode) {
            assertThat(this.exitCode).isEqualTo(exitCode);
            return this;
        }

        @Override
        public void thenOutputDoesNotContainLine(String notExpected) {
            doesNotContainLine(output, notExpected);
        }

        @Override
        public void thenErrorDoesNotContain(String notExpected) {
            doesNotContainLine(error, notExpected);
        }

        @Override
        public void thenNoExceptionThrown() {
            //empty
        }

        private void doesNotContainLine(List<String> lines, String notExpected) {
            assertThat(lines).noneMatch(line -> line.equals(notExpected));
        }

        private void containsLines(List<String> actual, String... expectedLines) {
            var remainingActual = new ArrayList<>(actual);
            var unmatchedExpected = new ArrayList<String>();
            for (var expected : expectedLines) {
                var index = remainingActual.indexOf(expected);
                if (index == -1) {
                    unmatchedExpected.add(expected);
                } else {
                    remainingActual.remove(index);
                }
            }
            assertThat(unmatchedExpected)
                .overridingErrorMessage("%s should contain all %s, but is missing %s", actual, List.of(expectedLines), unmatchedExpected)
                .isEmpty();
        }
    }

    private static final class Failure implements Result {

        private final Exception exception;

        private Failure(Exception exception) {
            this.exception = exception;
        }

        @Override
        public Result thenOutputContainsLines(String... expected) {
            thenNoExceptionThrown();
            return this;
        }

        @Override
        public void thenErrorContainsLines(String... expected) {
            thenOutputContainsLines();
        }

        @Override
        public Result thenFinishedWith(int exitCode) {
            return thenOutputContainsLines();
        }

        @Override
        public void thenOutputDoesNotContainLine(String notExpected) {
            thenNoExceptionThrown();
        }

        @Override
        public void thenErrorDoesNotContain(String notExpected) {
            thenNoExceptionThrown();
        }

        @Override
        public void thenNoExceptionThrown() {
            assertThat(exception).doesNotThrowAnyException();
        }
    }
}
