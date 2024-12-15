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
        new Shell(
            new Inputs(new Tokens(new Characters(new InputStreamReader(System.in)))),
            System.out,
            path(System.getenv("HOME")), path(""),
            executableCommandDirectories()
        )
            .evaluate();
    }

    private static Set<Path> executableCommandDirectories() {
        return Stream.of(System.getenv("PATH").split(":"))
            .map(Main::path)
            .collect(Collectors.toSet());
    }

    private static Path path(String raw) {
        return Paths.get(raw).normalize().toAbsolutePath();
    }
}
