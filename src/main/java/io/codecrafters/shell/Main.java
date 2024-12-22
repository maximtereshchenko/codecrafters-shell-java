package io.codecrafters.shell;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws IOException {
        Shell.from(
                new InputStreamReader(System.in),
                System.out,
                path(System.getenv("HOME")),
                path(""),
                executableLocations()
            )
            .evaluate();
    }

    private static Set<Path> executableLocations() {
        return Stream.of(System.getenv("PATH").split(":"))
            .map(Main::path)
            .collect(Collectors.toSet());
    }

    private static Path path(String raw) {
        return Paths.get(raw).normalize().toAbsolutePath();
    }
}
