package io.codecrafters.shell;

import java.nio.file.Path;

record External(Path path) implements CommandType {

    @Override
    public String name() {
        return path.getFileName().toString();
    }
}
