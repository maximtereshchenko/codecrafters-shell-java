package io.codecrafters.shell;

import java.io.IOException;
import java.util.List;

interface Command {

    ExecutionResult execute(List<String> arguments) throws IOException;
}
