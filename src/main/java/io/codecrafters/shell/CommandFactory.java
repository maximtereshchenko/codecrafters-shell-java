package io.codecrafters.shell;

import java.util.Optional;

interface CommandFactory {

    boolean hasName(String name);

    Optional<Command> command(String name);
}
