package io.codecrafters.shell;

import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class Main {

    public static void main(String[] args) {
        Shell.from(
                new InputStreamReader(System.in),
                System.out,
                System.err,
                path(System.getenv("HOME")),
                path(""),
                Stream.of(System.getenv("PATH").split(":"))
                    .map(Main::path)
                    .collect(Collectors.toSet())
            )
            .evaluationResult();
    }

    private static Path path(String raw) {
        return Paths.get(raw).normalize().toAbsolutePath();
    }
}
