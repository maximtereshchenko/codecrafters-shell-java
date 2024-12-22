package io.codecrafters.shell;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.LinkedHashSet;

final class TypeCommandFactory implements BuiltInCommandFactory {

    private final LinkedHashSet<Location> locations;

    TypeCommandFactory(LinkedHashSet<Location> locations) {
        this.locations = locations;
    }

    @Override
    public BuiltIn type() {
        return new BuiltIn("type");
    }

    @Override
    public Command command(Path homeDirectory, Path workingDirectory, PrintStream output) {
        return new Type(locations, output);
    }
}
