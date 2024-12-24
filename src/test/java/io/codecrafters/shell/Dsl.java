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
        return new Dsl(input, workingDirectory, homeDirectory, externalCommandLocations);
    }

    Result whenEvaluated() {
        var output = new ByteArrayOutputStream();
        var error = new ByteArrayOutputStream();
        try {
            return new Success(
                Shell.from(
                        new StringReader(input),
                        new PrintStream(output),
                        new PrintStream(error),
                        homeDirectory,
                        workingDirectory,
                        externalCommandLocations
                    )
                    .evaluationResult(),
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

    private List<String> lines(ByteArrayOutputStream stream) {
        return List.of(stream.toString(StandardCharsets.UTF_8).split(System.lineSeparator()));
    }

    interface Result {

        Result thenOutputContains(String... expected);

        void thenErrorContains(String... expected);

        Result thenFinishedWith(EvaluationResult evaluationResult);

        void thenOutputDoesNotContain(String notExpected);

        void thenErrorDoesNotContain(String notExpected);

        void thenNoExceptionThrown();
    }

    private static final class Success implements Result {

        private final EvaluationResult evaluationResult;
        private final List<String> output;
        private final List<String> error;

        Success(EvaluationResult evaluationResult, List<String> output, List<String> error) {
            this.evaluationResult = evaluationResult;
            this.output = output;
            this.error = error;
        }

        @Override
        public Result thenOutputContains(String... expected) {
            contains(output, expected);
            return this;
        }

        @Override
        public void thenErrorContains(String... expected) {
            contains(error, expected);
        }

        @Override
        public Result thenFinishedWith(EvaluationResult evaluationResult) {
            assertThat(this.evaluationResult).isEqualTo(evaluationResult);
            return this;
        }

        @Override
        public void thenOutputDoesNotContain(String notExpected) {
            doesNotContain(output, notExpected);
        }

        @Override
        public void thenErrorDoesNotContain(String notExpected) {
            doesNotContain(error, notExpected);
        }

        @Override
        public void thenNoExceptionThrown() {
            //empty
        }

        private void doesNotContain(List<String> lines, String notExpected) {
            assertThat(lines).noneMatch(line -> line.contains(notExpected));
        }

        private void contains(List<String> lines, String... expected) {
            var remainingElements = new ArrayList<>(List.of(expected));
            var remainingLines = new ArrayList<>(lines);
            var remainingElementsIterator = remainingElements.listIterator();
            while (remainingElementsIterator.hasNext()) {
                var element = remainingElementsIterator.next();
                var remainingLinesIterator = remainingLines.listIterator();
                while (remainingLinesIterator.hasNext()) {
                    var line = remainingLinesIterator.next();
                    if (line.contains(element)) {
                        remainingLinesIterator.remove();
                        remainingElementsIterator.remove();
                        break;
                    }
                }
            }
            assertThat(remainingElements)
                .describedAs("%s should contain all %s", lines, List.of(expected))
                .overridingErrorMessage("but missing %s", remainingElements)
                .isEmpty();
        }
    }

    private static final class Failure implements Result {

        private final Exception exception;

        private Failure(Exception exception) {
            this.exception = exception;
        }

        @Override
        public Result thenOutputContains(String... expected) {
            thenNoExceptionThrown();
            return this;
        }

        @Override
        public void thenErrorContains(String... expected) {
            thenOutputContains();
        }

        @Override
        public Result thenFinishedWith(EvaluationResult evaluationResult) {
            return thenOutputContains();
        }

        @Override
        public void thenOutputDoesNotContain(String notExpected) {
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
