package io.codecrafters.shell;

import java.io.IOException;
import java.util.Optional;

interface Location {

    Optional<CommandFactory> commandFactory(String name) throws IOException;
}
