package io.codecrafters.shell;

import java.io.IOException;
import java.util.Optional;

interface CommandFactory {

    Optional<Command> command(String name) throws IOException;
}
