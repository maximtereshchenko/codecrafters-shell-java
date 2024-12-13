package io.codecrafters.shell;

import java.util.Optional;

interface CommandFactory {

    Optional<Command> command(String name);
}
