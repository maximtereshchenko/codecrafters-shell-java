package io.codecrafters.shell;

import java.util.LinkedHashSet;

record MultiplePossibleCompletions(Core core, LinkedHashSet<String> completions) implements AutocompletionResult {}
