package io.codecrafters.shell.iterator.expression;

import java.nio.file.Path;

public record RedirectionExpression(Expression expression, Source source, Path path, Mode mode)
    implements Expression {

    public enum Source {

        OUTPUT, ERROR
    }

    public enum Mode {

        OVERWRITE, APPEND
    }
}
