package io.codecrafters.shell;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws IOException {
        new Shell(new Scanner(System.in), System.out, executableCommandDirectories()).evaluate();
    }

    private static Set<Path> executableCommandDirectories() {
        return Stream.of(System.getenv("PATH").split(":"))
            .map(Paths::get)
            .map(Path::normalize)
            .map(Path::toAbsolutePath)
            .collect(Collectors.toSet());
    }
}
