package io.codecrafters.shell.iterator.expression;

import java.nio.file.Path;

public record ErrorRedirection(Expression expression, Path path) implements Expression {}
