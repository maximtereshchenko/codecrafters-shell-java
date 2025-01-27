package io.codecrafters.shell;

import java.util.LinkedHashSet;

record MultiplePossibleCompletions(LinkedHashSet<String> completions) implements AutocompletionResult {}
